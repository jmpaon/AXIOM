/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package axiom.model;

import axiom.probabilityAdjusters.ProbabilityAdjuster;
import axiom.probabilityAdjusters.ProbabilityAdjustmentException;
import axiom.probabilityAdjusters.ProbabilityAdjustmentFunction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.Set;
import java.util.TreeSet;

/**
 *
 * @author juha
 */
public class Model implements LabelNamespace {
    
    public final String name;
    public final ComponentAdder add;
    public ComponentFinder find;
    final ProbabilityAdjuster probabilityAdjuster;
    final Set<Statement> statements;
    public final HashMap<Integer, Option> opttbl; // FIXME experimental
    
    public Model(String modelName, ProbabilityAdjuster probabilityAdjuster) {
        assert modelName != null;
        assert probabilityAdjuster != null : "Null probabilityAdjuster";
        
        this.statements = new TreeSet<>();
        this.add  = new ComponentAdder(this);
        this.find = null;
        this.name = modelName;
        this.probabilityAdjuster = probabilityAdjuster;
        
        this.opttbl = new HashMap<>(); // FIXME experimental
    }
    
    
    
    
    /**
     * Collect the statements in the model into a map of lists ordered by the 
     * temporal category (timestep value);
     * The statements in the lists are in random order (shuffled).
     * @return Map of lists of statements in the model.
     */
    public Map<Integer, List<Statement>> statementsByTimestep() { // FIXME remove public
        Map<Integer, List<Statement>> org = new TreeMap<>();
        
        // Collect the statements into timestep categories
        for(Statement s : this.statements) {
            if(!org.containsKey(s.timestep)) {
                org.put(s.timestep, new LinkedList<>());
            }
            org.get(s.timestep).add(s);
        }
        
        // Shuffle the statement lists in each timestep category
        for(Map.Entry<Integer, List<Statement>> e : org.entrySet()) {
            java.util.Collections.shuffle(e.getValue());
        }
        return org;
    }
    
    
    /**
     * Returns the number of <code>Option</code>fromStatement in the model.
     * @return The model option count
     */
    public int optionCount() {
        int optionCount = 0;
        for (Statement s : statements) {
            optionCount += s.optionCount();
        }
        return optionCount;
    }
    
    List<Option> getOptions() {
        List<Option> list = new ArrayList<>();
        for(Statement s : statements) list.addAll(s.options);
        return list;
    }
    
    public void updateFinder() {
        this.find = new ComponentFinder(this);
    }
    
    
    public Configuration evaluate() throws ProbabilityAdjustmentException {
        
        /* Initialize ComponentFinder if not yet initialized */
        if(this.find==null) this.find = new ComponentFinder(this);
        
        /* Evaluate model statements ordered by temporal category */
        for(Map.Entry<Integer, List<Statement>> e : this.statementsByTimestep().entrySet()) {
            for(Statement s : e.getValue()) {
                s.evaluate();
            }
        }
        
        /* Return evaluation result and reset model */
        Configuration c = new Configuration((this));
        reset();
        return c;
    }
    
    public Configuration evaluate(List<Pair<Statement, Option>> activeInterventions) throws ProbabilityAdjustmentException  {
        for(Pair<Statement, Option> p : activeInterventions) {
            assert p.left.model == this;
            assert p.left.intervention : "Statement " + p.left + " is not an intervention";
            
            /* Set the active intervention for each intervention statement */
            p.left.setActiveIntervention(p.right);
        }
        return this.evaluate();
        
    }
    
    void reset() {
        for(Statement s : statements) s.reset();
    }
    
    /**
     * 
     * @return String with probability or truth value of each option in the model
     */
    public String getModelStates() {
        StringBuilder states = new StringBuilder();
        for(Statement s : statements) {
            for(Option o : s.options) {
                states.append(o.toString()).append("\n");
            }
        }
        return states.toString();
    }

    
    @Override
    public Collection getNamespaceLabels() {
        Collection labels = new LinkedList<>();
        for(Statement s : statements) {
            labels.add(s.label);
        }
        return labels;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString()).append("\n");
        for(Statement s : this.statements) {
            sb.append("Statement ").append(s.label).append("\n");
            for(Option o : s.options) {
                sb.append("\tOption ").append(o.label).append(" (p").append(o.adjusted).append(")\n");
                for(Impact i : o.impacts) {
                    sb.append(String.format("\t\t> %s by %s\n", i.toOption.label, i.adjustmentFunction.toString()));
                }
            }
        }
        return sb.toString();
    }
    
    
    public final class ComponentAdder {
        
        final Model model;
        
        public ComponentAdder(Model model) {            
            this.model = model;
            assert this.model.find == null;
        }
        
        /**
         * Adds a <tt>Statement</tt> to the <b>model</b>
         * @param statementLabel Label of the statement
         * @param description Longer description of the statement
         * @param intervention Is the statement an intervention statement?
         * @param timestep The temporal category of the statement
         */
        public final void statement(
                String statementLabel, 
                String description, 
                boolean intervention, 
                int timestep) 
        {
            assert statementLabel != null;
            Label label = new Label(statementLabel, model);
            Statement s = new Statement(this.model, label, description, intervention, timestep);
            model.statements.add(s);
            
        }
        
        /**
         * Adds an <tt>Option</tt> to a <tt>Statement</tt> in the <b>model</b>.
         * Statements needs to be added to the model before option addition.
         * @param statementLabel Label of the statement of the option
         * @param optionLabel Label of the option to be added
         * @param aprioriProbability Initial / A priori probability of the option
         * @throws LabelNotFoundException 
         */
        public final void option(
                String statementLabel, 
                String optionLabel, 
                double aprioriProbability) 
                throws LabelNotFoundException 
        {
            assert statementLabel != null;
            assert optionLabel != null;
            assert aprioriProbability >= 0 && aprioriProbability <= 1;
            
            //Statement s = find.statement(statementLabel);
            Statement s = statements.stream().filter(st -> st.label.value.equals(statementLabel)).findFirst().get();
            assert s != null;
            Option o = new Option(new Label(optionLabel, s), s, new Probability(aprioriProbability));
            s.options.add(o);
        }
        
        /**
         * Adds an <tt>Impact</tt> to an <tt>Option</tt> of a <tt>Statement</tt> in the <b>model</b>.
         * The option and the statement need to be added to the model before impact addition.
         * @param fromStatementLabel Label of the statement of the option that the impact belongs to
         * @param fromOptionLabel Label of the option that the impact belongs to
         * @param toStatementLabel Label of the statement of the option that the impact is targeted at 
         * @param toOptionLabel Label of the option that the impact is targeted at 
         * @param adjustmentFunctionName Name of the probability adjustment function to be used with impact
         * @throws LabelNotFoundException
         * @throws AXIOMException 
         */
        public final void impact(
                String fromStatementLabel, 
                String fromOptionLabel, 
                String toStatementLabel, 
                String toOptionLabel, 
                String adjustmentFunctionName) 
                throws LabelNotFoundException, AXIOMException 
        {
            assert fromStatementLabel != null;
            assert toStatementLabel != null;
            assert fromOptionLabel != null;
            assert toOptionLabel != null;
            assert adjustmentFunctionName != null;
            
            Statement fromStatement = statements.stream().filter(st -> st.label.value.equals(fromStatementLabel)).findFirst().get();
            Option fromOption = fromStatement.findOption(new Label(fromOptionLabel));
            Statement toStatement = statements.stream().filter(st -> st.label.value.equals(toStatementLabel)).findFirst().get();
            Option toOption = toStatement.findOption(new Label(toOptionLabel));
            if(fromStatement.equals(toStatement)) throw new AXIOMException("Impact from statement " + fromStatement.label + " cannot point to an option in the same statement");
            ProbabilityAdjustmentFunction f = model.probabilityAdjuster.getFunction(adjustmentFunctionName);
            
            assert fromOption != null; assert toOption != null;
            assert fromOption != toOption;
            assert f != null;
            
            fromOption.impacts.add(new Impact(f, fromOption, toOption));
        }
    }
    
    public final class ComponentFinder {
        
        final Model model;
        private final HashMap<Integer, Option>   optionTable;
        private final HashMap<Option, Integer>   indexTable;
        private final HashMap<String, Statement> statementTable;
        
        ComponentFinder(Model model) {
            this.model = model;
            this.optionTable = new HashMap<>();
            this.indexTable = new HashMap<>();
            this.statementTable = new HashMap<>();
            
            for(int i=1;i<=this.model.optionCount();i++) {
                Option o = this.model.getOptions().get(i-1);
                assert o != null;
                this.optionTable.put(i, o);
                this.indexTable.put(o,i);
            }
            
            for(Statement s : model.statements) {
                assert s != null;
                statementTable.put(s.label.value, s);
            }
        }
        
        /**
         * Returns <tt>Statement</tt> with label <b>label</b>
         * @param label statement label
         * @return A <tt>Statement</tt>
         */
        public Statement statement(String label) {
            Statement s = Objects.requireNonNull(statementTable.get(label),"Statement with label " + label + " not found");
            return s;
        }

        /**
         * Returns <tt>Statement</tt> with label <b>label</b>
         * @param label statement label
         * @return A <tt>Statement</tt>
         */
        public Statement statement(Label label) {
            return this.statement(label.value);
        }        
        
        /**
         * Returns the <tt>Option</tt> with specified <b>statementLabel</b> and <b>optionLabel</b>
         * @param statementLabel Label of statement of option
         * @param optionLabel Label of option
         * @return <tt>Option</tt> 
         * @throws LabelNotFoundException 
         */
        public Option option(String statementLabel, String optionLabel) throws LabelNotFoundException  {
            Option o = Objects.requireNonNull(statement(statementLabel).findOption(new Label(optionLabel)), 
                    "Option with label " + optionLabel + " not found");
            return o;
        }

        /**
         * Returns the <tt>Option</tt> with index <b>index</b>
         * @param index Index of <tt>Option</tt> 
         * @return <tt>Option</tt> with index <b>index</b>
         */
        public Option option(int index) {
            Option o = Objects.requireNonNull(optionTable.get(index));
            return o;
        }

        /**
         * Returns the index of <tt>Option</tt> <b>option</b>
         * @param option Option whose index is returned
         * @return Index of <b>option</b>
         */
        int index(Option option) {
            assert option.statement.model == this.model;
            return indexTable.get(option);
        }
    }
    

    
    
}

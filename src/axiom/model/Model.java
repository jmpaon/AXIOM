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
    final ProbabilityAdjuster probabilityAdjuster;
    final Set<Statement> statements;
    public final HashMap<Integer, Option> opttbl; // FIXME experimental
    
    public Model(String modelName, ProbabilityAdjuster probabilityAdjuster) {
        assert modelName != null;
        assert probabilityAdjuster != null : "Null probabilityAdjuster";
        
        this.statements = new TreeSet<>();
        this.add = new ComponentAdder(this);
        this.name = modelName;
        this.probabilityAdjuster = probabilityAdjuster;
        
        this.opttbl = new HashMap<>(); // FIXME experimental
    }
    
    /* EXPERIMENTAL (FIXME)*/
    public void setupOpttbl() {
        for(int i=1;i<=this.optionCount();i++) {
            opttbl.put(i, this.getOption_(i));
            assert this.getOption(i) != null;
        }
        System.out.println("opttbl size : " + opttbl.size());
    }
    
    public Statement findStatement(Label label) throws LabelNotFoundException {
        for (Statement s : statements) {
            if(s.label.equals(label)) return s;
        }
        throw new LabelNotFoundException("Statement with label " + label + " not found");
    }
    
    public Statement findStatement(String label) throws LabelNotFoundException {
        return findStatement(new Label(label));
    }
    
    boolean statementExists(Label label) {
        for(Statement s : statements) if(s.label.equals(label)) return true;
        return false;
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
    
    /* EXPERIMENTAL */
    public Option getOption(int index) { // FIXME non public
        assert index > 0 && index <= this.optionCount() : "index " + index + " is out of bounds [1," + this.optionCount()+"]" ;
        return opttbl.get(index);
    }
    
    /* NON-experimental */
    public Option getOption_(int index) {
        assert index > 0 && index <= this.optionCount() : "index " + index + " is out of bounds [1," + this.optionCount()+"]" ;
        return this.getOptions().get(index-1);
    }
    
    public Option getOption(String label) throws LabelNotFoundException {
        for(Option o : getOptions()) if(o.label.value.equals(label)) return o; // FIXME model can have several options with the same label
        throw new LabelNotFoundException("Option with label " + label + " not found");
    }
    
    public int getOptionIndex(Option o) {
        assert o != null;
        assert o.statement.model == this;
        int i = 1;
        for(Option op : getOptions()) {
            if(op.equals(o)) return i;
            i++;
        }
        throw new IllegalArgumentException("Option " + o + " not found");
    }
    
    public Configuration evaluate() throws ProbabilityAdjustmentException {
        for(Map.Entry<Integer, List<Statement>> e : this.statementsByTimestep().entrySet()) {
            for(Statement s : e.getValue()) {
                s.evaluate();
            }
        }
        Configuration c = new Configuration((this));
        reset();
        return c;
    }
    
    public Configuration evaluate(List<Pair<Statement, Option>> activeInterventions) throws ProbabilityAdjustmentException  {
        for(Pair<Statement, Option> p : activeInterventions) {
            assert p.left.model == this;
            assert p.left.intervention : "Statement " + p.left + " is not an intervention";
            
            // Set the active intervention for each intervention statement
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

    
//    @Override
//    public int hashCode() {
//        int hash = 7;
//        hash = 59 * hash + Objects.hashCode(this.statements);
//        return hash;
//    }
//    
//    
//    @Override
//    public boolean equals(Object o) {
//        if(o == null) return false;
//        if(!Model.class.isAssignableFrom(o.getClass())) return false;
//        final Model m = (Model) o;
//        return this.statements.equals(m.statements);
//    }
    
    
    
    
    
    
    public final class ComponentAdder {
        
        final Model model;
        
        public ComponentAdder(Model model) {
            this.model = model;
        }
        
        public final void statement(String statementLabel, String description, boolean intervention, int timestep) {
            assert statementLabel != null;
            Label label = new Label(statementLabel, model);
            Statement s = new Statement(this.model, label, description, intervention, timestep);
            model.statements.add(s);
            
        }
        
        public final void option(String statementLabel, String optionLabel, double aprioriProbability) throws LabelNotFoundException {
            assert statementLabel != null;
            assert optionLabel != null;
            assert aprioriProbability >= 0 && aprioriProbability <= 1;
            
            Statement s = model.findStatement(new Label(statementLabel));
            Option o = new Option(new Label(optionLabel, s), s, new Probability(aprioriProbability));
            s.options.add(o);
        }
        
        public final void impact(
                String fromStatementLabel, 
                String fromOptionLabel, 
                String toStatementLabel, 
                String toOptionLabel, 
                String adjustmentFunctionName) 
                throws LabelNotFoundException, AXIOMException {
            
            assert fromStatementLabel != null;
            assert toStatementLabel != null;
            assert fromOptionLabel != null;
            assert toOptionLabel != null;
            assert adjustmentFunctionName != null;
            
            Statement fromStatement = model.findStatement(new Label(fromStatementLabel));
            Option fromOption = fromStatement.findOption(new Label(fromOptionLabel));
            Statement toStatement = model.findStatement(new Label(toStatementLabel));
            Option toOption = toStatement.findOption(new Label(toOptionLabel));
            if(fromStatement.equals(toStatement)) throw new AXIOMException("Impact from statement " + fromStatement.label + " cannot point to an option in the same statement");
            ProbabilityAdjustmentFunction f = model.probabilityAdjuster.getFunction(adjustmentFunctionName);
            
            assert fromOption != null; assert toOption != null;
            assert fromOption != toOption;
            assert f != null;
            
            fromOption.impacts.add(new Impact(f, fromOption, toOption));
        }
        
    }
    

    
    
}

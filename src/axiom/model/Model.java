/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package axiom.model;

import axiom.probabilityAdjusters.ProbabilityAdjuster;
import axiom.probabilityAdjusters.ProbabilityAdjustmentFunction;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 *
 * @author juha
 */
public class Model implements LabelNamespace {
    
    public ComponentAdder add;
    public ProbabilityAdjuster probabilityAdjuster;
    public final String name;
    private final Set<Statement> statements;
    
    
    public Model(String modelName) {
        this.statements = new TreeSet<>();
        this.add = new ComponentAdder(this);
        this.name = modelName;
        
    }
    
    Statement findStatement(Label label) throws LabelNotFoundException {
        for (Statement s : statements) {
            if(s.label.equals(label)) return s;
        }
        throw new LabelNotFoundException("Statement with label " + label + " not found");
    }
    
    boolean statementExists(Label label) {
        for(Statement s : statements) if(s.compareTo(label)==0) return true;
        return false;
    }
    
    
    /**
     * Returns the number of <code>Option</code>fromStatement in the model.
     * @return The model option count
     */
    int optionCount() {
        int optionCount = 0;
        for (Statement s : statements) {
            optionCount += s.optionCount();
        }
        return optionCount;
    }
    
    public Configuration evaluate() {
        throw new UnsupportedOperationException("Not implemented");
    }
    
    void reset() {
        
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
    
    
    
    
    
    public final class ComponentAdder {
        
        final Model model;
        
        public ComponentAdder(Model model) {
            this.model = model;
        }
        
        public void statement(String statementLabel, String description, boolean intervention, int timestep) {
            Label label = new Label(statementLabel, model);
            Statement s = new Statement(label, description, intervention, timestep);
            model.statements.add(s);
        }
        
        public void option(String statementLabel, String optionLabel, double aprioriProbability) throws LabelNotFoundException {
            Statement s = model.findStatement(new Label(statementLabel));
            Option o = new Option(new Label(optionLabel, s), s, new Probability(aprioriProbability));
            s.options.add(o);
        }
        
        public void impact(String fromStatementLabel, 
                String fromOptionLabel, 
                String toStatementLabel, 
                String toOptionLabel, 
                String adjustmentFunctionName) 
                throws LabelNotFoundException, AXIOMException {
            
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
        
        public void probabilityAdjuster(ProbabilityAdjuster probabilityAdjuster) throws ArgumentException {
            if(probabilityAdjuster == null) throw new ArgumentException("probability adjuster is null");
            model.probabilityAdjuster = probabilityAdjuster;
        }
        
    }
    

    
    
}

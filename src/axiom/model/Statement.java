/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package axiom.model;

import axiom.probabilityAdjusters.ProbabilityAdjustmentException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

/**
 *
 * @author juha
 */
public class Statement implements LabelNamespace, Comparable<Statement> {
    
    public static final String DEFAULT_DESCRIPTION = "(No description)";
    
    final Model model;
    final Label label;
    final String description;
    final boolean intervention;
    final int timestep;
    final Set<Option> options;
    
    private Option evaluatedState;
    
    
    Statement(Model model, Label label, boolean intervention, int timestep) {
        this(model, label, null, intervention, timestep);
    }
    
    Statement(Model model, Label label, String description, boolean intervention, int timestep) {
        
        assert model != null;
        assert label != null;
        
        this.model = model;
        this.label = label;
        this.description = description != null ? description : DEFAULT_DESCRIPTION;
        this.intervention = intervention;
        this.timestep = timestep;
        this.evaluatedState = null;
        this.options = new TreeSet<>();
    }
    
    Option getEvaluatedState() {
        return this.evaluatedState;
    }
    
    public boolean isEvaluated() {
        return this.evaluatedState != null;
    }
    
    void setActiveIntervention(Option interventionOption) {
        assert this.intervention : "Attempt to set active intervention option for a non-intervention statement";
        assert interventionOption.statement == this : 
                "intervention option set to wrong statement: Intervention option of statement " + interventionOption.statement.label + " set to " + this.label ;
        
        this.evaluatedState = interventionOption;
    }
    
    void evaluate() throws ProbabilityAdjustmentException {
        assert this.intervention || this.evaluatedState == null : "Non-intervention statement has state before evaluation";
        if(!Probability.isValidDistribution(this.optionProbabilities())) 
            throw new IllegalStateException("Probability distribution of statement " +this.label+ " is not valid (" + this.optionProbabilityDistribution(" ") +")");
        
        if(this.evaluatedState == null) {
            Probability rnd = Probability.random();
            Probability sum = new Probability(0);
            for(Option o : optionsInRandomOrder()) { 
                sum.add(o.adjusted);
                if(sum.compareTo(rnd) >= 0) {
                    this.evaluatedState = o;
                    break;
                }
            }            
        }
        
        assert this.evaluatedState != null;
        this.evaluatedState.executeImpacts();
        
    }
    
    void reset() {
        assert this.evaluatedState != null;
        for(Option o : options) o.reset();
        this.evaluatedState = null;
    }
    
    
    
    public int optionCount() {
        return options.size();
    }
    
    Option getOptionAtIndex(int index) {
        assert index <= this.optionCount();
        assert index > 0;
        for(Option o : options) {
            if(index <= 1) return o;
            index--;
        }
        throw new IllegalArgumentException("Option with index " + index + " not found");
    }
    
    List<Option> optionsInRandomOrder() {
        List options_shuffled = new LinkedList<>(options);
        java.util.Collections.shuffle(options_shuffled);
        return options_shuffled;
    }
    
    Option findOption(Label label) throws LabelNotFoundException {
        for(Option o : options) {
            if(o.label.equals(label)) return o;
        }
        throw new LabelNotFoundException("Option with label " + label.value + " not found");
    }
    
    boolean optionExists(Label label) {
        for(Option o : options) if(o.label.equals(label)) return true;
        return false;
    }
    


    @Override
    public Collection<Label> getNamespaceLabels() {
        Collection<Label> labels = new LinkedList<>();
        for(Option o : options) {
            labels.add(o.label);
        }
        return labels;
    }

    @Override
    public boolean equals(Object o) {
        if(o == null) return false;
        if(!Statement.class.isAssignableFrom(o.getClass())) return false;
        final Statement s = (Statement) o;
        if(!this.model.equals(s.model)) return false;
        if(!this.intervention==s.intervention) return false;
        if(!(this.timestep==s.timestep)) return false;
        return this.label.equals(s.label);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.model);
        hash = 97 * hash + Objects.hashCode(this.label);
        hash = 97 * hash + (this.intervention ? 1 : 0);
        hash = 97 * hash + this.timestep;
        return hash;
    }

    
    @Override
    public int compareTo(Statement s) {
        return this.label.compareTo(s.label);
    }
    
    @Override
    public String toString() {
        return String.format("Statement %s with options %s", this.label, this.options.toString());
    }
    
    public String optionProbabilityDistribution(String separator) {
        StringBuilder sb = new StringBuilder();
        sb.append("Statement ").append(this.label).append(separator);
        for(Option o : this.options) {
            sb.append(o.label).append(" : ").append(o.adjusted.toStringAsFraction()).append(separator);
        }
        int residual = Probability.requiredDistributionCorrection(this.optionProbabilities());
        sb.append("Residual ").append(residual);
        
        return sb.toString();
    }
    
    Collection<Probability> optionProbabilities() {
        LinkedList<Probability> ps = new LinkedList<>();
        for(Option o : this.options) ps.add(o.adjusted);
        return ps;
    }
    
}

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
    
    void evaluate() throws ProbabilityAdjustmentException {
        assert this.intervention || this.evaluatedState == null : "Non-intervention statement has state before evaluation";
        assert Probability.isValidDistribution(this.optionProbabilities());
        
        Probability rnd = Probability.random();
        System.out.println("random is " + rnd);
        Probability sum = new Probability(0);
        for(Option o : optionsInRandomOrder()) { 
            sum.add(o.adjusted);
            System.out.println("p sum is now "+  sum);
            if(sum.compareTo(rnd) >= 0) {
                System.out.println("sum " + sum + " is now bigger than " + rnd);
                this.evaluatedState = o;
                break;
            }
        }
        
        assert this.evaluatedState != null;
        
        //this.evaluatedState = this.optionsInRandomOrder().get(0);
        System.out.println("Evaluated statement " + this.label + "(timestep " + this.timestep + ") to state " + this.evaluatedState.label);
        this.evaluatedState.executeImpacts();
        System.out.println(this.model.getModelStates());
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
    
    public String optionProbabilityDistribution() {
        StringBuilder sb = new StringBuilder();
        sb.append("Statement ").append(this.label).append("\n");
        for(Option o : this.options) sb.append(o.label).append(" : ").append(o.adjusted.toStringAsFraction()).append("\n");
        return sb.toString();
    }
    
    private Collection<Probability> optionProbabilities() {
        LinkedList<Probability> ps = new LinkedList<>();
        for(Option o : this.options) ps.add(o.adjusted);
        return ps;
    }
    
}

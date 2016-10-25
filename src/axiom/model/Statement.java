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
    /** The evaluation of the statement results in one of the <tt>Option</tt>s being assigned as the <b>evaluatedState</b> */
    private Option evaluatedState; 
    
    
    /**
     * Constructor for <tt>Statement</tt>
     * @param model Model of statement
     * @param label Identifying label of statement
     * @param intervention Is the statement an intervention statement?
     * @param timestep The temporal category of the statement
     */
    Statement(Model model, Label label, boolean intervention, int timestep) {
        this(model, label, null, intervention, timestep);
    }
    
    /**
     * Constructor for <tt>Statement</tt>
     * @param model Model of statement
     * @param label Identifying label of statement
     * @param description Longer description of the statement
     * @param intervention Is the statement an intervention statement?
     * @param timestep The temporal category of the statement
     */
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
    
    /**
     * @return The <tt>Option</tt> that has been evaluated to be the state of this statement
     * or <i>null</i> if the statement has not been evaluated yet
     */
    Option getEvaluatedState() {
        assert this.evaluatedState == null || this.options.contains(this.evaluatedState);
        return this.evaluatedState;
    }
    
    /**
     * @return <i>true</i> if the statement has been evaluated, <i>false</i> otherwise.
     */
    public boolean isEvaluated() {
        return this.evaluatedState != null;
    }
    
    /**
     * Sets the evaluated state of this statement to <b>interventionOption</b>.
     * This method is used in the process of evaluating the model under different intervention combinations.
     * @param interventionOption An <tt>Option</tt> in the set of options under this statement, 
     * to be set as the evaluated state
     */
    void setActiveIntervention(Option interventionOption) {
        assert this.intervention : "Attempt to set active intervention option for a non-intervention statement";
        assert interventionOption.statement == this : 
                "intervention option set to wrong statement: Intervention option of statement " + interventionOption.statement.label + " set to " + this.label ;
        assert this.options.contains(interventionOption):
                "intervention option " + interventionOption.label + " is not present in statement " + this.label;
        
        this.evaluatedState = interventionOption;
    }
    
    /**
     * Evaluates the <tt>Statement</tt>: assigns a state to it 
     * and calls the {@link Option#executeImpacts() } method.
     * @throws ProbabilityAdjustmentException 
     */
    void evaluate() throws ProbabilityAdjustmentException {
        assert this.intervention || this.evaluatedState == null : "Non-intervention statement " + this.label + " has state before evaluation";
        assert Probability.isValidDistribution(this.adjustedProbabilities()) : "Probability distribution of statement " +this.label+ " is not valid (" + this.adjustedProbabilityDistributionString(" ")+")";
        
        /* Use a random probability to determine the result of the statement evaluation */
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
        assert this.options.contains(this.evaluatedState);
        
        this.evaluatedState.executeImpacts();
        
    }
    
    /**
     * Resets the statement and calls the {@link Option#reset() } method
     * to reset the <tt>Option</tt>s of the statement.
     */
    void reset() {
        assert this.evaluatedState != null;
        for(Option o : options) o.reset();
        this.evaluatedState = null;
    }
    
    /**
     * @return The number of options under this statement
     */
    public int optionCount() {
        return options.size();
    }
    
    /**
     * Returns the <tt>Option</tt> in place <b>index</b> in the <tt>Label</tt>-based ordering of <tt>Option</tt>s.
     * @param index Placing of the <tt>Option</tt> in the option list of this statement
     * @return Option
     */
    Option getOptionAtIndex(int index) {
        assert index <= this.optionCount() && index > 0;
        for(Option o : options) {
            if(index <= 1) return o;
            index--;
        }
        throw new IllegalArgumentException("Option with index " + index + " not found");
    }
    
    /**
     * @return A new list of options of this statement in random order
     */
    List<Option> optionsInRandomOrder() {
        List options_shuffled = new LinkedList<>(options);
        java.util.Collections.shuffle(options_shuffled);
        return options_shuffled;
    }
    
    /**
     * Returns the <tt>Option</tt> with label <b>label</b>.
     * @param label Label of the sought option
     * @return Option
     * @throws LabelNotFoundException 
     */
    Option findOption(Label label) throws LabelNotFoundException {
        for(Option o : options) {
            if(o.label.equals(label)) return o;
        }
        throw new LabelNotFoundException("Option with label " + label.value + " not found");
    }
    
    /**
     * Does <tt>Option</tt> with <b>label</b> exist under this statement?
     * @param label An option label
     * @return <i>true</i> if option with <b>label</b> exists for this <tt>Statement</tt>
     */
    boolean optionExists(Label label) {
        for(Option o : options) if(o.label.equals(label)) return true;
        return false;
    }
    
    /**
     * Returns the labels in the statement namespace, i.e.the option labels.
     * @return Option labels.
     */
    @Override
    public Collection<Label> getNamespaceLabels() {
        Collection<Label> labels = new LinkedList<>();
        for(Option o : options) {labels.add(o.label);}
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
    
    public String adjustedProbabilityDistributionString(String separator) {
        StringBuilder sb = new StringBuilder();
        sb.append("Statement ").append(this.label).append(separator);
        for(Option o : this.options) {
            sb.append(o.label).append(" : ").append(o.adjusted.toStringAsFraction()).append(separator);
        }
        int residual = Probability.requiredDistributionCorrection(this.adjustedProbabilities());
        sb.append("Residual ").append(residual);
        
        return sb.toString();
    }

    public String aprioriProbabilityDistributionString(String separator) {
        StringBuilder sb = new StringBuilder();
        sb.append("Statement ").append(this.label).append(separator);
        for(Option o : this.options) {
            sb.append(o.label).append(" : ").append(o.apriori.toStringAsFraction()).append(separator);
        }
        int residual = Probability.requiredDistributionCorrection(this.aprioriProbabilities());
        sb.append("Residual ").append(residual);
        return sb.toString();
    }

    
    /**
     * Returns the <u>adjusted</u> <tt>Probability</tt>s of the <tt>Option</tt>s of this <tt>Statement</tt>
     * @return the <u>adjusted</u> probabilities of the <tt>Option</tt>s of this <tt>Statement</tt>
     */
    Collection<Probability> adjustedProbabilities() {
        LinkedList<Probability> adjustedProbabilities = new LinkedList<>();
        for(Option o : this.options) adjustedProbabilities.add(o.adjusted);
        return adjustedProbabilities;
    }
    
    /**
     * Returns the <u>a priori</u> <tt>Probability</tt>s of the <tt>Option</tt>s of this <tt>Statement</tt>
     * @return the <u>a priori</u> probabilities of the <tt>Option</tt>s of this <tt>Statement</tt> 
     */
    Collection<Probability> aprioriProbabilities() {
        LinkedList<Probability> ps = new LinkedList<>();
        for(Option o : this.options) ps.add(o.apriori);
        return ps;
    }
    
}

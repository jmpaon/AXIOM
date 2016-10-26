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
import java.util.Objects;

/**
 *
 * @author juha
 */
public class Option implements Comparable<Option> {
    
    public static final String DEFAULT_DESCRIPTION = "(No description)";
    
    final Label label;
    final Statement statement;
    final Probability apriori;
    final Probability adjusted;
    final String description;
    final List<Impact> impacts;
    
    /**
     * Constructor for <tt>Option</tt>
     * @param label Option label
     * @param statement Statement of the option
     * @param apriori Initial probability of the option
     */
    Option(Label label, Statement statement, Probability apriori) {
        this(label, statement, apriori, null);
    }
    
    
    /**
     * Constructor for <tt>Option</tt>
     * @param label Option label
     * @param statement Statement of the option
     * @param apriori Initial probability of the option
     * @param description Longer description of the option
     */
    Option(Label label, Statement statement, Probability apriori, String description) {
        assert label != null;
        assert statement != null;
        assert apriori != null;
        
        this.description = description != null ? description : DEFAULT_DESCRIPTION;
        this.label = label;
        this.statement = statement;
        this.apriori = apriori;
        this.adjusted = new Probability(apriori.get());
        this.impacts = new LinkedList<>();
    }
    
    
    /**
     * Returns a list of other options that are under this statement
     * @return Other options under the same statement
     */
    List<Option> complementOptions() {
        List<Option> otherOptions = new LinkedList<>();
        for(Option o : this.statement.options) {
            if(!o.equals(this)) otherOptions.add(o);
        }
        return otherOptions;
    }
    
    /**
     * Executes the <tt>Impact</tt>s associated with this option.
     * This operation should be performed only for options 
     * that are evaluated to be true, i.e.are assigned as the state of some statements.
     * @throws ProbabilityAdjustmentException 
     */
    void executeImpacts() throws ProbabilityAdjustmentException {
        assert this.statement.getEvaluatedState() == this;
        for(Impact i : this.impactsInRandomOrder()) {
            i.execute();
        }
    }
    
    /**
     * Resets the <tt>Option</tt> by resetting the adjusted probability 
     * to be equal to the initial/a priori probability and 
     * calling the {@link Impact#reset() } method on each <tt>Impact</tt> of this option.
     */
    void reset() {
        this.adjusted.set(apriori);
        for(Impact i : impacts) i.reset();
    }
    
    /**
     * Returns the <code>Impact</code>s of the <code>Option</code> in random order.
     * @return List of <code>Impact</code>s in random order.
     */
    private List<Impact> impactsInRandomOrder() {
        List impacts_shuffled = new LinkedList<>(impacts);
        java.util.Collections.shuffle(impacts_shuffled);
        return impacts_shuffled;
    }

    
    @Override
    public boolean equals(Object o) {
        if(o == null) return false;
        if(!Option.class.isAssignableFrom(o.getClass())) return false;
        final Option op = (Option) o;
        return this.label.equals(op.label);
    }
    
    
    @Override
    public int hashCode() {
        int hash = 7;
        return 37 * hash + Objects.hashCode(this.label);
    }

    
    @Override
    public int compareTo(Option o) {
        return this.label.compareTo(o.label);
    }
    
    /**
     * Returns an identifying long label for this option: 
     * long label consists of the label of the statement 
     * and the label of the option,
     * separated by a semicolon.
     * @return A long label made of statement and option labels separated by semicolon
     */
    public String getLongLabel() {
        return this.statement.label + ":" + this.label;
    }
    
    @Override
    public String toString() {
        String value;
        if(this.statement.getEvaluatedState() != null) {
            value = this == this.statement.getEvaluatedState() ? "TRUE" : "FALSE";
        } else {
            value = this.adjusted.toString();
        }
        return String.format("%s (%s)", this.getLongLabel(), value);
    }




    
}

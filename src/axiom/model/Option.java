/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package axiom.model;

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
    Probability apriori;
    Probability adjusted;
    final String description;
    final List<Impact> impacts;
    
    
    Option(Label label, Statement statement, Probability apriori) {
        this(label, statement, apriori, null);
    }
    
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
    
    void executeImpacts() {
        for(Impact i : this.impactsInRandomOrder()) {
            i.execute();
        }
    }
    
    void reset() {
        this.adjusted.setValue(apriori);
        for(Impact i : impacts) i.reset();
    }
    
    /**
     * Returns the <code>Impact</code>s of the <code>Option</code> in random order.
     * @return List of <code>Impact</code>s in random order.
     */
    List<Impact> impactsInRandomOrder() {
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
        hash = 37 * hash + Objects.hashCode(this.label);
        //hash = 37 * hash + Objects.hashCode(this.statement);
        //hash = 37 * hash + Objects.hashCode(this.apriori);
        //hash = 37 * hash + Objects.hashCode(this.description);
        return hash;
    }

    
    @Override
    public int compareTo(Option o) {
        return this.label.compareTo(o.label);
    }    
    
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
        String concatenatedLabel = this.statement.label + ":" + this.label;
        return String.format("%s = %s", concatenatedLabel, value);
    }




    
}

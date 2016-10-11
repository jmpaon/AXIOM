/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package axiom.model;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author juha
 */
public class Option implements Comparable<Label> {
    
    public static final String DEFAULT_DESCRIPTION = "(No description)";
    
    Label label;
    Statement statement;
    Probability apriori;
    Probability adjusted;
    String description;
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
    
    void evaluate() {
        for(Impact i : this.impactsInRandomOrder()) {
            i.evaluate();
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
    public int compareTo(Label l) {
        return this.label.compareTo(l);
    }
    
    public int compareTo(String s) {
        return this.label.value.compareTo(s);
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
        return String.format("%40s = %5s", concatenatedLabel, value);
    }


    
}

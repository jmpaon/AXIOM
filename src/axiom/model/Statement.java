/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package axiom.model;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 *
 * @author juha
 */
public class Statement implements LabelNamespace, Comparable<Label> {
    
    public static final String DEFAULT_DESCRIPTION = "(No description)";
    
    Label label;
    String description;
    boolean intervention;
    int timestep;
    final Set<Option> options;
    
    private Option evaluatedState;
    
    
    Statement(Label label, boolean intervention, int timestep) {
        this(label, null, intervention, timestep);
    }
    
    Statement(Label label, String description, boolean intervention, int timestep) {
        
        assert label != null;
        
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
    
    void evaluate() {
        
    }
    
    void reset() {
        assert this.evaluatedState != null;
        for(Option o : options) o.reset();
        this.evaluatedState = null;
    }
    
    
    
    public int optionCount() {
        return options.size();
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
        for(Option o : options) if(o.compareTo(label) == 0) return true;
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
    public int compareTo(Label l) {
        return this.label.compareTo(l);
    }
    
    public int compareTo(String s) {
        return this.label.value.compareTo(s);
    }
    
}

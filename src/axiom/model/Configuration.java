/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package axiom.model;

import java.util.Collection;
import java.util.Set;

/**
 * Configuration is the result of an AXIOM <code>Model</code> evaluation.
 * In a configuration, each <code>Statement</code> has an evaluated state, 
 * meaning that each <code>Option</code> has a truth value.
 * @author jmpaon
 */
public class Configuration {
    
    final Model model;
    final boolean states[];
    
    /**
     * Constructor for <code>Configuration</code>.
     * @param model AXIOM model.
     */
    Configuration(Model model) {
        assert model != null;
        this.model = model;
        this.states = new boolean[model.optionCount()];
        for(int i=0; i<model.optionCount(); i++) {
            Option o = model.find.option(i+1);
            states[i] = o.statement.getEvaluatedState().equals(o);
        }
    }
    
    /**
     * @param optionIndex Index of option in <b>model</b>
     * @return TRUE if option is true in <code>Configuration</code>, FALSE otherwise
     */
    public boolean isOptionTrue(int optionIndex) {
        assert optionIndex > 0 && optionIndex <= this.model.optionCount();
        return this.states[optionIndex-1];
    }
    
    /**
     * 
     * @param option
     * @return 
     */
    public boolean isOptionTrue(Option option) {
        assert option != null;
        assert option.statement.model == this.model;
        return states[model.find.index(option)-1];
    }

    
    /**
     * 
     * @param optionSet
     * @return 
     */
    public boolean isOptionSetTrue(Collection<Option> optionSet) {
        for(Option o : optionSet) if(! isOptionTrue(o)) return false;
        return true;
    }

    /**
     * 
     * @param optionSet
     * @return 
     */
    public boolean isOptionSetTrue(int[] optionSet) {
        for(int i : optionSet) if(! states[i-1]==true) return false;
        return true;
    }
    
    /**
     * 
     * @return 
     */
    public String toStringAsOptionValues() {
        StringBuilder sb = new StringBuilder();
        int i=0;
        for(Option o : model.getOptions()) {
            sb.append(o.getLongLabel()).append(" ").append(this.states[i++]).append("\n");
        }
        
        return sb.toString();
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        int i=0;
        for(boolean b : states) sb.append(++i).append(") ").append(model.find.option(i).getLongLabel()).append("=").append(b).append(" ");
        return sb.toString();
    }
    
}

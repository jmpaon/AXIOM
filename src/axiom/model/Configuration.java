/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package axiom.model;

import java.util.Collection;
import java.util.Set;

/**
 *
 * @author jmpaon
 */
public class Configuration {
    final Model model;
    final boolean states[];
    
    public Configuration(Model model) {
        assert model != null;
        this.model = model;
        this.states = new boolean[model.optionCount()];
        for(int i=0; i<model.optionCount(); i++) {
            Option o = model.getOption(i+1);
            states[i] = o.statement.getEvaluatedState().equals(o);
        }
    }
    
    public boolean isOptionTrue(int index){
        return this.states[index-1];
    }
    
    public boolean isOptionTrue(Option option) {
        assert option != null;
        assert option.statement.model == this.model;
        return states[model.getOptionIndex(option)-1];
    }

    public boolean isOptionTrue(Label optionLabel) throws LabelNotFoundException {
        return states[model.getOptionIndex(model.getOption(optionLabel.value))];
    }
    
    public boolean isOptionSetTrue(Collection<Option> optionSet) {
        for(Option o : optionSet) if(! isOptionTrue(o)) return false;
        return true;
    }

    public boolean isOptionSetTrue(int[] optionSet) {
        for(int i : optionSet) if(! states[i-1]==true) return false;
        return true;
    }
    
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
        for(boolean b : states) sb.append(++i).append(") ").append(model.getOption(i).getLongLabel()).append("=").append(b).append(" ");
        return sb.toString();
    }
    
}

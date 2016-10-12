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
    
    public Option getStateOfStatement(Statement statement) {
        throw new UnsupportedOperationException("Not implemented");
    }

    public Option getStateOfStatement(Label statementLabel) {
        throw new UnsupportedOperationException("Not implemented");
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
        for(boolean b : states) sb.append(++i).append(":").append(b).append(" ");
        return sb.toString();
    }
    
}

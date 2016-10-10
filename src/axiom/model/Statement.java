/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package axiom.model;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author juha
 */
public class Statement {
    
    private Map<Label, Option> options;
    
    
    public int optionCount() {
        return options.size();
    }
    
    List<Option> optionsInRandomOrder() {
        List options_shuffled = new LinkedList<>(options.values());
        java.util.Collections.shuffle(options_shuffled);
        return options_shuffled;
    }
    
}

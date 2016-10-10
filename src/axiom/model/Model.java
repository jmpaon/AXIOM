/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package axiom.model;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author juha
 */
public class Model {
    
    private final Map<Label, Statement> statements;
    public final String name;
    
    public Model(String modelName) {
        this.statements = new HashMap<>();
        this.name = modelName;
        
    }
    
    /**
     * Returns the number of <code>Option</code>s in the model.
     * @return The model option count
     */
    int optionCount() {
        int optionCount = 0;
        for (Statement s : statements.values()) {
            optionCount += s.optionCount();
        }
        return optionCount;
    }
    
    
}

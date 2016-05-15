/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package axiom.model;

import java.util.HashMap;

/**
 *
 * @author juha
 */
public class Model {
    
    private final HashMap<Label, Statement> statements;
    public final String name;
    
    public Model(String modelName) {
        this.statements = new HashMap<>();
        this.name = modelName;
        
    }
    
    
}

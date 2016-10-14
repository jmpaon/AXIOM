/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package axiom.model;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 *
 * @author jmpaon
 */
public class InterventionCombination {
    private Model model;
    private Map<Statement, <Pair 
    
    
    public InterventionCombination(Model model) {
        this.model = model;
        
        
    }
    
    
    private final Set<Statement> interventionStatements() {
        
        return model.statements.stream().filter(s -> s.intervention).collect(Collectors.toSet());
        
//        Set<Statement> interventionStatements = new TreeSet<>();
//        for(Statement s : model.statements) {
//            if(s.intervention) interventionStatements.add(s);
//        }
//        return interventionStatements;        
    }
    
    
    
    
    
    
    
    
    
    
    
}

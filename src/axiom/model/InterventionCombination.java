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
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

//import java.util.Map.Entry;

/**
 *
 * @author jmpaon
 */
public class InterventionCombination {
    private final Model model;
    private final Map<Statement, Pair<Option, Collection<Option>>> interventions;
    private final List<Pair<Statement, Pair<Option, List<Option>>>> ints;
    
    public InterventionCombination(Model model) {
        
        this.model = model;
        this.interventions = new TreeMap<>();
        this.ints = new LinkedList<>();
        
        for(Statement s : this.model.statements) {
            if(s.intervention) {
                List<Option> optionList = new LinkedList<>(s.options);
                Option firstOption = optionList.get(0);
                Pair<Option, List<Option>> activeAndAvailableOptions = new Pair<>(firstOption, optionList);
                Pair<Statement, Pair<Option, List<Option>>> pair = new Pair<>(s, activeAndAvailableOptions);
                this.ints.add(pair);
            }
        }
        
        
    }
    
    public void nextCombination() {
        for(Pair p : ints) {
            if(canStep(p)) {
                step(p);
                return;
            } else {
                reset(p);
            }
        }
    }
    
    private boolean canStep(Pair<Option, List<Option>> p) {
        return !(p.right.indexOf(p.left) == p.right.size()-1);
    }
    
    private void step(Pair<Option, List<Option>> p) {
        assert canStep(p);
        p.left = p.right.get(p.right.indexOf(p.left)+1);
    }
    
    private void reset(Pair<Option, List<Option>> p) {
        p.left = p.right.get(0);
    }
    
    
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for(Pair<Statement, Pair<Option, List<Option>>> p : this.ints) {
            Statement s = p.left;
            Option o = p.right.left;
            Collection<Option> c = p.right.right;
            sb.append("Statement ").append(s.label).append(": ").append(o.label).append(" ").append(c);
        }
        return sb.toString();
    }
    

    
    // FIXME not needed?
    private final Set<Statement> interventionStatements() {
        return model.statements.stream().filter(s -> s.intervention).collect(Collectors.toSet());
    }
    

    
    
    
    
    
    
    
    
    
    
    
}

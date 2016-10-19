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
    private final List<Pair<Statement, Pair<Option, List<Option>>>> interventions;
    
    public InterventionCombination(Model model) {
        
        this.model = model;
        this.interventions = new LinkedList<>();
        
        for(Statement s : this.model.statements) {
            if(s.intervention) {
                List<Option> optionList = new LinkedList<>(s.options);
                Option firstOption = optionList.get(0);
                Pair<Option, List<Option>> activeAndAvailableOptions = new Pair<>(firstOption, optionList);
                Pair<Statement, Pair<Option, List<Option>>> pair = new Pair<>(s, activeAndAvailableOptions);
                this.interventions.add(pair);
            }
        }
    }
    
    /**
     * Returns the current combination of options for the 
     * intervention statements of the <b>model</b> of this 
     * <code>InterventionCombination</code>.
     * 
     * @return 
     */
    public List<Pair<Statement, Option>> getCombination() {
        List<Pair<Statement, Option>> combination = new LinkedList<>();
        for( Pair<Statement, Pair<Option, List<Option>>> p : this.interventions ) {
            Pair<Statement, Option> intervention = new Pair(p.left, p.right.left);
            combination.add(intervention);
        }
        return combination;
    }
    
    /**
     * Is there a new combination of <code>Option</code>s available
     * @return <b>true</b> if next combination exists, <b>false</b> otherwise
     */
    public boolean hasNextCombination() {
        for(Pair<Statement, Pair<Option, List<Option>>> p : this.interventions) {
            if(canStep (p.right) ) return true;
        }
        return false;
    }
    
    
    public void nextCombination() {
        assert hasNextCombination() : "No next combination available fpr " + this;
        for(Pair<Statement, Pair<Option, List<Option>>> p : interventions) {
            Pair<Option, List<Option>> pp = p.right;
            if(canStep(pp)) {
                step(pp);
                return;
            } else {
                reset(pp);
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
        for(Pair<Statement, Pair<Option, List<Option>>> p : this.interventions) {
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

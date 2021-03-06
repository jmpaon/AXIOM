/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package axiom.model;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

//import java.util.Map.Entry;

/**
 * Objects of this class can serve sets of <tt>Option</tt>s from model <b>model</b>,
 * which are combinations of options in the intervention statements of <b>model</b>.
 * <tt>InterventionCombination</tt> can generate a new/unused combination of options
 * with the {@link InterventionCombination#nextCombination()} method
 * and return the combination 
 * in the form of a <tt>List</tt> of <tt>Statement</tt>-<tt>Option</tt> pairs
 * with the {@link InterventionCombination#getStatementsAndActiveInterventions()} method.
 * @author jmpaon
 */
public class InterventionCombination {
    private final Model model;
    private final List<Pair<Statement, Pair<Option, List<Option>>>> interventions;
    private final boolean hasCombinations;
    
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
        
        /** Flag <b>hasCombinations</b> if there are no interventions */
        hasCombinations = !this.interventions.isEmpty();
    }
    
    /**
     * Does the <tt>InterventionCombination</tt> have any combinations?
     * @return <i>true</i> if there are any combinations in the <tt>InterventionCombination</tt>
     */
    public boolean hasCombinations() { return hasCombinations; }
    
    /**
     * Returns the current combination of options for the 
     * intervention statements of the <b>model</b> of this 
     * <code>InterventionCombination</code>.
     * 
     * @return A list of pairs of intervention statements and their active interventions
     */
    public List<Pair<Statement, Option>> getStatementsAndActiveInterventions() {
        List<Pair<Statement, Option>> combination = new LinkedList<>();
        for( Pair<Statement, Pair<Option, List<Option>>> p : this.interventions ) {
            Pair<Statement, Option> intervention = new Pair(p.left, p.right.left);
            combination.add(intervention);
        }
        return combination;
    }
    
    /**
     * Is there a new combination of <code>Option</code>s available? 
     * @return <b>true</b> if next combination exists, <b>false</b> otherwise
     */
    public boolean hasNextCombination() {
        for(Pair<Statement, Pair<Option, List<Option>>> p : this.interventions) {
            if(canStep (p.right) ) return true;
        }
        return false;
    }
    
    
    /**
     * 'Advances' this intervention combination one step forward,
     * transforming it to the next intervention combination available for <b>model</b>.
     * The new combination can be obtained with the {@link InterventionCombination#getStatementsAndActiveInterventions()} method.
     */
    public void nextCombination() {
        assert hasNextCombination() : "Next combination is not available for " + this;
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
    
    /**
     * Is the active intervention <code>Option</code> not the last <code>Option</code>
     * of a set of possible intervention <code>Option</code>s?
     * @param activeOptionAndPossibleOptions Pair containing the active <code>Option</code> and a <code>List</code> of possible <code>Option</code>s.
     * @return <b>true</b> if the active option is the last in the list, <b>false</b> otherwise.
     */
    private boolean canStep(Pair<Option, List<Option>> activeOptionAndPossibleOptions) {
        return !(activeOptionAndPossibleOptions.right.indexOf(activeOptionAndPossibleOptions.left) == activeOptionAndPossibleOptions.right.size()-1);
    }
    
    /**
     * Set the active intervention option to be the next option on the list of possible options.
     * @param activeOptionAndPossibleOptions <code>Pair</code> containing 
     */
    private void step(Pair<Option, List<Option>> activeOptionAndPossibleOptions) {
        assert canStep(activeOptionAndPossibleOptions);
        activeOptionAndPossibleOptions.left = activeOptionAndPossibleOptions.right.get(activeOptionAndPossibleOptions.right.indexOf(activeOptionAndPossibleOptions.left)+1);
    }
    
    /**
     * Set the active intervention option to be the first option on the list of possible options.
     * @param activeOptionAndPossibleOptions the active option and possible options
     */
    private void reset(Pair<Option, List<Option>> activeOptionAndPossibleOptions) {
        activeOptionAndPossibleOptions.left = activeOptionAndPossibleOptions.right.get(0);
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
    

    

    
    
    
    
    
    
    
    
    
    
    
}

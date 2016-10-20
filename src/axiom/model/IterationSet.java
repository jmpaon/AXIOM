/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package axiom.model;

import axiom.probabilityAdjusters.ProbabilityAdjustmentException;
import java.util.LinkedList;
import java.util.List;

/**
 * <code>IterationSet</code> is a set of <code>Iteration</code>s, 
 * so that each iteration in the set uses one of the possible 
 * <code>InterventionCombination</code>s.
 * @author jmpaon
 */
public class IterationSet {
    
    final public List<Iteration> iterations;
    final Model model;
    final int evaluationCount;
    
    /**
     * Constructor for <code>IterationSet</code>
     * @param model AXIOM model from which the iterations are generated
     * @param evaluationCount How many times should the model be evaluated in a single iteration
     * @throws ProbabilityAdjustmentException 
     */
    public IterationSet(Model model, int evaluationCount) throws ProbabilityAdjustmentException {
        assert evaluationCount > 0 : "Evaluation count is zero";
        
        this.iterations = new LinkedList<>();
        this.model = model;
        this.evaluationCount = evaluationCount;
        this.createIterations();
    }
    
    /**
     * @return How many possible intervention combinations <b>model</b> has?
     */
    private int possibleInterventionCombinations() {
        int combinationCount = 1;
        for(Statement s : this.model.statements) {
            if(s.intervention) combinationCount *= s.optionCount();
        }
        return combinationCount;
    }
    
    /**
     * Creates an <code>Iteration</code> for each possible intervention combination 
     * available in <b>model</b>.
     * @throws ProbabilityAdjustmentException 
     */
    private void createIterations() throws ProbabilityAdjustmentException {
        InterventionCombination interventionCombination = new InterventionCombination(model);
        
        do {
            this.iterations.add(new Iteration(model, interventionCombination.getStatementsAndActiveInterventions(), evaluationCount));
            interventionCombination.nextCombination();            
        } while (interventionCombination.hasNextCombination());
    }
    
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Iteration set of %s iterations, each of %s evaluations\n\n", this.possibleInterventionCombinations(), this.evaluationCount));
        
        for(Iteration i : this.iterations) {
            sb.append(i.toString_probabilityChanges());
        }
        return sb.toString();
    }
    
    
}

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
 *
 * @author jmpaon
 */
public class IterationSet {
    
    final public List<Iteration> iterations;
    final Model model;
    final int evaluationCount;
    
    public IterationSet(Model model, int iterationCount) throws ProbabilityAdjustmentException {
        assert iterationCount > 0;
        
        this.iterations = new LinkedList<>();
        this.model = model;
        this.evaluationCount = iterationCount;
        this.createIterations();
    }
    
    private int possibleInterventionCombinations() {
        int combin = 1;
        for(Statement s : this.model.statements) {
            if(s.intervention) combin *= s.optionCount();
        }
        return combin;
    }
    
    private void createIterations() throws ProbabilityAdjustmentException {
        InterventionCombination interventions = new InterventionCombination(model);
        
        do {
            this.iterations.add(new Iteration(model, interventions.getCombination(), evaluationCount));
            interventions.nextCombination();            
        } while (interventions.hasNextCombination());
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Iteration set of %s iterations, each of %s evaluations\n\n", this.possibleInterventionCombinations(), this.evaluationCount));
        
        for(Iteration i : this.iterations) {
            sb.append(i.toString_pChanges());
        }
        return sb.toString();
    }
    
    
}

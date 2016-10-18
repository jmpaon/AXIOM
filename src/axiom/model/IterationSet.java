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
    
    final List<Iteration> iterations;
    final Model model;
    final int iterationCount;
    
    public IterationSet(Model model, int iterationCount) throws ProbabilityAdjustmentException {
        this.iterations = new LinkedList<>();
        this.model = model;
        this.iterationCount = iterationCount;
        this.createIterations();
    }
    
    private void createIterations() throws ProbabilityAdjustmentException {
        InterventionCombination interventions = new InterventionCombination(model);
        
        do {
            this.iterations.add( new Iteration(model, interventions.getCombination(), iterationCount));
            System.out.println("intervention set " + interventions.getCombination());
            interventions.nextCombination();            
        } while (interventions.hasNextCombination());

    }
    
    public List<Iteration> getIterations() {
        return iterations;
    }
    
    
}

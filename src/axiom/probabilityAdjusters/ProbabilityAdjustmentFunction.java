/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package axiom.probabilityAdjusters;

import axiom.model.ArgumentException;
import axiom.model.Probability;

/**
 * Probability adjustment function maps probabilities
 * (as instances of <code>Probability</code> or as <code>double</code>)
 * to adjusted probabilities wrapped in <code>Probability</code>.
 * 
 * @author jmpaon
 */
public abstract class ProbabilityAdjustmentFunction {
    
    /**
     * 
     * @param probability
     * @return
     * @throws ProbabilityAdjustmentException 
     */
    public abstract Probability map(Probability probability) throws ProbabilityAdjustmentException, ArgumentException;
    
    /**
     * 
     * @param probability
     * @return
     * @throws ProbabilityAdjustmentException 
     * @throws axiom.model.ArgumentException 
     */
    public Probability map(double probability) throws ProbabilityAdjustmentException, ArgumentException {
        return(map(new Probability(probability)));
    }
    


}

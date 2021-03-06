/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package axiom.probabilityAdjusters;

import axiom.model.ArgumentException;
import axiom.model.Probability;

/**
 * UnsafeProbability adjustment function maps probabilities
 (as instances of <code>UnsafeProbability</code> or as <code>double</code>)
 * to adjusted probabilities wrapped in <code>UnsafeProbability</code>.
 * 
 * @author jmpaon
 */
public abstract class ProbabilityAdjustmentFunction {
    
    /**
     * 
     * @param probability
     * @return
     * @throws ProbabilityAdjustmentException 
     * @throws axiom.model.ArgumentException 
     */
    public abstract Probability map(Probability probability) throws ProbabilityAdjustmentException;
    
//    /**
//     * 
//     * @param probability
//     * @return
//     * @throws ProbabilityAdjustmentException 
//     * @throws axiom.model.ArgumentException 
//     */
//    public Probability map(double probability) throws ProbabilityAdjustmentException {
//        return(map(new Probability(probability)));
//    }
    


}

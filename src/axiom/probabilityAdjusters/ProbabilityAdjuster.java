/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package axiom.probabilityAdjusters;

import axiom.model.ArgumentException;
import axiom.model.Probability;

/**
 * UnsafeProbability adjuster is a collection of probability adjustment functions
 with an identifying name or index.
 * It mainly returns the value the probability adjustment functions map for
 * the non-adjusted probability value passed to <code>adjustedProbability</code>
 * method.
 * 
 * @author juha
 */
public abstract class ProbabilityAdjuster {

    /**
     * Returns the probability adjustment function with name <b>name</b>
     * @param name Name of the probability adjustment function
     * @return The probability adjustment function with name <b>name</b>
     */
    public abstract ProbabilityAdjustmentFunction getFunction(String name);
    
    public abstract int adjustmentFunctionCount();

    /**
     * Returns an adjusted probability, evaluated by the adjustment function
     * indicated by the index.
     * @param probability Double representing a probability, in range [0..1]
     * @param id Identifier of the adjustment function
     * @return Adjusted probability
     * @throws ProbabilityAdjustmentException
     */
    public abstract Probability adjustedProbability(Probability probability, String id) throws ProbabilityAdjustmentException;
    
    public Probability adjustedProbability(double probability, String id) throws ProbabilityAdjustmentException {
        return this.adjustedProbability(new Probability(probability), id);
    }
    
    /**
     * Tests that the probability adjuster has an adjustment function corresponding to index
     * @param index An index of a probability adjustment function
     * @return True if index exists in the probability adjuster, false otherwise
     */
    public abstract boolean adjusterExists(int index);
    
    /**
     * Tests that the probability adjuster has an adjustment function corresponding to name
     * @param name A name of a probability adjustment function
     * @return True if name exists in the probability adjuster, false otherwise
     */    
    public abstract boolean adjusterExists(String name);
        
    
    
}

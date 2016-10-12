/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package axiom.probabilityAdjusters;

import axiom.model.Probability;
import axiom.model.ArgumentException;


/**
 * Maps probabilities to adjusted probabilities in a linear manner.
 * 
 * @author jmpaon
 */
public class LinearProbabilityAdjustmentFunction extends ProbabilityAdjustmentFunction {

    /**
     * The parameter that describes the "strength" or "size"
     * of the adjustment done by this adjustment function instance;
     * how much the probability is adjusted.
     * -1 maps all probabilities to 0, 
     * 0 maps every probability values to itself,
     * 1 maps all probabilities to 1.
     */
    public final double distance;
    
    /**
     * Constructs the linear probability adjustment function.
     * 
     * @param distance Describes the size of the "linear" probability adjustment.
     * Value can range from -1 to 1.
     * @throws ProbabilityAdjustmentException 
     */
    public LinearProbabilityAdjustmentFunction(double distance) throws ProbabilityAdjustmentException {
        if(distance > 1 || distance < (-1)) {
            throw new ProbabilityAdjustmentException
            ("Linear probability adjustment function distance argument must be in range [-1..1]");
        }

        this.distance = distance;
    }
    
    @Override
    public String toString() {
        return "Linear adjustment function with distance " + this.distance;
    }

    @Override
    public Probability map(Probability probability) throws ProbabilityAdjustmentException, ArgumentException {
        
        if(probability.getValue() == 1) probability.setValue(0.999999999999);
        if(probability.getValue() == 0) probability.setValue(0.000000000001);
        
        if(distance >= 0) {
            return adjustByDistance(probability);
        }            
        else {
            return adjustByDistance(probability.getComplement()).getComplement();
        }
    }
    

    
    /**
     * Does the linear adjustment parameterized by <b>distance</b>.
     * @param probability Unadjusted probability
     * @return "Linearly" adjusted probability
     */
    private Probability adjustByDistance(Probability probability) throws ArgumentException {
        double d = Math.abs(this.distance);
        double ap = probability.getValue() * (1 / (1-d) );
        
        if (probability.getValue() + ap > 1) {
            ap = (probability.getValue() * (1-d) + d);
        }
        return new Probability(ap);
    }    
    
    
}

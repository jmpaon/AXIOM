/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package axiom.probabilityAdjusters;

import axiom.model.ArgumentException;
import java.util.HashMap;
import axiom.model.Probability;
import java.util.Map;

/**
 * 
 * @author juha
 */
public class NameProbabilityAdjuster extends ProbabilityAdjuster {
    
    private final Map<String, ProbabilityAdjustmentFunction> adjustmentFunctions;
    
    public NameProbabilityAdjuster() {
        adjustmentFunctions = new HashMap<>();
    }
    
    
    /**
     * Adds a new adjustment function with a specific name to the probability adjuster.
     * No duplicate names are allowed.
     * @param name The identifying name of the adjustment function
     * @param f The probability adjustment function to be added to adjuster
     * @throws ProbabilityAdjustmentException 
     */
    public void addAdjustmentFunction(String name, ProbabilityAdjustmentFunction f) throws ProbabilityAdjustmentException {
        if(name==null) throw new ProbabilityAdjustmentException("Adjustment function name cannot be null");
        if(f==null) throw new ProbabilityAdjustmentException("Adjustment function cannot be null");
        if(adjustmentFunctions.containsKey(name)) throw new ProbabilityAdjustmentException("Duplicate entry of adjustment function with name " + name);
        
        adjustmentFunctions.put(name, f);
    }
    
    
    /**
     * Adds a new adjustment function with an index number for identification to the probability adjuster.
     * Integer index is converted to <code>String</code> representation.
     * @param index Index of the probability adjuster 
     * @param f The probability adjustment function to be added to adjuster
     * @throws ProbabilityAdjustmentException 
     */
    public void addAdjustmentFunction(int index, ProbabilityAdjustmentFunction f) throws ProbabilityAdjustmentException {
        String name = index > 0 ? "+" + String.valueOf(index) : String.valueOf(index);
        addAdjustmentFunction(name, f);
    }    
    
    
    
    @Override
    public Probability adjustedProbability(Probability probability, String name) throws ProbabilityAdjustmentException {
        return adjustmentFunctions.get(name).map(probability);
    }
    
    @Override
    public Probability adjustedProbability(double probability, String id) throws ProbabilityAdjustmentException {
        return adjustedProbability(new Probability(probability), id);
    }


    
    @Override
    public boolean adjusterExists(String name) {
        return adjustmentFunctions.containsKey(name);
    }

    @Override
    public boolean adjusterExists(int index) {
        String name = index > 0 ? "+" + String.valueOf(index) : String.valueOf(index);
        return adjusterExists(name);
    }


    @Override
    public ProbabilityAdjustmentFunction getFunction(String name) {
        assert adjusterExists(name);
        return adjustmentFunctions.get(name);
    }

    @Override
    public int adjustmentFunctionCount() {
        return adjustmentFunctions.size();
    }

    
    
}

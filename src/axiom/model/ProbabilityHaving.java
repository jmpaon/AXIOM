/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package axiom.model;

/**
 *
 * @author juha
 */
public interface ProbabilityHaving {

    /**
     * Returns a <code>Probability</code> identical to this <code>Probability</code>.
     * @return Copy of the <code>Probability</code>.
     */
    public Probability get();

    /**
     * Sets the probability value.
     * @param newProbability The probability value will be equal to value of <code>newProbability</code>.
     */
    public void set(Probability newProbability);
        
    
    /**
     * Returns the value of this probability
     * @return 
     */
    public double getValue();
    
    
    /**
     * Sets the probability value.
     * @param value The probability will be equal to the value.
     */
    public void setValue(double value);
    
    
}

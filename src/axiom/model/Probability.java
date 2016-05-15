/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package axiom.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 *
 * @author juha
 */
public class Probability implements Comparable<Probability>{


    
    private double value;
    private final int precision;
    
    public Probability(double value) throws ArgumentException {
        this(value, 4);
    }
    
    /**
     *
     * @param value
     * @param precision
     * @throws ArgumentException
     */
    public Probability(double value, int precision) throws ArgumentException {
        if(value < 0 || value > 1) throw new ArgumentException("Invalid probability value");
        this.value = value;
        this.precision = precision;        
    }
    
    /**
     * Returns the value of this probability
     * @return 
     */
    public double get() { return round(value, precision); }
    
    public void set(double value) throws ArgumentException {
        if(value < 0 || value > 1) throw new ArgumentException("Invalid probability value ("+value+")");
        this.value = round(value, 4);
    }
    
    /**
     * Sets the probability value.
     * @param newProbability The probability value will be equal to value of <code>newProbability</code>.
     */
    public void set(Probability newProbability) {
        this.value = newProbability.get();
    }
    
    /**
     * Returns the complement of the probability.
     * @return Complement of this probability
     * @throws ArgumentException 
     */
    public Probability getComplement() throws ArgumentException {
        return new Probability(1-value);
    }
    
    @Override
    public String toString() {
        return String.valueOf(get());
    }
    
    
    @Override
    public int compareTo(Probability p) {
        if(this.value < p.value) return -1;
        if(this.value > p.value) return  1;
        return 0;
    }    
    
    
    /**
     * Rounds a number to <code>places</code> decimal places;
     * three might be precise enough for cross-impact analysis probabilities
     * @param d Decimal number
     * @return Decimal number rounded to <code>places</code> places.
     */
    private double round(double value, int places) {
        return new BigDecimal(value).setScale(places, RoundingMode.HALF_UP).doubleValue();
    }


    
    
    
    
}

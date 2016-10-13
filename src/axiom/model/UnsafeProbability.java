/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package axiom.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;

/**
 *
 * @author juha
 */
public class UnsafeProbability implements Comparable<UnsafeProbability> {

    private double value;
    final int precision;
    

    public UnsafeProbability(UnsafeProbability p) {
        this(p.value, p.precision);
    }
    
    public UnsafeProbability(double value) {
        this(value, 4);
    }
    

    public UnsafeProbability(double value, int precision) {
        if(value < 0 || value > 1) throw new IllegalArgumentException("Invalid probability value");
        this.value = value;
        this.precision = precision;        
    }
    
    public UnsafeProbability(UnsafeProbability numerator, UnsafeProbability denominator) {
        assert numerator.value <= denominator.value;
        assert numerator != denominator;
        this.value = numerator.value / denominator.value;
        this.precision = numerator.precision;
    }
    
    public UnsafeProbability(Collection<UnsafeProbability> probabilityItems, int precision) {
        UnsafeProbability sum = new UnsafeProbability(0);
        for(UnsafeProbability p : probabilityItems) sum.add(p);
        this.value = sum.value;
        this.precision = precision;
    }
    
    /**
     * Returns a <code>UnsafeProbability</code> identical to this <code>UnsafeProbability</code>.
     * @return Copy of the <code>UnsafeProbability</code>.
     */
    public UnsafeProbability get() {
        return new UnsafeProbability(this);
    }

    /**
     * Sets the probability value.
     * @param newProbability The probability value will be equal to value of <code>newProbability</code>.
     */
    public void set(UnsafeProbability newProbability) {
        this.value = newProbability.getValue();
    }
    
    /**
     * Returns the value of this probability
     * @return 
     */
    public double getValue() { return round(value, precision); }
    
    public void setValue(double value)  {
        if(value < 0 || value > 1) throw new IllegalArgumentException("Invalid probability value ("+value+")");
        this.value = round(value, precision);
    }
    

    
    /**
     * Returns the complement of the probability.
     * @return Complement of this probability
     */
    public UnsafeProbability getComplement()  {
        return new UnsafeProbability(1-value);
    }
    
    @Override
    public String toString() {
        return String.valueOf(getValue());
    }
    
    
    @Override
    public int compareTo(UnsafeProbability p) {
        if(this.value < p.value) return -1;
        if(this.value > p.value) return  1;
        return 0;
    }

    public void add(UnsafeProbability p) {
        this.set(new UnsafeProbability(this.value + p.value, this.precision));
    }
    
    public void add(Collection<UnsafeProbability> ps) {
        double summed_p=0;
        for(UnsafeProbability p : ps) {
            summed_p += p.getValue();
        }
        this.set(new UnsafeProbability(this.value + summed_p, this.precision));
    }
    
    public void subtract(UnsafeProbability p) {
        this.setValue(this.value - p.value);
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

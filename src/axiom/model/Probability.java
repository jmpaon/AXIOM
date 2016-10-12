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
public class Probability implements Comparable<Probability> {

    private double value;
    final int precision;
    

    public Probability(Probability p) {
        this(p.value, p.precision);
    }
    
    public Probability(double value) {
        this(value, 4);
    }
    

    public Probability(double value, int precision) {
        if(value < 0 || value > 1) throw new IllegalArgumentException("Invalid probability value");
        this.value = value;
        this.precision = precision;        
    }
    
    public Probability(Probability numerator, Probability denominator) {
        assert numerator.value <= denominator.value;
        assert numerator != denominator;
        this.value = numerator.value / denominator.value;
        this.precision = numerator.precision;
    }
    
    public Probability(Collection<Probability> probabilityItems, int precision) {
        Probability sum = new Probability(0);
        for(Probability p : probabilityItems) sum.add(p);
        this.value = sum.value;
        this.precision = precision;
    }
    
    /**
     * Returns a <code>Probability</code> identical to this <code>Probability</code>.
     * @return Copy of the <code>Probability</code>.
     */
    public Probability get() {
        return new Probability(this);
    }

    /**
     * Sets the probability value.
     * @param newProbability The probability value will be equal to value of <code>newProbability</code>.
     */
    public void set(Probability newProbability) {
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
    public Probability getComplement()  {
        return new Probability(1-value);
    }
    
    @Override
    public String toString() {
        return String.valueOf(getValue());
    }
    
    
    @Override
    public int compareTo(Probability p) {
        if(this.value < p.value) return -1;
        if(this.value > p.value) return  1;
        return 0;
    }

    public void add(Probability p) {
        this.set(new Probability(this.value + p.value, this.precision));
    }
    
    public void add(Collection<Probability> ps) {
        double summed_p=0;
        for(Probability p : ps) {
            summed_p += p.getValue();
        }
        this.set(new Probability(this.value + summed_p, this.precision));
    }
    
    public void subtract(Probability p) {
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

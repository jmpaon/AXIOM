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
public class SafeProbability {
    
    private int numerator;
    private static final int DENOMINATOR = 1000000;
    private static final int DEFAULT_PRECISION = 5;
    
    
    public SafeProbability(double value) {
        assert value >= 0 && value <= 1;
        this.numerator = (int)(value * DENOMINATOR);
    }
    
    
    public SafeProbability(SafeProbability p) {
        this.numerator = p.numerator;
    }
    
    
    private SafeProbability(int numerator) {
        assert numerator >= 0 && numerator <= DENOMINATOR;
        this.numerator = numerator;
    }
    
    public int getNumerator() {
        return numerator;
    }
    
    
    
    public SafeProbability complement() {
        return new SafeProbability(DENOMINATOR-numerator);
    }
    
    public void add(SafeProbability p) {
        assert this.numerator + p.numerator <= DENOMINATOR;
        this.numerator += p.numerator;
    }
    
    public void add(Collection<SafeProbability> collection) {
        for(SafeProbability p : collection) this.add(p);
    }
    
    public void subtract(SafeProbability p) {
        assert this.numerator - p.numerator >= 0;
        this.numerator -= p.numerator;
    }
    
    public void subtract(Collection<SafeProbability> collection) {
        for(SafeProbability p : collection) this.subtract(p);
    }
    
    
    public boolean equals(Object o) {
        if(o == null) return false;
        if(!SafeProbability.class.isAssignableFrom(o.getClass())) return false;
        final SafeProbability p = (SafeProbability) o;
        return this.numerator == p.numerator;
    }
    

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + this.numerator;
        return hash;
    }
    
    public double toDouble() {
        double val = (double)numerator/DENOMINATOR;
        return round(val, DEFAULT_PRECISION);
    }
    
    public double toDouble(int precision) {
        double val = (double)numerator/DENOMINATOR;
        return round(val, precision);
    }
    
    @Override
    public String toString() {
        return String.valueOf(this.toDouble());
    }
    
    public String toString_fraction() {
        return this.numerator + "/" + this.DENOMINATOR;
    }

    
    public static boolean isValidDistribution(Collection<SafeProbability> distribution) {
        return residual(distribution)==0;
    }
    
    public static int residual(Collection<SafeProbability> distribution) {
        int sum = 0;
        for(SafeProbability p : distribution) sum += p.numerator;
        return DENOMINATOR - sum;
    }
    
    
    /**
     * Rounds a number to <code>places</code> decimal places;
     * three might be precise enough for cross-impact analysis probabilities
     * @param d Decimal number
     * @return Decimal number rounded to <code>places</code> places.
     */
    public static double round(double value, int places) {
        return new BigDecimal(value).setScale(places, RoundingMode.HALF_UP).doubleValue();
    }    
            
            
    
    
    
    
    
    
    
    
    
}

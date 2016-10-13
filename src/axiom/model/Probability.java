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
    
    private int numerator;
    private static final int DENOMINATOR = 1000000;
    private static final int DEFAULT_PRECISION = 4;
    
    
    public Probability(double value) {
        assert value >= 0 && value <= 1 : "value " + value + " is out of bounds [0,1]";
        this.numerator = (int)(value * DENOMINATOR);
    }
    
    
    public Probability(Probability p) {
        this.numerator = p.numerator;
    }
    
    
    Probability(int numerator) {
        assert numerator >= 0 && numerator <= DENOMINATOR
                : "numerator is " + numerator;
        this.numerator = numerator;
    }
    
    
    
    public Probability get() {
        return new Probability(this);
    }
    
    public int getNumerator() {
        return numerator;
    }
    
    public void set(Probability p) {
        this.numerator = p.numerator;
    }
    
    public void set(double p) {
        set(new Probability(p));
    }
    
    /**
     * Sets a new value for <code>Probability</code> and performs secondary probability adjustment.
     * @param newProbability New probability value will be identical to <b>newProbability</b> value
     * @param secondaryProbabilities Other probabilities in the same probability distribution
     */
    public void setWithSecondaryAdjustment(Probability newProbability, Collection<Probability> secondaryProbabilities) {
        Probability oldComplement = new Probability(sumNumerators(secondaryProbabilities));
        Probability newComplement = newProbability.complement();
        this.set(newProbability); // Primary adjustment
        for(Probability sp : secondaryProbabilities) {
            sp.numerator *= ((double)newComplement.numerator / oldComplement.numerator);
        }
        
        int residual = DENOMINATOR - (this.numerator + sumNumerators(secondaryProbabilities)) ;
        System.out.println("Residual is " +residual);
        this.correct(residual);
        assert this.numerator + sumNumerators(secondaryProbabilities) == DENOMINATOR;
        
    }
    
    /**
     * This method is used to correct a numerator value in case 
     * where there is residual left from secondary probability adjustment.
     * @param addToNumerator The residual to be added to <code>this.numerator</code>.
     */
    private void correct(int addToNumerator) {
        assert this.numerator + addToNumerator <= DENOMINATOR : "Corrected to >1: "+  (this.numerator+addToNumerator);
        assert this.numerator + addToNumerator >= 0 : "Corrected to <0";
        this.numerator += addToNumerator;
    }    
    
    public Probability complement() {
        return new Probability(DENOMINATOR-numerator);
    }

    
    public void add(Probability p) {
        assert this.numerator + p.numerator <= DENOMINATOR : "added p is " + (this.numerator+p.numerator);
        this.numerator += p.numerator;
    }
    
    public void add(Collection<Probability> collection) {
        for(Probability p : collection) this.add(p);
    }
    
    public void subtract(Probability p) {
        assert this.numerator - p.numerator >= 0 : "subtracted p is " + (this.numerator+p.numerator);
        this.numerator -= p.numerator;
    }
    
    public void subtract(Collection<Probability> collection) {
        for(Probability p : collection) this.subtract(p);
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
    
    public String toStringAsFraction() {
        return String.format("%7s/%s", this.numerator, this.DENOMINATOR);
        //return this.numerator + "/" + this.DENOMINATOR;
    }
    
    public static int sumNumerators(Collection<Probability> distribution) {
        return distribution.stream().mapToInt(i -> i.getNumerator()).sum();
    }

    
    public static boolean isValidDistribution(Collection<Probability> distribution) {
        return residual(distribution)==0;
    }
    
    public static int residual(Collection<Probability> distribution) {
        int sum = 0;
        for(Probability p : distribution) sum += p.numerator;
        return DENOMINATOR - sum;
    }
    
    public static Probability random() {
        return new Probability((int)(Math.random() * (DENOMINATOR + 1)));
    }
    
    
    @Override
    public boolean equals(Object o) {
        if(o == null) return false;
        if(!Probability.class.isAssignableFrom(o.getClass())) return false;
        final Probability p = (Probability) o;
        return this.numerator == p.numerator;
    }
    

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + this.numerator;
        return hash;
    }
    
    

    @Override
    public int compareTo(Probability p) {
        if(this.numerator > p.numerator) return  1;
        if(this.numerator < p.numerator) return -1;
        return 0;
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

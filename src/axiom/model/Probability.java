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
 * This class represents a probability.
 * It wraps an int that represents the numerator;
 * this solution is safe from the point of view of having valid 
 * probability distributions (whose members' probabilities' sum equals 1)
 * @author juha
 */
public class Probability implements Comparable<Probability> {
    
    private int numerator;
    private static final int DENOMINATOR = 1000000;
    private static final int DEFAULT_PRECISION = 4;
    
    /**
     * Constructor for Probability.
     * @param value real from range [0,1]
     */
    public Probability(double value) {
        assert value >= 0 && value <= 1 : "value " + value + " is out of bounds [0,1]";
        this.numerator = (int)(value * DENOMINATOR);
    }
    
    /**
     * Constructor for Probability.
     * @param p A <code>Probability</code> that has same value as the new probability.
     */
    public Probability(Probability p) {
        this.numerator = p.numerator;
    }
    
    /**
     * Constructor for Probability.
     * @param numerator Value of the numerator of the new <code>Probability</code>
     */
    Probability(int numerator) {
        assert numerator >= 0 && numerator <= DENOMINATOR
                : "numerator is " + numerator;
        this.numerator = numerator;
    }
    
    /**
     * Returns a copy of the probability.
     * @return A copy of the probability.
     */
    public Probability get() {
        return new Probability(this);
    }
    
    /**
     * Returns the value of the numerator.
     * @return The value of the numerator.
     */
    public int getNumerator() {
        return numerator;
    }
    
    /**
     * Sets the numerator of the probability equal to the numerator of <b>p</b>.
     * @param p A <code>Probability</code>
     */
    public void set(Probability p) {
        this.numerator = p.numerator;
    }
    
    /**
     * Sets the numerator to FLOOR(DENOMINATOR * p)
     * @param p A real from range [0,1]
     */
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
        this.correct(residual);
        assert this.numerator + sumNumerators(secondaryProbabilities) == DENOMINATOR;
        
    }
    
    /**
     * This method is used to correct a numerator value in case 
 where there is requiredDistributionCorrection left from secondary probability adjustment.
     * @param addToNumerator The requiredDistributionCorrection to be added to <code>this.numerator</code>.
     */
    void correct(int addToNumerator) {
        assert this.numerator + addToNumerator <= DENOMINATOR : "Corrected to >1: "+  (this.numerator+addToNumerator);
        assert this.numerator + addToNumerator >= 0 : "Corrected to <0";
        this.numerator += addToNumerator;
    }    
    
    /**
     * Returns a probability that is the complement of this probability.
     * @return A complement of this probability.
     */
    public Probability complement() {
        return new Probability(DENOMINATOR-numerator);
    }

    
    /**
     * Adds the numerator of <b>p</b> to the numerator.
     * @param p 
     */
    public void add(Probability p) {
        assert this.numerator + p.numerator <= DENOMINATOR : "added p is " + (this.numerator+p.numerator);
        this.numerator += p.numerator;
    }
    
    /**
     * Adds the sum of all numerators in <b>probabilityCollection</b> to the numerator.
     * @param probabilityCollection A collection of probabilities.
     */
    public void add(Collection<Probability> probabilityCollection) {
        for(Probability p : probabilityCollection) this.add(p);
    }
    
    /**
     * Subtracts the numerator of <b>p</b> from the numerator.
     * @param p 
     */
    public void subtract(Probability p) {
        assert this.numerator - p.numerator >= 0 : "subtracted p is " + (this.numerator+p.numerator);
        this.numerator -= p.numerator;
    }
    
    /**
     * Subtracts the sum of all numerators in <b>probabilityCollection</b> from the numerator.
     * @param probabilityCollection A collection of probabilities.
     */    
    public void subtract(Collection<Probability> collection) {
        for(Probability p : collection) this.subtract(p);
    }
    
    /**
     * Returns a new probability from which <b>p</b> has been subtracted from.
     * @param p A probability
     * @return Probability from which <b>p</b> has been subtracted.
     */
    public Probability getSubtract(Probability p) {
        Probability tp = this.get();
        tp.subtract(p);
        return tp;
    }
    
    /**
     * Returns the value of this probability as double (in range [0,1])
     * @return Probability value as double
     */
    public double toDouble() {
        double val = (double)numerator/DENOMINATOR;
        return round(val, DEFAULT_PRECISION);
    }
    
    /**
     * Returns the value of this probability as double (in range [0,1]) with precision <b>precision</b>.
     * @param precision The precision of the double representation.
     * @return Probability value as double
     */
    public double toDouble(int precision) {
        double val = (double)numerator/DENOMINATOR;
        return round(val, precision);
    }
    
    @Override
    public String toString() {
        return String.valueOf(this.toDouble());
    }
    
    /**
     * Returns String representation of the probability in format NUMERATOR/DENOMINATOR.
     * @return String representation of the probability in format NUMERATOR/DENOMINATOR.
     */
    public String toStringAsFraction() {
        return String.format("%7s/%s", this.numerator, this.DENOMINATOR);
    }
    
    /**
     * Sums the numerator values in a Probability collection representing a distribution.
     * @param distribution Collection of probabilities.
     * @return 
     */
    public static int sumNumerators(Collection<Probability> distribution) {
        return distribution.stream().mapToInt(i -> i.getNumerator()).sum();
    }

    /**
     * Tests that distribution is <i>valid</i> in the sense that 
     * the sum of the numerators in the distribution is equal to DENOMINATOR value.
     * @param distribution Collection of probabilities.
     * @return 
     */
    public static boolean isValidDistribution(Collection<Probability> distribution) {
        return requiredDistributionCorrection(distribution)==0;
    }
    
    /**
     * returns an int signifying what is the difference of DENOMINATOR
     * and the sum of nominators in distribution.
     * @param distribution Collection of probabilities.
     * @return Difference of DENOMINATOR and the sum of nominators in distribution.
     */
    public static int requiredDistributionCorrection(Collection<Probability> distribution) {
        int sum = 0;
        for(Probability p : distribution) sum += p.numerator;
        return DENOMINATOR - sum;
    }
    
    /**
     * Returns a new probability with random value.
     * @return a new probability with random value.
     */
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

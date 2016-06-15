/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package exit;


import java.math.BigDecimal;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;



/**
 * <code>CrossImpactMatrix</code> represents a table of values 
 variables have on each other in EXIT cross-impact analysis.
 * Impacts are usually integers, ranging from 
 * negative <b>maxImpact</b> to positive <b>maxImpact</b>.
 Negative impact of variable X on variable Y represents
 probability-decreasing effect of X on Y; 
 Positive impact of variable X on variable Y represents
 probability-increasing effect of X on Y.
 Cross-impact matrix can be used both for representing the direct values
 that are inputs for the EXIT calculation 
 and the summed direct and indirect values that are the result of the
 EXIT calculation.
 * 
 * @author jmpaon
 */
public final class CrossImpactMatrix extends SquareDataMatrix {
    
    private double maxImpact;

    
    
    public CrossImpactMatrix(double maxImpact, int varCount, boolean onlyIntegers, String[] names, double[] impacts) {
        super(varCount, onlyIntegers, names, impacts);
        
        if(maxImpact <= 0) {throw new IllegalArgumentException("maxImpact must be greater than 0");}
        if(varCount < 2) {throw new IllegalArgumentException("Cross-impact matrix must have at least 2 variables");}
        
        this.maxImpact = maxImpact;
    }
    
    /**
     * Constructor for <code>CrossImpactMatrix</code>.
     * @param maxImpact The maximum value values in the matrix can have.
     * Minimum allowed value is negative <b>maxImpact</b> and maximum <b>maxImpact</b>.
     * <b>maxImpact</b> must be greater than 0.
     * @param varCount The number of variables in the matrix; 
     * will be also the number of rows and the number of columns.
     * @param onlyIntegers <b>true</b> if matrix contains only integers
     * @param names <code>String</code> array of variable names
     */
    public CrossImpactMatrix(double maxImpact, int varCount, boolean onlyIntegers, String[] names)  {
        this(maxImpact, varCount, onlyIntegers, names, new double[varCount*varCount]);
    }


    /**
     * Constructor for <code>CrossImpactMatrix</code>.
     * @param maxImpact The maximum value values in the matrix can have.
     * Minimum allowed value is <b>-maxImpact</b> and maximum <b>maxImpact</b>.
     * @param varCount The number of variables in the matrix; 
     * @param onlyIntegers <b>true</b> if matrix contains only integers
     * @see CrossImpactMatrix#CrossImpactMatrix(double, int, boolean, java.lang.String[], double[]) 
     */
    public CrossImpactMatrix(double maxImpact, int varCount, boolean onlyIntegers) {
        this(maxImpact, varCount, onlyIntegers, createNames(varCount));
    }
    
    
    /**
     * Constructor for <code>CrossImpactMatrix</code>.
     * @param maxImpact The maximum value values in the matrix can have.
     * Minimum allowed value is <b>-maxImpact</b> and maximum <b>maxImpact</b>.
     * <b>maxImpact</b> must be greater than 0.
     * @param varCount The number of variables in the matrix; 
     * will be also the number of rows and the number of columns.
     */
    public CrossImpactMatrix(double maxImpact, int varCount) {
        this(maxImpact, varCount, true);
    }
    
    

    
    
    
    /**
     * Calculates and returns 
     * a new <code>CrossImpactMatrix</code> that contains
     * the summed direct and indirect values between the variables.
     * In the returned matrix, 
     * impactor variables are in rows 
     * and impacted variables are in columns.
     * @param impactTreshold The low bound for inclusion for the impact of chains that are summed in the matrix. 
     * See {@link ImpactChain#highImpactChainsIntermediary(double)}.
     * @return <code>CrossImpactMatrix</code> with the summed direct and indirect values between variables
     * @deprecated This calculation strategy is highly inefficient.
     */
    @Deprecated
    public CrossImpactMatrix summedImpactMatrix_slow(double impactTreshold) {
        
        Reporter.indicateProgress(String.format("Begin search of impact chains having impact of at least %1.3f...%n", impactTreshold), 3);
        CrossImpactMatrix iim = new CrossImpactMatrix(this.maxImpact, this.varCount, false, names);
        int chainsProcessedCount=0;
        
        for(int impactor = 1 ; impactor <= iim.varCount ; impactor++ ) {
            for(int impacted = 1 ; impacted <= iim.varCount ; impacted++ ) {
                double impactSum = 0;
                if(impactor != impacted) {
                    
                    Reporter.indicateProgress(String.format(
                            "Calculating impacts of %4s (%10s) on %4s (%10s)...", 
                            "V"+impactor, 
                            truncateName(this.getName(impactor),10), 
                            "V"+impacted, 
                            truncateName(this.getName(impacted),10)
                    ), 3);
                    
                    List<ImpactChain> chains = this.indirectImpacts(impactor, impacted, impactTreshold);
                    
                    int counter = 0;
                    for (ImpactChain chain : chains) {
                        impactSum += chain.impact();
                        counter++;
                    }
                    Reporter.indicateProgress(String.format(" %5d significant impact chains found with total impact sum of %4.2f%n", counter, impactSum), 3);
                    chainsProcessedCount += counter;
                }
                if(iim.maxImpact < Math.abs(impactSum)) {
                    iim.setMaxImpact(Math.abs(impactSum));
                }
                
                iim.setImpact(impactor, impacted, impactSum);
            }
        }
        
        Reporter.indicateProgress(String.format("Total of %d significant (treshold %1.2f) impact chains found in the matrix.%n", chainsProcessedCount, impactTreshold), 5);
        Reporter.indicateProgress(String.format("The total number of possible chains in this matrix is %s.%n", approximateChainCountString()),2);
        return iim;
    }
    
    
    /**
     * Calculates and returns 
     * a new <code>CrossImpactMatrix</code> that contains
 the summed direct and indirect values between the variables.
     * In the returned matrix, 
     * impactor variables are in rows 
     * and impacted variables are in columns.
     * @param impactTreshold The low bound for inclusion for the impact of chains that are summed in the matrix. 
     * See {@link ImpactChain#highImpactChains(double)}.
     * @return <code>CrossImpactMatrix</code> with the summed direct and indirect values between variables
     */
    public CrossImpactMatrix summedImpactMatrix(double impactTreshold) {
        CrossImpactMatrix resultMatrix = new CrossImpactMatrix(maxImpact*varCount, varCount, false, names);
        double totalCount=0;
        for(int impactor=1; impactor<=this.varCount; impactor++) {
            Reporter.msg("Calculating significant indirect impacts of %s(%s)... ", getNameShort(impactor), truncateName(getName(impactor), 15));
            ImpactChain chain = new ImpactChain(this, Arrays.asList(impactor));
            double count = sumImpacts(chain, impactTreshold, resultMatrix);
            totalCount += count;
            Reporter.msg("%10.0f significant (treshold %1.4f) impact chains found%n", count, impactTreshold);
        }
        Reporter.msg("%s significant (treshold %1.4f) impact chains found in the matrix.%n", Math.round(totalCount), impactTreshold);
        Reporter.msg("The total number of possible chains in this matrix is %s.%n", approximateChainCountString());
        resultMatrix.setMaxImpact(resultMatrix.greatestValue());
        return resultMatrix;
    }
    
    
    /**
     * If impact of <b>chain</b> is higher than <b>impactTreshold</b>,
     * it is added to <b>resultMatrix</b> and the possible immediate expansions of
     * <b>chain</b> are generated and their values added to <b>resultMatrix</b>
     * if appropriate.
     * @param chain <code>ImpactChain</code> to consider for addition to <b>resultMatrix</b>
     * @param impactTreshold <b>chain</b> must have an impact of at least this value to be added to <b>resultMatrix</b>
     * @param resultMatrix <code>CrossImpactMatrix</code> where the significant values are summed
     * @return count of significant impact chains found in <b>chain</b> and its expansions.
     */
    private double sumImpacts(ImpactChain chain, double impactTreshold, CrossImpactMatrix resultMatrix) {
        
        double count = 0;
        if(Math.abs(chain.impact()) >= impactTreshold) {
            count++;
            int impactor = chain.impactorIndex();
            int impacted = chain.impactedIndex();
            double accumulatedValue = resultMatrix.getValue(impactor, impacted);
            double additionValue    = chain.impact();
            double newValue         = accumulatedValue + additionValue;
            
            if(impactor != impacted) {
                if(resultMatrix.maxImpact < Math.abs(newValue)) {
                    resultMatrix.setMaxImpact(Math.abs(Math.round((accumulatedValue + additionValue)*1.5)));
                }
                resultMatrix.setImpact(impactor, impacted, newValue);
            }
            
            if(chain.hasExpansion()) {
                Set<ImpactChain> expansions = chain.continuedByOne();
                for(ImpactChain ic : expansions) {
                    count += sumImpacts(ic, impactTreshold, resultMatrix);
                }
            }
        }
        return count;
    }
    

    /**
     * Scales an impact matrix to have its values within a certain range.
     * @param scaleTo The value that the highest absolute impact value in the matrix will be scaled to
     * @return SquareDataMatrix scaled according to <b>scaleTo</b> argument.
     */
    public CrossImpactMatrix scaleByMax(double scaleTo) {
        if(scaleTo == 0) throw new IllegalArgumentException("scaleTo cannot be 0");
        double max = greatestValue();
        double[] scaledImpacts = this.values.clone();
        for(int i = 0; i < values.length; i++) {
            scaledImpacts[i] = values[i] / max * scaleTo;
        }
        return new CrossImpactMatrix(Math.abs(scaleTo), this.varCount, this.onlyIntegers, this.names, scaledImpacts);
    }
    
    public CrossImpactMatrix driverDriven() {
        throw new UnsupportedOperationException("Not implemented");
    }
    
    /**
     * Creates an importance matrix from the impact matrix.
     * Importance for each impact is calculated by comparing the
     * impact an impactor has on impacted relative to all the impacts on the impacted.
     * <table><tr><th>Absolute impact</th><th>Importance</th></tr><tr><td>&gt;0.5</td><td>3/-3</td></tr><tr><td>&gt;0.25</td><td>2/-2</td></tr><tr><td>&gt;0.1</td><td>1/-1</td></tr><tr><td>&lt;=0.1</td><td>0</td></tr></table>
     * 
     * @return 
     */
    public CrossImpactMatrix importanceMatrix() {
        CrossImpactMatrix importanceMatrix = new CrossImpactMatrix(10, this.varCount, true, this.names);
        for (int impacted=1; impacted<=this.varCount; impacted++) {
            for (int impactor=1; impactor <= this.varCount; impactor++) {
                double shareOfAbsoluteSum = this.columnSum(impacted, true) != 0 ? 
                        this.getValue(impactor, impacted) /  this.columnSum(impacted, true) 
                        : 0;
                //int importance = (int) Math.round(shareOfAbsoluteSum * 3);
                double absShare = Math.abs(shareOfAbsoluteSum);
                int importance = 
                          absShare > 0.5 ? 3
                        : absShare > 0.25 ? 2
                        : absShare > 0.10 ? 1
                        : 0;
                importance = shareOfAbsoluteSum < 0 ? -importance : importance ;
                
                importanceMatrix.setImpact(impactor, impacted, importance);
            }
        }
        return importanceMatrix;
    }
    
    
    public String reportDrivingVariables() {
        String report = "";
        CrossImpactMatrix m = this.scaleByMax(1);
        for(int i = 1 ; i<=varCount; i++) {
            report += String.format("Important drivers for %s:%n", m.getName(i));
            for(Integer imp : m.aboveAverageImpactors(i)) {
                report += String.format("\t%s (%1.1f)%n", m.getName(imp), m.getValue(imp, i));
            }
        }
        return report;
    }
    
    List<Integer> aboveAverageImpactors(int varIndex) {
        List<Integer> impactors = new LinkedList<>();
        CrossImpactMatrix normMatrix = this.scaleByMax(1);
        double average = normMatrix.columnAverage(varIndex);
        for(int i=1; i<=normMatrix.varCount; i++) {
            if(Math.abs(normMatrix.getValue(i, varIndex)) >= average)
                impactors.add(i);
        }
        return impactors;
    }
    
 
    
    /**
     * Tests if impact values of this matrix deviate 
     * from the impact values of <b>impactMatrix</b>
     * at most by value of <b>maxDeviation</b>
     * @param impactMatrix Cross-impact matrix to compare against this one in terms of impact sizes
     * @param maxDeviation The maximum relative deviation allowed to still consider the matrices approximately same in terms of impact sizes
     * @return <b>true</b> if 
     */
    boolean areImpactsApproximatelySame(CrossImpactMatrix impactMatrix, double maxDeviation) {
        if(impactMatrix.values.length != this.values.length) throw new IllegalArgumentException("Matrices are differently sized and cannot be compared");
        for(int i=0; i<this.values.length; i++) {
            double v1 = this.values[i], v2 = impactMatrix.values[i];
            double rel = v1 > v2 ? v1 / v2 : v2 / v1;
            if((1-rel) > maxDeviation) return false;
        }
        return true;
    }
    
    
    /**
     * Returns a String with information about 
     * the count or approximate count of possible impact chains
     * for this matrix.
     * Count is exact when <i>varCount</i> is smaller than 15
     * and approximate when <i>varCount</i> is greater.
     * @return String for printing out information about count of possible chains
     * in the matrix
     */
    String approximateChainCountString() {
        int n = varCount;
        double chainCount = approximateChainCount() ;
        
        if(varCount < 15) {
            return new BigDecimal(chainCount).toBigInteger().toString();
        } else {
            int exp = 0;
            while(chainCount >= 10) {
                chainCount /= 10;
                exp ++ ;
            }
            return String.format("approximately %1.2f x 10^%d", chainCount, exp);
        }

    }
    
    /**
     * @return The number of possible impact chains in this matrix.
     */
    private double approximateChainCount() {
        int n = this.varCount-1;
        double count = 0;
        while(n >= 0) {
            count += (factorial(varCount) / factorial(n));
            n--;
        }
        return count;
    }
    
    /**
     * Returns the factorial of <i>n</i>.
     * @param n 
     * @return Factorial of <i>n</i>.
     */
    private double factorial(int n) {
        if(n == 1 || n == 0) return 1;
        return n * factorial(n-1);
    }
    
    public void setImpact(int impactor, int impacted, double value) {
        
        // Absolute value of impact cannot be greater than maxImpact
        if (maxImpact < Math.abs(value)) {
            throw new IllegalArgumentException(String.format("Value %2.2f is bigger than max value %2.2f", value, maxImpact));
        }
        
        super.setValue(impactor, impacted, value);
    }
    
    
    
    
    /**
     * @return The defined maximum value for values in this matrix.
     */
    public double getMaxImpact() {
        return maxImpact;
    }       
    
    
    /**
     * Sets a new value for the <b>maxImpact</b> of the matrix.
     * @param newMaxImpact New  <b>maxImpact</b> value
     */
    void setMaxImpact(double newMaxImpact) {
        if (newMaxImpact <= 0) {
            throw new IllegalArgumentException("maxImpact cannot be 0 or smaller");
        }
        for (Double i : values) {
            if (i > newMaxImpact) {
                throw new IllegalArgumentException("impacts array contains values greater than new max impact");
            }
        }
        this.maxImpact = newMaxImpact;
    }    
    
    
    /**
     * Generates a list of impact chains possible in this matrix 
     * that have impact value greater than <b>treshold</b>.
     * @param impactOf Index of impactor variable. 
     * Only chains starting with this variable are included in returned list.
     * Can also be null; if null, chains starting with any variable 
     * are generated and returned.
     * @param impactOn Index of impacted variable. 
     * Only chains ending in this variable are included in returned list.
     * Can also be null; if null, chains ending in any variable 
     * are generated and returned.
     * @param treshold The required minimum impact a chain must have to be included in returned list.
     * @return A <code>List</code> of impact chains with impact higher than <i>treshold</i> and that have
     * the impactor and impacted variables specified in the <b>impactOf</b> and <b>impactOn</b> arguments.
     */
    List<ImpactChain> indirectImpacts(Integer impactOf, Integer impactOn, double treshold) {
        
        if(impactOf != null && (impactOf <1 || impactOf > varCount)) throw new IndexOutOfBoundsException("impactOf index is not present in the matrix");
        if(impactOn != null && (impactOn <1 || impactOn > varCount)) throw new IndexOutOfBoundsException("impactOn index is not present in the matrix");
        if(treshold <=0 || treshold > 1) throw new IllegalArgumentException("treshold value is not in range ]0..1]");
        
        List<Integer> initialChain = null;
        if(impactOf != null) {
            initialChain = new LinkedList<>(Arrays.asList(impactOf));
        }
        
        ImpactChain ic = new ImpactChain(this, initialChain);
        
        Set<ImpactChain> chains = ic.highImpactChains(treshold);
        
        if(impactOn != null) {
            chains = chains.stream()
                    .filter(c -> c.impactedIndex() == impactOn)
                    .collect(Collectors.toSet());
        }
        
        return chains.stream()
                .sorted(new ImpactComparator())
                .filter(c -> c.memberCount > 1)
                .collect(Collectors.toList());
    }

    
    

    
    
    /**
     * @return A string representation of the impact matrix.
     */
    @Override
    public String toString() {
        int labelWidth = 55;
        int i=0, c, n=0;
        String stringRepresentation="";
        
        stringRepresentation += String.format("%"+labelWidth+"s     \t", " ");
        for(c=0; c<varCount;c++) {
            stringRepresentation += String.format("%s\t", "V"+(c+1));
        }
        stringRepresentation += String.format("%n");
        
        while( i < values.length) {
            stringRepresentation += String.format("%"+labelWidth+"s (%s)\t", truncateName(names[n], labelWidth), ("V"+(n+1)));
            n++;
            c=0;
            while(c < varCount) {
                if(this.onlyIntegers) {
                    DecimalFormat fmt = new DecimalFormat("+#,##0;-#");
                    if(values[i] == 0) 
                        {stringRepresentation += " 0\t"; } 
                    else 
                        {stringRepresentation += fmt.format((int)values[i]) + "\t"; }
                    //stringRepresentation += String.format("%2d\t", (int)values[i]);
                } else {
                    DecimalFormat fmt = new DecimalFormat("+#,##0.00;-#");
                    stringRepresentation += fmt.format(values[i]) +"\t";
                    //stringRepresentation += String.format("%2.2f\t", values[i]);
                }
                
                c++;
                i++;
            }
            stringRepresentation += String.format("%n");
        }
        return stringRepresentation;
    }
    
    

    @Override
    public boolean equals(Object impactMatrix){
        if(! (impactMatrix instanceof CrossImpactMatrix)) return false;
        return this.hashCode() == impactMatrix.hashCode();
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 29 * hash + (int) (Double.doubleToLongBits(this.maxImpact) ^ (Double.doubleToLongBits(this.maxImpact) >>> 32));
        hash = 29 * hash + this.varCount;
        hash = 29 * hash + Arrays.hashCode(this.values);
        hash = 29 * hash + Arrays.deepHashCode(this.names);
        return hash;
    }

    
}

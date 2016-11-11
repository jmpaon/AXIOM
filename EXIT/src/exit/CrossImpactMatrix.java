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
 * <p><code>CrossImpactMatrix</code> represents 
 * a set of impacts variables have on each other 
 * in EXIT cross-impact analysis.
 * Impacts are usually integers, ranging from 
 * negative <b>maxImpact</b> to positive <b>maxImpact</b>.
 * Negative impact of variable X on variable Y represents
 * probability-decreasing effect of X on Y; 
 * Positive impact of variable X on variable Y represents
 * probability-increasing effect of X on Y.</p>
 * 
 * <p>Cross-impact matrix can be used both for representing 
 * the matrix of direct impact values
 * that are inputs for the EXIT calculation 
 * and the matrix of summed direct and indirect values 
 * that are the result of the EXIT calculation.
 * It is also used for presenting the many transformations 
 * that can be performed on
 * both input matrices and summed matrices,
 * to facilitate interpretation and analysis of the impact matrices.
 * These transformations include
 * <ul>
 *  <li>scaling the matrix {@link CrossImpactMatrix#scale(double) }</li>
 *  <li>rounding the matrix {@link CrossImpactMatrix#round(int) }</li>
 *  <li>deriving an importance matrix {@link CrossImpactMatrix#importanceMatrix() }</li>
 *  <li>deriving a difference matrix {@link CrossImpactMatrix#differenceMatrix(exit.CrossImpactMatrix) }</li>
 * </ul>
 * </p>
 * @author jmpaon
 * @version 0.9
 */
public final class CrossImpactMatrix extends SquareMatrix {
    
    /** The maximum absolute value allowed in this matrix */
    private double maxImpact;
    
    /**
     * Constructor for <code>CrossImpactMatrix</code>.
     * @param maxImpact The maximum value values in the matrix can have.
     * Minimum allowed value is negative <b>maxImpact</b> and maximum <b>maxImpact</b>.
     * <b>maxImpact</b> must be greater than 0.
     * @param varCount The number of variables in the matrix; 
     * will be also the number of rows and the number of columns.
     * @param onlyIntegers <b>true</b> if matrix contains only integers
     * @param names <code>String</code> array of variable names
     * @param impacts array containing matrix contents
     */
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
     * Creates a <tt>CrossImpactMatrix</tt> from a <tt>SquareMatrix</tt>.
     * @param matrix <tt>SquareMatrix</tt> that is a valid <tt>CrossImpactMatrix</tt>
     */
    public CrossImpactMatrix(SquareMatrix matrix) {
        this(matrix.greatestValue(), matrix.varCount, matrix.allValuesAreIntegers(), matrix.names.clone(), matrix.values.clone());
        for(int i=1;i<=this.varCount;i++) {
            assert this.getValue(i, i) == 0 : "Impact of hypothesis on itself not allowed";
        }        
    }
    
    
    
    
    /**
     * Calculates and returns 
     * a new <code>CrossImpactMatrix</code> that contains
     * the summed direct and indirect values between the variables.
     * In the returned matrix, 
     * impactor variables are in rows 
     * and impacted variables are in columns.
     * @param impactThreshold The low bound for inclusion for the impact of chains that are summed in the matrix. 
     * See {@link ImpactChain#highImpactChains(double)}.
     * @return <code>CrossImpactMatrix</code> with the summed direct and indirect values between variables
     */
    public CrossImpactMatrix summedImpactMatrix(double impactThreshold) {
        CrossImpactMatrix resultMatrix = new CrossImpactMatrix(maxImpact*varCount, varCount, false, names);
        double totalCount=0;
        for (int impactor=1; impactor<=this.varCount; impactor++) {
            Reporter.msg("Calculating significant indirect impacts of %s(%s)... ", getNameShort(impactor), truncateName(getName(impactor), 15));
            ImpactChain chain = new ImpactChain(this, Arrays.asList(impactor));
            double count = sumImpacts(chain, impactThreshold, resultMatrix);
            totalCount += count;
            Reporter.msg("%10.0f significant (threshold %1.4f) impact chains found%n", count, impactThreshold);
        }
        Reporter.msg("%s significant (threshold %1.4f) impact chains found in the matrix.%n", Math.round(totalCount), impactThreshold);
        Reporter.msg("The total number of possible chains in this matrix is %s.%n", approximateChainCountString());
        resultMatrix.setMaxImpact(resultMatrix.greatestValue());
        return resultMatrix;
    }
    
    
    /**
     * If impact of <b>chain</b> is higher than <b>impactThreshold</b>,
     * it is added to <b>resultMatrix</b> and the possible immediate expansions of
     * <b>chain</b> are generated 
     * and {@link CrossImpactMatrix#sumImpacts(exit.ImpactChain, double, exit.CrossImpactMatrix)}
     * is called recursively on the expansion chains.
     * @param chain <code>ImpactChain</code> to consider for addition to <b>resultMatrix</b>
     * @param impactThreshold <b>chain</b> must have an impact of at least this value to be added to <b>resultMatrix</b>
     * @param resultMatrix <code>CrossImpactMatrix</code> where the significant values are summed
     * @return count of significant impact chains found in <b>chain</b> and its expansions.
     */
    private double sumImpacts(ImpactChain chain, double impactThreshold, CrossImpactMatrix resultMatrix) {
        
        double count = 0;
        if(Math.abs(chain.impact()) >= impactThreshold) {
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
                    count += sumImpacts(ic, impactThreshold, resultMatrix);
                }
            }
        }
        return count;
    }
    
    
    /**
     * Scales the impact matrix to have its greatest absolute value 
     * to be equal to value of <b>scaleTo</b>.
     * @param scaleTo The value that the greatest absolute impact value in the matrix will be scaled to
     * @return <code>CrossImpactMatrix</code> scaled according to <b>scaleTo</b> argument.
     */
    public CrossImpactMatrix scale(double scaleTo) {
        if(scaleTo == 0) throw new IllegalArgumentException("scaleTo cannot be 0");
        double max = greatestValue();
        double[] scaledImpacts = this.values.clone();
        for(int i = 0; i < values.length; i++) {
            scaledImpacts[i] = values[i] / max * scaleTo;
        }
        return new CrossImpactMatrix(Math.abs(scaleTo), this.varCount, this.onlyIntegers, this.names, scaledImpacts);
    }
    
    
    /**
     * Scales the impact matrix by calling {@link CrossImpactMatrix#scale} 
     * and rounding the values of resulting <code>CrossImpactMatrix</code>
     * to the nearest integer.
     * The <code>onlyIntegers</code> property for the new <code>CrossImpactMatrix</code>
     * will be true.
     * @param scaleTo The value that the highest absolute impact value in the matrix will be scaled to
     * @return <code>CrossImpactMatrix</code> that has its values scaled to <b>scaleTo</b> and rounded to nearest integers
     */
    public CrossImpactMatrix round(int scaleTo) {
        if(Math.abs(scaleTo) < 1) throw new IllegalArgumentException("scaleTo cannot be 0");
        CrossImpactMatrix roundedMatrix = this.scale(scaleTo);
        
        for(int i = 0; i < roundedMatrix.values.length; i++) {
            roundedMatrix.values[i] = Math.round(roundedMatrix.values[i]);
        }

        return new CrossImpactMatrix(roundedMatrix.maxImpact, roundedMatrix.varCount, true, roundedMatrix.names, roundedMatrix.values);
    }
    
    
    /**
     * Returns a new <code>CrossImpactMatrix</code>
     * derived from this impact matrix 
     * where impact values are rounded to the nearest integer
     * @return New <code>CrossImpactMatrix</code> where impact values are rounded to the nearest integer
     */
    public CrossImpactMatrix round() {return this.round((int)Math.round(this.getMaxImpact()));}
    
    
    /**
     * Normalizes this matrix by dividing each impact value
     * by the average distance of values from zero.
     * The maximum impact value will be the greatest value in the normalized matrix.
     * @return Normalized matrix
     */
    public CrossImpactMatrix normalize() {
        
        double[] normalizedValues = this.values.clone();
        double averageDistanceFromZero = this.matrixAverage(true);
        
        for(int i = 0; i < normalizedValues.length; i++) {
            normalizedValues[i] /= averageDistanceFromZero;
        }
        
        double[] sorted = normalizedValues.clone();
        Arrays.sort(sorted);
        double arrmax = sorted[sorted.length-1];
        
        return new CrossImpactMatrix(arrmax, this.varCount, false, this.names, normalizedValues);
        
    }
    
    public CrossImpactMatrix normalize(double scaleTo) {
        CrossImpactMatrix normalized = this.normalize().scale(scaleTo);
        normalized.noteTransformation("Normalized to " + scaleTo);
        return normalized;
    }
    
    
    
    /**
     * Returns an importance matrix for the impact matrix.
     * Importance for each impactor-impacted pair or matrix entry
     * is calculated by dividing the impact 
     * by the sum of the absolute impacts on the impacted variable.
     * @return Importance matrix derived from this <code>CrossImpactMatrix</code>
     */
    public CrossImpactMatrix importanceMatrix() {
        CrossImpactMatrix importanceMatrix = new CrossImpactMatrix(this.getMaxImpact()*this.varCount, this.varCount, this.onlyIntegers, this.names);
        for (int impacted=1; impacted<=this.varCount; impacted++) {
            for (int impactor=1; impactor <= this.varCount; impactor++) {
                double shareOfAbsoluteSum = this.columnSum(impacted, true) != 0 ? 
                        this.getValue(impactor, impacted) /  this.columnSum(impacted, true) 
                        : 0;
                double absShare = Math.abs(shareOfAbsoluteSum);
                double importance = shareOfAbsoluteSum < 0 ? -absShare : absShare ;
                
                importanceMatrix.setImpact(impactor, impacted, importance);
            }
        }
        importanceMatrix.noteTransformation("Derived as an importance matrix");
        return importanceMatrix;
    }
    
    
    /**
     * Returns a matrix where values of <b>subtractMatrix</b> have
     * been subtracted from values of this matrix.
     * @param subtractMatrix Matrix whose values are subtracted from values of this matrix
     * @return Difference matrix
     */
    public CrossImpactMatrix differenceMatrix(CrossImpactMatrix subtractMatrix) {
        if(this.varCount != subtractMatrix.varCount) throw new IllegalArgumentException("Comparison matrix is of different size");
        // if(this.maxImpact != subtractMatrix.maxImpact) throw new IllegalArgumentException("Comparison matrix has different maxImpact and might be not suitable for difference calculation");
        boolean bothMatricesIntegral = this.onlyIntegers && subtractMatrix.onlyIntegers;
        CrossImpactMatrix differenceMatrix = new CrossImpactMatrix((this.maxImpact+subtractMatrix.maxImpact), this.varCount, bothMatricesIntegral, this.names, this.values);
        for (int i = 0; i < differenceMatrix.values.length; i++) {
            differenceMatrix.values[i] -= subtractMatrix.values[i];
        }
        differenceMatrix.noteTransformation("Derived as a difference matrix");
        return differenceMatrix;
    }

    
    /**
     * Returns a <code>VarInfoTable</code>
     * where for each variable, the sum of impacts on other variables
     * and the sum of impacts of other variables have been calculated.
     * Variables are classified into four classes on the basis of this information.
     * <table><tr><th></th><th>driven &lt; average</th><th>driven &gt; average</th></tr><tr><td>driver &gt; average</td><td>Driver</td><td>Critical</td></tr><tr><td>driver &lt; average</td><td>Stable</td><td>Reactive</td></tr></table>
     * 
     * @return <code>CrossImpactMatrix</code> row and column sums for each variable and the classification in a <code>VarInfoTable</code>
     */
    public VarInfoTable<String> driverDriven() {
        VarInfoTable<String> driverDrivenInfo = new VarInfoTable<>(Arrays.asList("Driver", "Driven", "Type"));
        double driverAverage = rowSum(1, true), drivenAverage = columnSum(1, true);
        for (int i = 2; i <= varCount; i++) {
            driverAverage = ((i-1)*driverAverage + rowSum(i, true)) / i ;
            drivenAverage = ((i-1)*drivenAverage + columnSum(i, true)) / i ;
        }
        
        
        for (int i = 1; i <= this.varCount; i++) {
            String name = this.getNamePrint(i);
            double driver = this.rowSum(i, true);
            double driven = this.columnSum(i, true);
            boolean isDriver = driver > driverAverage;
            boolean isDriven = driven > drivenAverage;
            String type = isDriver ?
                    isDriven ? "Critical" : "Driver" :
                    isDriven ? "Reactive" : "Stable" ;
            String driver_s = String.format("%2.1f", driver);
            String driven_s = String.format("%2.1f", driven);
            driverDrivenInfo.put(name, Arrays.asList(driver_s, driven_s, type));
        }
        return driverDrivenInfo;
    }
    
    
    /**
     * Returns information about the most important drivers of each variable.
     * Extracts the same information as {@link CrossImpactMatrix#reportDrivingVariables()}
     * in a <code>VarInfoTable</code>.
     * @return Information about the most important drivers for each variable.
     */
    public VarInfoTable<String> drivingVariables() {
        VarInfoTable<String> drivers = new VarInfoTable<>();
        for (int i = 1; i <= varCount; i++) {
            String varName = this.getNamePrint(i);
            List<String> impactors = new LinkedList<>();
            for(Integer imp : this.aboveAverageImpactors(i)) {
                impactors.add(this.getNamePrint(imp));
            }
            drivers.put(varName, impactors);
        }
        return drivers;
    }
    
    
    /**
     * Lists for each variable the variables that have an above-average
     * impact on the variable.
     * @return String containing the report of important driving variables for each variable
     */
    public String reportDrivingVariables() {
        String report = "";
        CrossImpactMatrix m = this.scale(1);
        for(int i = 1 ; i<=varCount; i++) {
            report += String.format("Important drivers for %s:%n", m.getName(i));
            for(Integer imp : m.aboveAverageImpactors(i)) {
                report += String.format("\t%s (%1.1f)%n", m.getName(imp), m.getValue(imp, i));
            }
        }
        return report;
    }
    
    
    /**
     * Returns a list of indices of the variables
     * that have a greater than average impact on variable
     * with index <b>varIndex</b>.
     * @param varIndex Variable index for variable whose above-average impactors
     * are returned
     * @return List of above-average impactors for variable <b>varIndex</b>
     */
    List<Integer> aboveAverageImpactors(int varIndex) {
        List<Integer> impactors = new LinkedList<>();
        SquareMatrix normMatrix = this.scale(1);
        double average = normMatrix.columnAverage(varIndex, true);
        for(int i=1; i<=normMatrix.varCount; i++) {
            if(Math.abs(normMatrix.getValue(i, varIndex)) >= average)
                impactors.add(i);
        }
        return impactors;
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
     * Calculates the approximate number of possible impact chains
     * that can be formed from this cross-impact matrix.
     * Impact chains must have a varCount of at least 2 to be included.
     * Chains of only one variable are not real impact chains,
     * so they aren't included in the count.
     * @return The number of possible impact chains in this matrix.
     */
    private double approximateChainCount() {
        int n = 0;
        double count = 0;
        while(n <= this.varCount-2) {
            count += (factorial(varCount) / factorial(n));
            n++;
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
    
    
    /**
     * Sets an impact value for a variable pair (impactor-impacted pair).
     * @param impactor Index of impactor variable
     * @param impacted Index of impacted variable
     * @param value New value for impact
     */
    public void setImpact(int impactor, int impacted, double value) {
        
        // Absolute value of impact cannot be greater than maxImpact
        if (maxImpact < Math.abs(value)) {
            throw new IllegalArgumentException(String.format("Value %2.2f is bigger than max value %2.2f", value, maxImpact));
        }
        
        // Variables cannot have an impact on themselves
        if (impactor == impacted && value != 0) {
            throw new IllegalArgumentException(String.format("Attempt to set an impact (%s) of variable (%s) on itself", value, impactor));
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
     * Generates and returns 
     * a list of impact chains possible in this matrix 
     * that have impact value greater than <b>threshold</b>.
     * @param impactOf Index of impactor variable. 
     * Only chains starting with this variable are included in returned list.
     * Can also be null; if null, chains starting with any variable 
     * are returned.
     * @param impactOn Index of impacted variable. 
     * Only chains ending in this variable are included in returned list.
     * Can also be null; if null, chains ending in any variable 
     * are returned.
     * @param threshold The required minimum impact a chain must have to be included in returned list.
     * @return A <code>List</code> of impact chains with impact higher than <i>threshold</i> and that have
     * the impactor and impacted variables specified in the <b>impactOf</b> and <b>impactOn</b> arguments.
     */
    List<ImpactChain> indirectImpacts(Integer impactOf, Integer impactOn, double threshold) {
        
        if(impactOf != null && (impactOf <1 || impactOf > varCount)) throw new IndexOutOfBoundsException("impactOf index is not present in the matrix");
        if(impactOn != null && (impactOn <1 || impactOn > varCount)) throw new IndexOutOfBoundsException("impactOn index is not present in the matrix");
        if(threshold <=0 || threshold > 1) throw new IllegalArgumentException("threshold value is not in range ]0..1]");
        
        List<Integer> initialChain = null;
        if(impactOf != null) {
            initialChain = new LinkedList<>(Arrays.asList(impactOf));
        }
        
        ImpactChain ic = new ImpactChain(this, initialChain);
        
        Set<ImpactChain> chains = ic.highImpactChains(threshold);
        
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
        
        // String representation "label" 
        stringRepresentation += String.format("Cross-impact matrix with maximum impact %1.2f (greatest impact %1.2f)%n", this.maxImpact, this.greatestValue());
        
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
    
    
    /**
     * Tests equality of this <code>CrossImpactMatrix</code>
     * with another based on {@link CrossImpactMatrix#hashCode()}.
     * @param impactMatrix <code>CrossImpactMatrix</code> to be tested for equality
     * @return 
     */
    @Override
    public boolean equals(Object impactMatrix){
        if(! (impactMatrix instanceof CrossImpactMatrix)) return false;
        return this.hashCode() == impactMatrix.hashCode();
    }
    
    /**
     * Returns hashcode based on varCount, maxImpact, values array and names array.
     * @return Hashcode
     */
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 29 * hash + (int) (Double.doubleToLongBits(this.maxImpact) ^ (Double.doubleToLongBits(this.maxImpact) >>> 32));
        hash = 29 * hash + this.varCount;
        hash = 29 * hash + Arrays.hashCode(this.values);
        hash = 29 * hash + Arrays.deepHashCode(this.names);
        return hash;
    }
    
    
    /**
     * Returns a clone of the cross-impact matrix.
     * @return Clone of the matrix
     */
    public CrossImpactMatrix clone() {
        return new CrossImpactMatrix(maxImpact, varCount, onlyIntegers, this.names.clone(), this.values.clone());
    }

    
}
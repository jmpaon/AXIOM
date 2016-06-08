/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package exit;

import java.lang.management.GarbageCollectorMXBean;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;



/**
 * <code>CrossImpactMatrix</code> represents a table of impacts 
 * variables have on each other in EXIT cross-impact analysis.
 * Impacts are usually integers, ranging from 
 * negative <b>maxImpact</b> to positive <b>maxImpact</b>.
 * Negative impact of variable X on variable Y represents
 * probability-decreasing effect of X on Y; 
 * Positive impact of variable X on variable Y represents
 * probability-increasing effect of X on Y.
 * Cross-impact matrix can be used both for representing the direct impacts
 * that are inputs for the EXIT calculation 
 * and the summed direct and indirect impacts that are the result of the
 * EXIT calculation.
 * 
 * @author jmpaon
 */
public final class CrossImpactMatrix {
    
    private double maxImpact;
    private final int varCount;
    private final double[] impacts;
    private boolean onlyIntegers;
    private boolean isLocked;
    private final String[] names;
    
    
    public CrossImpactMatrix(double maxImpact, int varCount, boolean onlyIntegers, String[] names, double[] impacts) {
        if(maxImpact <= 0) {throw new IllegalArgumentException("Negative maxImpact");}
        if(varCount < 2) {throw new IllegalArgumentException("Matrix must have at least 2 rows");}
        if(impacts == null) throw new NullPointerException("impacts array is null");
        
        this.maxImpact = maxImpact;
        this.varCount = varCount;
        this.impacts = new double[varCount*varCount];
        this.onlyIntegers = onlyIntegers;
        this.isLocked = false;
        if(impacts.length / varCount != varCount) throw new IllegalArgumentException(
                String.format("impacts array has %d elements, while varCount^2 is %d", impacts.length, varCount*varCount));
        
        int row = 1, col = 1;
        for(Double i : impacts) {
            setImpact(row, col, i);
            if(++col > varCount) { col=1; row++; }
        }

        if(names == null) { names = createNames(varCount); }
        if(names.length != varCount) { throw new IllegalArgumentException("Names array should have length equal to impact count"); }
        this.names = names;        
    }
    
    /**
     * Constructor for <code>CrossImpactMatrix</code>.
     * @param maxImpact The maximum value impacts in the matrix can have.
     * Minimum allowed value is negative <b>maxImpact</b> and maximum <b>maxImpact</b>.
     * <b>maxImpact</b> must be greater than 0.
     * @param varCount The number of variables in the matrix; 
     * will be also the number of rows and the number of columns.
     * @param onlyIntegers <b>true</b> if matrix contains only integers
     * @param names <code>String</code> array of variable names
     */
    public CrossImpactMatrix(double maxImpact, int varCount, boolean onlyIntegers, String[] names)  {
        if(maxImpact <= 0) {throw new IllegalArgumentException("Negative maxImpact");}
        if(varCount < 2) {throw new IllegalArgumentException("Matrix must have at least 2 rows");}
        
        this.maxImpact = maxImpact;
        this.varCount = varCount;
        this.impacts = new double[varCount*varCount];
        this.onlyIntegers = onlyIntegers;
        this.isLocked = false;
        if(names == null) { names = createNames(varCount); }
        if(names.length != varCount) { throw new IllegalArgumentException("Names array should have length equal to impact count"); }
        this.names = names;
    }
    
    
    /**
     * Constructor for <code>CrossImpactMatrix</code>.
     * @param maxImpact The maximum value impacts in the matrix can have.
     * Minimum allowed value is <b>-maxImpact</b> and maximum <b>maxImpact</b>.
     * <b>maxImpact</b> must be greater than 0.
     * @param varCount The number of variables in the matrix; 
     * will be also the number of rows and the number of columns.
     */
    public CrossImpactMatrix(double maxImpact, int varCount) {
        this(maxImpact, varCount, true, null);
    }
    
    
    /**
     * Constructor for <code>CrossImpactMatrix</code>.
     * @param maxImpact The maximum value impacts in the matrix can have.
     * Minimum allowed value is <b>-maxImpact</b> and maximum <b>maxImpact</b>.
     * @param varCount The number of variables in the matrix; 
     * @param onlyIntegers <b>true</b> if matrix contains only integers
     * @see CrossImpactMatrix#CrossImpactMatrix(double, int, boolean, java.lang.String[], double[]) 
     */
    public CrossImpactMatrix(double maxImpact, int varCount, boolean onlyIntegers) {
        this(maxImpact, varCount, onlyIntegers, null);
    }
    
    
    /**
     * Calculates and returns 
     * a new <code>CrossImpactMatrix</code> that contains
     * the summed direct and indirect impacts between the variables.
     * In the returned matrix, 
     * impactor variables are in rows 
     * and impacted variables are in columns.
     * @param impactTreshold The low bound for inclusion for the impact of chains that are summed in the matrix. 
     * See {@link ImpactChain#highImpactChainsIntermediary(double)}.
     * @return <code>CrossImpactMatrix</code> with the summed direct and indirect impacts between variables
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
                        impactSum += chain.chainedImpact();
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
     * the summed direct and indirect impacts between the variables.
     * In the returned matrix, 
     * impactor variables are in rows 
     * and impacted variables are in columns.
     * @param impactTreshold The low bound for inclusion for the impact of chains that are summed in the matrix. 
     * See {@link ImpactChain#highImpactChains(double)}.
     * @return <code>CrossImpactMatrix</code> with the summed direct and indirect impacts between variables
     */
    public CrossImpactMatrix summedImpactMatrix(double impactTreshold) {
        CrossImpactMatrix resultMatrix = new CrossImpactMatrix(maxImpact*varCount, varCount, false, names);
        double totalCount=0;
        for(int impactor=1; impactor<=this.varCount; impactor++) {
            Reporter.msg("Calculating direct and indirect impacts of %s(%s)... ", getNameShort(impactor), truncateName(getName(impactor), 15));
            ImpactChain chain = new ImpactChain(this, Arrays.asList(impactor));
            double count = sumImpacts(chain, impactTreshold, resultMatrix);
            totalCount += count;
            Reporter.msg("%10.0f significant (treshold %1.4f) impact chains found%n", count, impactTreshold);
        }
        Reporter.msg("%s significant (treshold %1.4f) impact chains found in the matrix.%n", Math.round(totalCount), impactTreshold);
        Reporter.msg("The total number of possible chains in this matrix is %s.%n", approximateChainCountString());
        return resultMatrix;
    }
    
    
    /**
     * If impact of <b>chain</b> is higher than <b>impactTreshold</b>,
     * it is added to <b>resultMatrix</b> and the possible immediate expansions of
     * <b>chain</b> are generated and their impacts added to <b>resultMatrix</b>
     * if appropriate.
     * @param chain <code>ImpactChain</code> to consider for addition to <b>resultMatrix</b>
     * @param impactTreshold <b>chain</b> must have an impact of at least this value to be added to <b>resultMatrix</b>
     * @param resultMatrix <code>CrossImpactMatrix</code> where the significant impacts are summed
     * @return count of significant impact chains found in <b>chain</b> and its expansions.
     */
    private double sumImpacts(ImpactChain chain, double impactTreshold, CrossImpactMatrix resultMatrix) {
        
        double count = 0;
        if(Math.abs(chain.chainedImpact()) >= impactTreshold) {
            count++;
            int impactor = chain.impactorIndex();
            int impacted = chain.impactedIndex();
            double accumulatedValue = resultMatrix.getImpact(impactor, impacted);
            double additionValue    = chain.chainedImpact();
            
            if(impactor != impacted) {
                if(resultMatrix.maxImpact < accumulatedValue + additionValue) {
                    resultMatrix.setMaxImpact(Math.round((accumulatedValue + additionValue)*1.5));
                }
                resultMatrix.setImpact(impactor, impacted, accumulatedValue + additionValue);
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
     * @return Matrix scaled according to <b>scaleTo</b> argument.
     */
    public CrossImpactMatrix scaleByMax(double scaleTo) {
        if(scaleTo == 0) throw new IllegalArgumentException("scaleTo cannot be 0");
        double max = greatestImpact();
        double[] scaledImpacts = this.impacts.clone();
        for(int i = 0; i < impacts.length; i++) {
            scaledImpacts[i] = impacts[i] / max * scaleTo;
        }
        return new CrossImpactMatrix(Math.abs(scaleTo), this.varCount, this.onlyIntegers, this.names, scaledImpacts);
    }
    
    public CrossImpactMatrix driverDriven() {
        throw new UnsupportedOperationException("Not implemented");
    }
    
    public String reportDrivingVariables() {
        String report = "";
        CrossImpactMatrix m = this.scaleByMax(1);
        for(int i = 1 ; i<=varCount; i++) {
            report += String.format("Important drivers for %s:%n", m.getName(i));
            for(Integer imp : m.aboveAverageImpactors(i)) {
                report += String.format("\t%s (%1.1f)%n", m.getName(imp), m.getImpact(imp, i));
            }
        }
        return report;
    }
    
    List<Integer> aboveAverageImpactors(int varIndex) {
        List<Integer> impactors = new LinkedList<>();
        CrossImpactMatrix normMatrix = this.scaleByMax(1);
        double average = normMatrix.averageImpactOn(varIndex);
        for(int i=1; i<=normMatrix.varCount; i++) {
            if(Math.abs(normMatrix.getImpact(i, varIndex)) >= average)
                impactors.add(i);
        }
        return impactors;
    }
    
    double sumImpactsOf(int varIndex, boolean absoluteValues) {
        double sum=0;
        for(int i=1; i<=varCount; i++) {
            sum += absoluteValues ? Math.abs(getImpact(varIndex, i)) : getImpact(varIndex, i);
        }
        return sum;
    }
    
    double sumImpactsOn(int varIndex, boolean absoluteValues) {
        double sum=0;
        for(int i=1; i<=varCount; i++) {
            sum += absoluteValues ? Math.abs(getImpact(i, varIndex)) : getImpact(i, varIndex);
        }
        return sum;        
    }
    
    double averageImpactOf(int varIndex) {
        return sumImpactsOf(varIndex, true) / varCount;
    }
    
    double averageImpactOn(int varIndex) {
        return sumImpactsOn(varIndex, true) / varCount;
    }
    
    /**
     * 
     * @param im Cross-impact matrix to compare against this one in terms of impact sizes
     * @param maxDeviation The maximum relative deviation allowed to still consider the matrices approximately same in terms of impact sizes
     * @return <b>true</b> if 
     */
    boolean areImpactsApproximatelySame(CrossImpactMatrix im, double maxDeviation) {
        if(im.impacts.length != this.impacts.length) throw new IllegalArgumentException("Matrices are differently sized and cannot be compared");
        for(int i=0; i<this.impacts.length; i++) {
            double v1 = this.impacts[i], v2 = im.impacts[i];
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
    
    
    /**
     * @return The defined maximum value for impacts in this matrix.
     */
    public double getMaxImpact() {
        return maxImpact;
    }
    
    
    /**
     * Sets a new value for the <b>maxImpact</b> of the matrix.
     * @param newMaxImpact New  <b>maxImpact</b> value
     */
    void setMaxImpact(double newMaxImpact) {
        if(newMaxImpact <= 0 ) throw new IllegalArgumentException("maxImpact cannot be 0 or smaller");
        for(Double i : impacts) if(i > newMaxImpact) throw new IllegalArgumentException("impacts array contains values greater than new max impact");
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
    public List<ImpactChain> indirectImpacts(Integer impactOf, Integer impactOn, double treshold) {
        
        if(impactOf != null && (impactOf <1 || impactOf > varCount)) throw new IndexOutOfBoundsException("impactOf index is not present in the matrix");
        if(impactOn != null && (impactOn <1 || impactOn > varCount)) throw new IndexOutOfBoundsException("impactOn index is not present in the matrix");
        if(treshold <=0 || treshold > 1) throw new IllegalArgumentException("treshold value is not in range ]0..1]");
        
        List<Integer> initialChain = null;
        if(impactOf != null) {
            initialChain = new LinkedList<>(Arrays.asList(impactOf));
        }
        
        ImpactChain ic = new ImpactChain(this, initialChain);
        
        Set<ImpactChain> chains = ic.highImpactChainsIntermediary(treshold);
        
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
     * Creates variable names for the cross-impact matrix.
     * Variable names are numbered from 1 to <b>nameCount</b>.
     * @param nameCount How many names will be generated
     * @return String array containing variable names
     */
    private String[] createNames(int nameCount) {
        int i=0;
        String n[] = new String[nameCount];
        while (i < nameCount) {
            n[i] = "Variable " + (i+1);
            i++;
        }
        return n;
    }
    
    /**
     * Returns the name of variable with index <b>var</b>.
     * @param var Index of variable in question
     * @return Name of variable with index <b>var</b>.
     * @throws IllegalArgumentException 
     */
    public String getName(int var) throws IndexOutOfBoundsException {
        if(var < 1 || var > varCount) {
            String s = String.format("No name for index [%d], varCount for the matrix is %d.", var, varCount);
            throw new IndexOutOfBoundsException(s);
        }
        return names[var-1];
    }
    
    public String getNameShort(int var) {
        if(var < 1 || var > varCount) {
            String s = String.format("No name for index [%d], varCount for the matrix is %d.", var, varCount);
            throw new IndexOutOfBoundsException(s);
        }
        return "V"+var;
    }
    
    /**
     * Sets the variable name for variable at <i>varIndex</i>.
     * @param varIndex Index of a variable in <i>matrix</i>
     * @param varName New name for variable
     * @throws IllegalArgumentException
     * @throws IndexOutOfBoundsException
     * @throws EXITexception 
     */
    public void setName(int varIndex, String varName) throws IllegalArgumentException, IndexOutOfBoundsException, EXITexception {
        if(isLocked) { throw new EXITexception("The impact matrix is locked and cannot be modified"); }
        if(varIndex < 0 || varIndex > varCount) { throw new IndexOutOfBoundsException("Invalid variable index"); }
        if(varName == null) { throw new IllegalArgumentException("Variable name cannot be null"); }
        this.names[varIndex-1] = varName;
    }
    
    /**
     * Gets an impact value from the impact matrix.
     * These values are the direct impacts, fed as input for the EXIT algorithm.
     * @param impactOf Index of variable that is the impactor variable of the impact
     * @param impactOn Index of variable that is the impacted variable of the impact
     * @return The direct impact of var with index <i>impactOf</i> on var with index <i>impactOn</i>
     * @throws IllegalArgumentException 
     */
    public double getImpact(int impactOf, int impactOn) throws IndexOutOfBoundsException {
        if(impactOf < 1 || impactOf > varCount || impactOn < 1 || impactOn > varCount) {
            String exceptionMsg = String.format("No impact for index [%d:%d], varCount for the matrix is %d.", impactOf, impactOn, varCount);
            throw new IndexOutOfBoundsException(exceptionMsg);
        }
        int index = ((impactOf-1) * varCount) + (impactOn-1);
        return impacts[index];
    }
    
    /**
     * Sets an impact value in the impact matrix.
     * @param impactOf Index of variable that is the impactor variable of the impact
     * @param impactOn Index of variable that is the impacted variable of the impact
     * @param value Value/strength of the impact, between negative <b>maxImpact</b> and positive <b>maxImpact</b>.
     * @throws IllegalArgumentException
     */
    public void setImpact(int impactOf, int impactOn, double value) throws IllegalArgumentException, IndexOutOfBoundsException, IllegalStateException {
        
        // Locked matrix cannot be changed
        if(isLocked) { throw new IllegalStateException("The impact matrix is locked and cannot be modified"); }
        
        // Test if indexes are legal
        if(impactOf < 1 || impactOf > varCount || impactOn < 1 || impactOn > varCount) {
            String s = String.format("Impact for index [%d:%d] cannot be set, varCount for the matrix is %d.", impactOf, impactOn, varCount);
            throw new IndexOutOfBoundsException(s);
        }
        
        // Variables cannot have an impact on themselves
        if(impactOf == impactOn && value != 0) throw new IllegalArgumentException(String.format("Attempt to set an impact (%s) of variable (%s) on itself", value, impactOf));
        
        // If onlyIntegers is true for the matrix, only integral impact values can be set in the matrix
        if(this.onlyIntegers && value != (int)value) {
            throw new IllegalArgumentException(String.format("Value %f is not an integer and not allowed", value));
        }
        
        // Absolute value of impact cannot be greater than maxImpact
        if(maxImpact < Math.abs(value)) {
            throw new IllegalArgumentException(String.format("Value %2.2f is bigger than max value %2.2f",value, maxImpact));
        }

        int index = ((impactOf-1) * varCount) + (impactOn-1);
        impacts[index] = value; 
        
    }
    
    /**
     * @return The number of variables in the <code>CrossImpactMatrix</code>.
     */
    public int getVarCount() { return varCount; }
    
    /**
     * @return <b>true</b> if all direct impacts are integers, false otherwise
     */
    private boolean allImpactsAreIntegers() {
        for(int i=0;i<impacts.length;i++) {
            if(impacts[i] != (int)impacts[i]) { return false; }
        }
        return true;
    }    
    
    /**
     * Returns true if <code>CrossImpactMatrix</code> is locked, false otherwise.
     * Matrix being locked means that impact values cannot be changed anymore.
     * @return <b>true</b> if matrix is locked, <b>false</b> otherwise.
     */
    public boolean isLocked() { return isLocked; }
    
    
    /**
     * Locks the matrix so that contents cannot be changed.
     */
    public void lock() {
        if (allImpactsAreIntegers()) { onlyIntegers = true; }
        this.isLocked = true; 
    }
    
    
    /**
     * @return A string representation of the impact matrix.
     */
    @Override
    public String toString() {
        int i=0, c, n=0;
        String stringRepresentation="";
        
        stringRepresentation += String.format("%30s     \t", " ");
        for(c=0; c<varCount;c++) {
            stringRepresentation += String.format("%s\t", "V"+(c+1));
        }
        stringRepresentation += String.format("%n");
        
        while( i < impacts.length) {
            stringRepresentation += String.format("%30s (%s)\t", truncateName(names[n], 30), ("V"+(n+1)));
            n++;
            c=0;
            while(c < varCount) {
                if(this.onlyIntegers) {
                    stringRepresentation += String.format("%2d\t", (int)impacts[i]);
                } else {
                    stringRepresentation += String.format("%2.2f\t", impacts[i]);
                }
                
                c++;
                i++;
            }
            stringRepresentation += String.format("%n");
        }
        return stringRepresentation;
    }
    
    
    /**
     * If <b>s</b> is shorter than <b>len</b>, <b>s</b> is returned;
     * Otherwise, first <b>len</b>-3 characters of <b>exceptionMsg</b> 
     * appended with three dots are returned.
     * @param s String (name) to truncate
     * @param len Length of <b>exceptionMsg</b> after truncation.
     * @return Truncated String/name.
     */
    private String truncateName(String s, int len) {
        return s.length()<=len ? s : s.substring(0, len-3) + "...";
    }
    
    
    /**
     * @return The greatest <u>absolute</u> impact value in the matrix.
     */
    public double greatestImpact() {
        
        double greatest = Math.abs(impacts[0]);
        for (int i=1; i<impacts.length; i++) {
            double v = Math.abs(impacts[i]);
            if(v > greatest) { greatest = v; }
        }
        return greatest;
    }
    
    
    /**
     * Returns a copy of the impact matrix contents (the impact values)
     * in a 2-dimensional array.
     * @return Impact matrix contents in a 2-dimensional array.
     */
    public double[][] copyMatrix() {
        double[][] copy = new double[varCount][varCount];
        int i=0, r=0, c;
        while(i<impacts.length) {
            for(c=0 ; c < varCount; c++,i++) {
                copy[r][c] = impacts[i];
            }
            r++;
        }
        return copy;
    }
    
}

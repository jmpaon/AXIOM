/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package exit;

import java.util.Arrays;
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
 * 
 * @author jmpaon
 */
public class CrossImpactMatrix {
    
    private final double maxImpact;
    private final int varCount;
    private final double[] impacts;
    private boolean onlyIntegers;
    private boolean isLocked;
    private final String[] names;
    
    /**
     * Constructor for <code>CrossImpactMatrix</code>
     * @param maxImpact The maximum value impacts in the matrix can have.
     * Minimum allowed value is <b>-maxImpact</b> and maximum <b>maxImpact</b>.
     * @param varCount The number of variables in the matrix; 
     * will be also the number of rows and the number of columns.
     * @param onlyIntegers Are only integers accepted?
     * @param names Array of variable names
     */
    public CrossImpactMatrix(double maxImpact, int varCount, boolean onlyIntegers, String[] names)  {
        if(maxImpact < 0) {throw new IllegalArgumentException("Negative maxImpact");}
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
    
    
    public CrossImpactMatrix(double maxImpact, int varCount) {
        this(maxImpact, varCount, true, null);
    }
    
    
    public List<ImpactChain> indirectImpacts(Integer impactOf, Integer impactOn, double treshold) {
        
        
        if(impactOn != null && (impactOn <1 || impactOn > varCount)) throw new IndexOutOfBoundsException("impactOn index is not present in the matrix");
        if(impactOf != null && (impactOf <1 || impactOf > varCount)) throw new IndexOutOfBoundsException("impactOf index is not present in the matrix");
        if(treshold <=0 || treshold > 1) throw new IllegalArgumentException("treshold value is not in range [0..1]");
        
        List<Integer> initialChain = null;
        
        if(impactOf != null) {
            initialChain = new LinkedList<>(Arrays.asList(impactOf));
        }
        
        ImpactChain ic = new ImpactChain(this, initialChain);
        Set<ImpactChain> chains = ic.highImpactChains(treshold);
        
        if(impactOn != null) {
            chains = chains.stream()
                    .filter(c -> c.chainEndsToIndex(impactOn))
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
    
    public void setName(int varIndex, String varName) throws IllegalArgumentException, IndexOutOfBoundsException, EXITException {
        if(isLocked) { throw new EXITException("The impact matrix is locked and cannot be modified"); }
        if(varIndex < 0 || varIndex > varCount) { throw new IndexOutOfBoundsException("Invalid variable index"); }
        if(varName == null) { throw new IllegalArgumentException("Variable name cannot be null"); }
        this.names[varIndex-1] = varName;
    }
    
    /**
     * Gets the impact value from the impact matrix.
     * @param impactOf Index of variable that is the impactor variable of the impact
     * @param impactOn Index of variable that is the impacted variable of the impact
     * @return 
     * @throws IllegalArgumentException 
     */
    public double getImpact(int impactOf, int impactOn) throws IndexOutOfBoundsException {
        if(impactOf < 1 || impactOf > varCount || impactOn < 1 || impactOn > varCount) {
            String s = String.format("No impact for index [%d:%d], varCount for the matrix is %d.", impactOf, impactOn, varCount);
            throw new IndexOutOfBoundsException(s);
        }
        int index = ((impactOf-1) * varCount) + (impactOn-1);
        return impacts[index];
    }
    
    /**
     * Sets an impact value in the impact matrix.
     * @param impactOf Index of variable that is the impactor variable of the impact
     * @param impactOn Index of variable that is the impacted variable of the impact
     * @param value Value/strength of the impact
     * @throws IllegalArgumentException
     * @throws EXITException 
     */
    public void setImpact(int impactOf, int impactOn, double value) throws IllegalArgumentException, IndexOutOfBoundsException, EXITException {
        
        // Locked matrix cannot be changed
        if(isLocked) { throw new EXITException("The impact matrix is locked and cannot be modified"); }
        
        // Test if indexes are legal
        if(impactOf < 1 || impactOf > varCount || impactOn < 1 || impactOn > varCount) {
            String s = String.format("Impact for index [%d:%d] cannot be set, varCount for the matrix is %d.", impactOf, impactOn, varCount);
            throw new IndexOutOfBoundsException(s);
        }
        
        // Variables cannot have an impact on themselves
        if(impactOf == impactOn && value != 0) throw new IllegalArgumentException("Attempt to set an impact for variable at itself");
        
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
        
        while( i < impacts.length) {

            stringRepresentation += String.format("%25s\t", names[n]);
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
    
    private boolean allImpactsAreIntegers() {
        for(int i=0;i<impacts.length;i++) {
            if(impacts[i] != (int)impacts[i]) { return false; }
        }
        return true;
    }
    
    /**
     * @return The defined maximum value for impacts in this matrix.
     * Can be null.
     */
    public double getMaxImpact() {
        return maxImpact;
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
     * Returns a copy of the impact matrix (only the impact values)
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

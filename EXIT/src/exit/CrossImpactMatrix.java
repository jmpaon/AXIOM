/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package exit;

/**
 *
 * @author jmpaon
 */
public class CrossImpactMatrix {
    
    private final Double maxImpact;
    private final int varCount;
    private final double[] impacts;
    private boolean onlyIntegers;
    private boolean isLocked;
    private final String[] names;
    
    
    public CrossImpactMatrix(Double maxImpact, int varCount, boolean onlyIntegers, String[] names) throws ModelBuildingException {
        if(maxImpact != null && maxImpact < 0) {throw new ModelBuildingException("Negative maxImpact");}
        if(varCount < 2) {throw new ModelBuildingException("Matrix must have at least 2 rows");}
        
        this.maxImpact = maxImpact;
        this.varCount = varCount;
        this.impacts = new double[varCount*varCount];
        this.onlyIntegers = onlyIntegers;
        this.isLocked = false;
        if(names == null) { names = createNames(varCount); }
        if(names.length != varCount) { throw new ModelBuildingException("Names array should have length equal to impact count"); }
        this.names = names;
    }
    
    
    
    public CrossImpactMatrix(Double maxImpact, int varCount) throws ModelBuildingException {
        this(maxImpact, varCount, true, null);
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
     * @throws ArgumentException 
     */
    public String getName(int var) throws ArgumentException {
        if(var < 1 || var > varCount) {
            String s = String.format("No name for index [%d], varCount for the matrix is %d.", var, varCount);
            throw new ArgumentException(s);
        }
        return names[var-1];
    }
    
    public void setName(int varIndex, String varName) throws ArgumentException, EXITException {
        if(isLocked) { throw new EXITException("The impact matrix is locked and cannot be modified"); }
        if(varIndex < 0 || varIndex > varCount) { throw new ArgumentException("Invalid variable index"); }
        if(varName == null) { throw new ArgumentException("Variable name cannot be null"); }
        this.names[varIndex-1] = varName;
    }
    
    /**
     * Gets the impact value from the impact matrix.
     * @param impactOf Index of variable that is the impactor variable of the impact
     * @param impactOn Index of variable that is the impacted variable of the impact
     * @return 
     * @throws ArgumentException 
     */
    public double getImpact(int impactOf, int impactOn) throws ArgumentException {
        if(impactOf < 1 || impactOf > varCount || impactOn < 1 || impactOn > varCount) {
            String s = String.format("No impact for index [%d:%d], varCount for the matrix is %d.", impactOf, impactOn, varCount);
            throw new ArgumentException(s);
        }
        int index = ((impactOf-1) * varCount) + (impactOn-1);
        return impacts[index];
    }
    
    /**
     * Sets an impact value in the impact matrix.
     * @param impactOf Index of variable that is the impactor variable of the impact
     * @param impactOn Index of variable that is the impacted variable of the impact
     * @param value Value/strength of the impact
     * @throws ArgumentException
     * @throws EXITException 
     */
    public void setImpact(int impactOf, int impactOn, double value) throws ArgumentException, EXITException {
        
        if(isLocked) { throw new EXITException("The impact matrix is locked and cannot be modified"); }

        if(impactOf < 1 || impactOf > varCount || impactOn < 1 || impactOn > varCount) {
            String s = String.format("Impact for index [%d:%d] cannot be set, varCount for the matrix is %d.", impactOf, impactOn, varCount);
            throw new ArgumentException(s);
        }
        
        if(this.onlyIntegers && value != (int)value) {
            throw new ArgumentException(String.format("Value %f is not an integer and not allowed", value));
        }
        
        if(this.maxImpact != null && maxImpact < value) {
            throw new ArgumentException(String.format("Value %2.2f is bigger than max value %2.2f",value, maxImpact));
        }
        
        int index = ((impactOf-1) * varCount) + (impactOn-1);
        impacts[index] = value; 
        
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
        for(int i=1; i<impacts.length; i++) {
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

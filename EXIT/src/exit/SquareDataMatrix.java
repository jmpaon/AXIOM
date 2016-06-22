/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exit;

import java.util.NoSuchElementException;

/**
 * 
 * @author jmpaon
 */
public class SquareDataMatrix {

    protected final int varCount;          /* Number of variables (and rows and columns) in this matrix */
    protected final double[] values;       /* Matrix contents */
    protected final boolean onlyIntegers;  /* Are only integers allowed as matrix values */
    protected final String[] names;        /* Variable/row/column names */
    private boolean isLocked;              /* Is the matrix locked? If locked, matrix contents cannot be changed */

    
    public SquareDataMatrix(int varCount, boolean onlyIntegers, String[] names, double[] values) {
        if (varCount < 1) { throw new IllegalArgumentException("varCount cannot be smaller than 1"); }
        if(values == null) throw new NullPointerException("values array is null");
        if(names == null) throw new NullPointerException("names array is null");
        
        this.varCount = varCount;
        if(values.length/varCount != varCount) 
            throw new IllegalArgumentException(
                    String.format("values array has %d elements, while varCount^2 is %d", values.length, varCount*varCount));
        
        if(names.length != varCount)
            throw new IllegalArgumentException(
                    String.format("names array has %d elements, while varCount is %d", names.length, varCount));
        
        
        this.onlyIntegers = onlyIntegers;
        this.values = values;
        this.names = names;
        this.isLocked = false;

    }
    
    public SquareDataMatrix(int varCount, boolean onlyIntegers, String[] names) {
        this(varCount, onlyIntegers, names, new double[varCount*varCount]);
    }
    
    public SquareDataMatrix(int varCount, boolean onlyIntegers) {
        this(varCount, onlyIntegers, createNames(varCount), new double[varCount*varCount]);
    }
    

    /**
     * Returns true if <code>SquareDataMatrix</code> is locked, false
     * otherwise. SquareDataMatrix being locked means that impact values cannot
     * be changed anymore.
     *
     * @return <b>true</b> if matrix is locked, <b>false</b> otherwise.
     */
    protected boolean isLocked() {
        return isLocked;
    }

    /**
     * Locks the matrix so that contents cannot be changed.
     */
    protected void lock() {
        this.isLocked = true;
    }

    /**
     * Returns the sum of the values in a specific row of the matrix.
     * @param row index of the row which values are summed
     * @param absoluteValues if <i>true</i>, sum of absolute values is returned;
     * otherwise sum of values is returned
     * @return Sum of values on row with index <b>row</b>
     */
    protected double rowSum(int row, boolean absoluteValues) {
        double sum = 0;
        for (int i = 1; i <= varCount; i++) {
            sum += absoluteValues ? Math.abs(getValue(row, i)) : getValue(row, i);
        }
        return sum;
    }

    /**
     * Returns the sum of the values in a specific column of the matrix.
     * @param column index of the column which values are summed
     * @param absoluteValues if <i>true</i>, sum of absolute values is
     * returned; otherwise sum of values is returned
     * @return Sum of values of other variables on variable with index <b>column</b>
     */
    protected double columnSum(int column, boolean absoluteValues) {
        double sum = 0;
        for (int i = 1; i <= varCount; i++) {
            sum += absoluteValues ? Math.abs(getValue(i, column)) : getValue(i, column);
        }
        return sum;
    }

    /**
     * Returns the average of values of specific row
     *
     * @param row Index of the variable which values are averaged
     * @return Average of values of variable with index <b>row</b> on other
     * variables
     */
    protected double rowAverage(int row, boolean absoluteValues) {
        return rowSum(row, absoluteValues) / varCount;
    }

    /**
     * Returns the average of values on specific column
     *
     * @param column Index of the variable which values are averaged
     * @return Average of values of other variables on variable with index
 <b>column</b>
     */
    protected double columnAverage(int column, boolean absoluteValues) {
        return columnSum(column, absoluteValues) / varCount;
    }
    
    /**
     * Returns the maximum value in a specific row of the matrix.
     * @param row Row index
     * @param absolute if true, maximum of absolute values is returned; else maximum of values is returned
     * @return the maximum value in row <i>row</i>
     */
    protected double rowMax(int row, boolean absolute) {
        double max = absolute ? Math.abs(this.getValue(row, 1)) : this.getValue(row, 1);
        for (int i = 1; i <= varCount; i++) {
            if(absolute ? Math.abs(this.getValue(row, i)) > max : this.getValue(row, i) > max)
                max = absolute ? Math.abs(this.getValue(row, i)) : this.getValue(row, i);
        }
        return max;
    }

    /**
     * Returns the maximum value in a specific column of the matrix
     * @param column Column index
     * @param absolute if true, maximum of absolute values is returned; else maximum of values is returned
     * @return the maximum value in column <i>column</i>
     */
    protected double columnMax(int column, boolean absolute) {
        double max = absolute ? Math.abs(this.getValue(1, column)) : this.getValue(1, column);
        for (int i = 1; i <= varCount; i++) {
            if(absolute ? Math.abs(this.getValue(i, column)) > max : this.getValue(i, column) > max)
                max = absolute ? Math.abs(this.getValue(i, column)) : this.getValue(i, column);
        }
        return max;
    }

    /**
     * Creates variable names for the matrix. Variable names are
     * numbered from 1 to <b>nameCount</b>.
     * @param nameCount How many names will be generated
     * @return String array containing variable names
     */
    protected static String[] createNames(int nameCount) {
        int i = 0;
        String[] n = new String[nameCount];
        while (i < nameCount) {
            n[i] = "Variable " + (i + 1);
            i++;
        }
        return n;
    }

    /**
     * Returns the name of variable with index <b>varIndex</b>.
     *
     * @param varIndex Index of variable in question
     * @return Name of variable with index <b>varIndex</b>.
     * @throws IllegalArgumentException
     */
    public String getName(int varIndex) throws IndexOutOfBoundsException {
        if (varIndex < 1 || varIndex > varCount) {
            String s = String.format("No name for index [%d], varCount for the matrix is %d.", varIndex, varCount);
            throw new IndexOutOfBoundsException(s);
        }
        return names[varIndex - 1];
    }
    
    
    /**
     * Returns the name of the variable with index <b>varIndex</b>,
     * so that each name will have whitespace as much as is needed
     * to make all variable names equal in length.
     * The longest variable name will not have any whitespace.
     * @param varIndex
     * @return 
     */
    public String getNamePrint(int varIndex) {
        String varName = getName(varIndex);
        int longestNameLen = 0;
        for(String name : names) {
            longestNameLen = name.length() > longestNameLen ? name.length() : longestNameLen;
        }
        int whitespaceChars = longestNameLen - varName.length();
        StringBuilder sb = new StringBuilder();
        while(whitespaceChars>0) {
            sb.append(" ");
            whitespaceChars--;
        }
        sb.append(varName);
        return sb.toString();
    } 

    /**
     * Get a short (Vx) name for variable <i>varIndex</i>
     * @param varIndex Variable index
     * @return A short name for variable <i>varIndex</i>
     */
    protected String getNameShort(int varIndex) {
        if (varIndex < 1 || varIndex > varCount) {
            String s = String.format("No name for index [%d], varCount for the matrix is %d.", varIndex, varCount);
            throw new IndexOutOfBoundsException(s);
        }
        return "V" + varIndex;
    }
    
    
    /**
     * If <b>s</b> is shorter than <b>len</b>, <b>s</b> is returned;
     * Otherwise, first <b>len</b>-3 characters of <b>exceptionMsg</b> 
     * appended with three dots are returned.
     * @param s String (name) to truncate
     * @param len Length of <b>exceptionMsg</b> after truncation.
     * @return Truncated String/name.
     */
    protected String truncateName(String s, int len) {
        return s.length()<=len ? s : s.substring(0, len-3) + "...";
    }    
    

    public int getIndex(String varName) {
        for (int i = 0; i < names.length; i++) {
            if (names[i].equals(varName)) {
                return i + 1;
            }
        }
        throw new NoSuchElementException(String.format("impact matrix doesn't have a variable with name %s", varName));
    }

    
    /**
     * Sets the variable name for variable at <i>varIndex</i>.
     *
     * @param varIndex Index of a variable in <i>matrix</i>
     * @param varName New name for variable
     * @throws IllegalArgumentException
     * @throws IndexOutOfBoundsException
     */
    public void setName(int varIndex, String varName) throws IllegalArgumentException, IndexOutOfBoundsException, IllegalStateException  {
        if (isLocked) {
            throw new IllegalStateException("The impact matrix is locked and cannot be modified");
        }
        if (varIndex < 0 || varIndex > varCount) {
            throw new IndexOutOfBoundsException("Invalid variable index");
        }
        if (varName == null) {
            throw new IllegalArgumentException("Variable name cannot be null");
        }
        this.names[varIndex - 1] = varName;
    }
    
    /**
     * Gets a value from the matrix. 
     * @param row Row index
     * @param column Column index
     * @return The value in the matrix at index [row:column]
     * @throws IllegalArgumentException
     */
    public double getValue(int row, int column) throws IndexOutOfBoundsException {
        if (row < 1 || row > varCount || column < 1 || column > varCount) {
            String exceptionMsg = String.format("No value for index [%d:%d], varCount for the matrix is %d.", row, column, varCount);
            throw new IndexOutOfBoundsException(exceptionMsg);
        }
        int index = ((row - 1) * varCount) + (column - 1);
        return values[index];
    }

    /**
     * Sets a value in the matrix.
     *
     * @param row Index of variable that is the impactor variable of the
     * impact
     * @param column Index of variable that is the impacted variable of the impact
     * @param value Value/strength of the impact, between negative
     * <b>maxImpact</b> and positive <b>maxImpact</b>.
     * @throws IllegalArgumentException
     */
    public void setValue(int row, int column, double value) throws IllegalArgumentException, IndexOutOfBoundsException, IllegalStateException {

        // Locked matrix cannot be changed
        if (isLocked) {
            throw new IllegalStateException("The impact matrix is locked and cannot be modified");
        }
        // Test if indexes are legal
        if (row < 1 || row > varCount || column < 1 || column > varCount) {
            String s = String.format("Impact for index [%d:%d] cannot be set, varCount for the matrix is %d.", row, column, varCount);
            throw new IndexOutOfBoundsException(s);
        }
        // Variables cannot have an impact on themselves
        if (row == column && value != 0) {
            throw new IllegalArgumentException(String.format("Attempt to set an impact (%s) of variable (%s) on itself", value, row));
        }
        // If onlyIntegers is true for the matrix, only integral impact values can be set in the matrix
        if (this.onlyIntegers && value != (int) value) {
            throw new IllegalArgumentException(String.format("Value %f is not an integer and not allowed", value));
        }

        int index = ((row - 1) * varCount) + (column - 1);
        values[index] = value;
    }

    /**
     * @return The number of variables in the <code>SquareDataMatrix</code>.
     */
    public int getVarCount() {
        return varCount;
    }

    /**
     * @return <b>true</b> if all values in matrix are integers, false otherwise
     */
    protected boolean allValuesAreIntegers() {
        for (int i = 0; i < values.length; i++) {
            if (values[i] != (int) values[i]) {
                return false;
            }
        }
        return true;
    }

    /**
     * @return The greatest <u>absolute</u> value in the matrix.
     */
    public double greatestValue() {
        double greatest = Math.abs(values[0]);
        for (int i = 1; i < values.length; i++) {
            double v = Math.abs(values[i]);
            if (v > greatest) {
                greatest = v;
            }
        }
        return greatest;
    }

    /**
     * Returns a copy of the matrix contents (the values) in a
     * 2-dimensional array.
     *
     * @return Matrix contents in a 2-dimensional array.
     */
    public double[][] copyMatrix() {
        double[][] copy = new double[varCount][varCount];
        int i = 0;
        int r = 0;
        int c;
        while (i < values.length) {
            for (c = 0; c < varCount; c++, i++) {
                copy[r][c] = values[i];
            }
            r++;
        }
        return copy;
    }

    /**
     * Tests if impact values of this matrix deviate
     * from the impact values of <b>matrix</b>
     * at most by value of <b>maxDifference</b>
     * @param matrix matrix to compare against this one in terms of impact sizes
     * @param maxDifference The maximum relative difference allowed to still consider the matrices approximately same in terms of values
     * @return <b>true</b> if
     */
    boolean areValuesApproximatelySame(SquareDataMatrix matrix, double maxDifference) {
        if(maxDifference <= 0) throw new IllegalArgumentException("maxDifference must be greater than 0");
        if (matrix.values.length != this.values.length) {
            throw new IllegalArgumentException("Matrices are differently sized and cannot be compared");
        }
        for (int i = 0; i < this.values.length; i++) {
            double v1 = this.values[i];
            double v2 = matrix.values[i];
            double rel = v1 > v2 ? v1 / v2 : v2 / v1;
            if ((rel - 1) > maxDifference) {
                return false;
            }
        }
        return true;
    }

}

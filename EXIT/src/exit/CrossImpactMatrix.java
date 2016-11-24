/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exit;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author juha
 */
public class CrossImpactMatrix extends SquareMatrix{
    /** List of descriptions of transformations done to this matrix */
    protected final List<String> transformations;
    /** Are only integers allowed as matrix values? */
    protected final boolean onlyIntegers;
    /** Is the matrix locked? If locked, matrix contents cannot be changed */
    private boolean isLocked;

    public CrossImpactMatrix(int varCount, boolean onlyIntegers, String[] names, double[] values) {
        super(varCount, onlyIntegers, names, values);
        this.onlyIntegers = onlyIntegers;
        this.transformations = new LinkedList<>();
    }

    public CrossImpactMatrix(int varCount, boolean onlyIntegers, String[] names) {
        this(varCount, onlyIntegers, names, new double[varCount*varCount]);
    }

    public CrossImpactMatrix(int varCount, boolean onlyIntegers) {
        this(varCount, onlyIntegers, createNames(varCount), new double[varCount*varCount]);
    }

    public CrossImpactMatrix(int varCount, String[] names) {
        this(varCount, false, names, new double[varCount*varCount]);
    }

    public CrossImpactMatrix(int varCount) {
        this(varCount, false, createNames(varCount), new double[varCount*varCount]);
    }

    public CrossImpactMatrix(boolean onlyIntegers, String[] names, double[][] values) {
        this(values.length, onlyIntegers, names, flattenArray(values));
    }
    
    public void setImpact(int impactor, int impacted, double value) {
        // Variables cannot have an impact on themselves
        if (impactor == impacted && value != 0) {
            throw new IllegalArgumentException(String.format("Attempt to set an impact (%s) of variable (%s) on itself", value, impactor));
        }
        super.setValue(impactor, impacted, value);
    }
    

    /**
     * Returns a String containing a list of descriptions of transformations
     * performed on this matrix
     * @return String description of transformations
     */
    public String getTransformations() {
        StringBuilder sb = new StringBuilder();
        transformations.stream().forEach((String s) -> {
            sb.append(" * ").append(s).append("\n");
        });
        return sb.toString();
    }

    /**
     * Adds a description of a transformation performed on this matrix
     * @param description
     */
    public void noteTransformation(String description) {
        assert description != null;
        assert !description.equalsIgnoreCase("");
        this.transformations.add(description);
    }

    /**
     * Returns true if <code>SquareMatrix</code> is locked, false otherwise.
     * SquareMatrix being locked means
     * that impact values cannot be changed anymore.
     * @return <i>true</i> if matrix is locked, <i>false</i> otherwise.
     */
    public boolean isLocked() {
        return isLocked;
    }

    /**
     * Locks the matrix so that contents cannot be changed.
     */
    public void lock() {
        this.isLocked = true;
    }

    /**
     * Returns an importance matrix for the impact matrix.
     * Importance for each impactor-impacted pair or matrix entry
     * is calculated by dividing the impact
     * by the sum of the absolute impacts on the impacted variable.
     * @return Importance matrix derived from this <code>EXITImpactMatrix</code>
     */
    public CrossImpactMatrix importanceMatrix() {
        CrossImpactMatrix importanceMatrix = 
                new CrossImpactMatrix(varCount, onlyIntegers, names, values);
        // (this.getMaxImpact() * this.varCount, this.varCount, this.onlyIntegers, this.names);
        for (int impacted = 1; impacted <= this.varCount; impacted++) {
            for (int impactor = 1; impactor <= this.varCount; impactor++) {
                double shareOfAbsoluteSum = this.columnSum(impacted, true) != 0 ? this.getValue(impactor, impacted) / this.columnSum(impacted, true) : 0;
                double absShare = Math.abs(shareOfAbsoluteSum);
                double importance = shareOfAbsoluteSum < 0 ? -absShare : absShare;
                importanceMatrix.setValue(impactor, impacted, importance);
            }
        }
        importanceMatrix.noteTransformation("Derived as an importance matrix");
        return importanceMatrix;
    }

    /**
     * Normalizes this matrix by dividing each impact value
     * by the average distance of values from zero.
     * The maximum impact value will be the greatest value in the normalized matrix.
     * @return Normalized matrix
     */
    public CrossImpactMatrix normalize() {
        double[] normalizedValues = this.values.clone();
        double averageDistanceFromZero = this.matrixAverage(true);
        for (int i=0; i < normalizedValues.length; i++) {
            normalizedValues[i] /= averageDistanceFromZero;
        }
        CrossImpactMatrix normalized = new CrossImpactMatrix(this.varCount, false, this.names, normalizedValues);
        normalized.noteTransformation("normalized");
        return normalized;
    }

    /**
     * Normalizes this matrix by dividing each impact value
     * by the average distance of values from zero
     * and linearly scaling the matrix to have TODO
     * @param scaleTo
     * @return 
     */
    public CrossImpactMatrix normalize(double scaleTo) {
        CrossImpactMatrix normalized = this.normalize().scale(scaleTo);
        return normalized;
    }

    /**
     * Returns a new <code>EXITImpactMatrix</code>
     * derived from this impact matrix
     * where impact values are rounded to the nearest integer
     * @return New <code>EXITImpactMatrix</code> where impact values are rounded to the nearest integer
     */
    public CrossImpactMatrix round() {
        return this.round((int) Math.round(this.greatestValue()));
    }

    /**
     * Scales the impact matrix by calling {@link CrossImpactMatrix#scale}
     * and rounding the values of resulting <code>EXITImpactMatrix</code>
     * to the nearest integer.
     * The <code>onlyIntegers</code> property for the new <code>EXITImpactMatrix</code>
     * will be true.
     * @param scaleTo The value that the highest absolute impact value in the matrix will be scaled to
     * @return <code>EXITImpactMatrix</code> that has its values scaled to <b>scaleTo</b> and rounded to nearest integers
     */
    public CrossImpactMatrix round(int scaleTo) {
        if (Math.abs(scaleTo) < 1) {
            throw new IllegalArgumentException("scaleTo cannot be 0");
        }
        CrossImpactMatrix roundedMatrix = this.scale(scaleTo);
        for (int i = 0; i < roundedMatrix.values.length; i++) {
            roundedMatrix.values[i] = Math.round(roundedMatrix.values[i]);
        }
        return roundedMatrix;
    }

    /**
     * Scales the impact matrix to have its greatest absolute value
     * to be equal to value of <b>scaleTo</b>.
     * @param scaleTo The value that the greatest absolute impact value in the matrix will be scaled to
     * @return <code>EXITImpactMatrix</code> scaled according to <b>scaleTo</b> argument.
     */
    public CrossImpactMatrix scale(double scaleTo) {
        if (scaleTo == 0) {
            throw new IllegalArgumentException("scaleTo cannot be 0");
        }
        double max = greatestValue();
        double[] scaledImpacts = this.values.clone();
        for (int i = 0; i < values.length; i++) {
            scaledImpacts[i] = values[i] / max * scaleTo;
        }
        return new CrossImpactMatrix(this.varCount, this.onlyIntegers, this.names, scaledImpacts);
    }

    /**
     * Returns a matrix where values of <b>subtractMatrix</b> have
     * been subtracted from values of this matrix.
     * @param subtractMatrix Matrix whose values are subtracted from values of this matrix
     * @return Difference matrix
     */
    public CrossImpactMatrix differenceMatrix(CrossImpactMatrix subtractMatrix) {
        if (this.varCount != subtractMatrix.varCount) {
            throw new IllegalArgumentException("Comparison matrix is of different size");
        }
        
        boolean bothMatricesIntegral = this.onlyIntegers && subtractMatrix.onlyIntegers;
        CrossImpactMatrix differenceMatrix = new CrossImpactMatrix(this.varCount, bothMatricesIntegral, this.names, this.values);
        for (int i = 0; i < differenceMatrix.values.length; i++) {
            differenceMatrix.values[i] -= subtractMatrix.values[i];
        }
        differenceMatrix.noteTransformation("Derived as a difference matrix");
        return differenceMatrix;
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
        for (int i = 1; i <= normMatrix.varCount; i++) {
            if (Math.abs(normMatrix.getValue(i, varIndex)) >= average) {
                impactors.add(i);
            }
        }
        return impactors;
    }

    /**
     * Returns a <code>VarInfoTable</code>
     * where for each variable, the sum of impacts on other variables
     * and the sum of impacts of other variables have been calculated.
     * Variables are classified into four classes on the basis of this information.
     * <table><tr><th></th><th>driven &lt; average</th><th>driven &gt; average</th></tr><tr><td>driver &gt; average</td><td>Driver</td><td>Critical</td></tr><tr><td>driver &lt; average</td><td>Stable</td><td>Reactive</td></tr></table>
     *
     * @return <code>EXITImpactMatrix</code> row and column sums for each variable and the classification in a <code>VarInfoTable</code>
     */
    public VarInfoTable<String> driverDriven() {
        VarInfoTable<String> driverDrivenInfo = new VarInfoTable<>(Arrays.asList("Driver", "Driven", "Type"));
        double driverAverage = rowSum(1, true);
        double drivenAverage = columnSum(1, true);
        for (int i = 2; i <= varCount; i++) {
            driverAverage = ((i - 1) * driverAverage + rowSum(i, true)) / i;
            drivenAverage = ((i - 1) * drivenAverage + columnSum(i, true)) / i;
        }
        for (int i = 1; i <= this.varCount; i++) {
            String name = this.getNamePrint(i);
            double driver = this.rowSum(i, true);
            double driven = this.columnSum(i, true);
            boolean isDriver = driver > driverAverage;
            boolean isDriven = driven > drivenAverage;
            String type = isDriver ? isDriven ? "Critical" : "Driver" : isDriven ? "Reactive" : "Stable";
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
            for (Integer imp : this.aboveAverageImpactors(i)) {
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
        EXITImpactMatrix m = new EXITImpactMatrix(this.scale(1));
        for (int i = 1; i <= varCount; i++) {
            report += String.format("Important drivers for %s:%n", m.getName(i));
            for (Integer imp : m.aboveAverageImpactors(i)) {
                report += String.format("\t%s (%1.1f)%n", m.getName(imp), m.getValue(imp, i));
            }
        }
        return report;
    }
    
    
}

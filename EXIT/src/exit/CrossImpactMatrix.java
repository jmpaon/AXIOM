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
        for (int i = 0; i < normalizedValues.length; i++) {
            normalizedValues[i] /= averageDistanceFromZero;
        }
        double[] sorted = normalizedValues.clone();
        return new CrossImpactMatrix(this.varCount, false, this.names, normalizedValues);
    }

    public EXITImpactMatrix normalize(double scaleTo) {
        EXITImpactMatrix normalized = this.normalize().scale(scaleTo);
        normalized.noteTransformation("Normalized to " + scaleTo);
        return normalized;
    }

    /**
     * Returns a new <code>EXITImpactMatrix</code>
     * derived from this impact matrix
     * where impact values are rounded to the nearest integer
     * @return New <code>EXITImpactMatrix</code> where impact values are rounded to the nearest integer
     */
    public EXITImpactMatrix round() {
        return this.round((int) Math.round(this.getMaxImpact()));
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
    public EXITImpactMatrix round(int scaleTo) {
        if (Math.abs(scaleTo) < 1) {
            throw new IllegalArgumentException("scaleTo cannot be 0");
        }
        EXITImpactMatrix roundedMatrix = this.scale(scaleTo);
        for (int i = 0; i < roundedMatrix.values.length; i++) {
            roundedMatrix.values[i] = Math.round(roundedMatrix.values[i]);
        }
        return new EXITImpactMatrix(roundedMatrix.maxImpact, roundedMatrix.varCount, true, roundedMatrix.names, roundedMatrix.values);
    }

    /**
     * Scales the impact matrix to have its greatest absolute value
     * to be equal to value of <b>scaleTo</b>.
     * @param scaleTo The value that the greatest absolute impact value in the matrix will be scaled to
     * @return <code>EXITImpactMatrix</code> scaled according to <b>scaleTo</b> argument.
     */
    public EXITImpactMatrix scale(double scaleTo) {
        if (scaleTo == 0) {
            throw new IllegalArgumentException("scaleTo cannot be 0");
        }
        double max = greatestValue();
        double[] scaledImpacts = this.values.clone();
        for (int i = 0; i < values.length; i++) {
            scaledImpacts[i] = values[i] / max * scaleTo;
        }
        return new EXITImpactMatrix(Math.abs(scaleTo), this.varCount, this.onlyIntegers, this.names, scaledImpacts);
    }
    
    
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;


/**
 * <code>MicmacMatrix</code> class provides an implementation of 
 * Michel Godet's MICMAC analysis.
 * In MICMAC, the cross-impact matrix variables are ordered by their
 * influence or dependency, calculated as the row or column sums.
 * The variable with the highest sum becomes the first variable 
 * in the ordering.
 * The direct impact matrix gives an initial ordering of the variables;
 * the matrix is then squared iteratively, with the stopping condition
 * that if the ordering of the variables does not change after squaring the matrix,
 * iteration can be stopped and the final ordering is the MICMAC classification.
 * 
 * The implementation of MICMAC is provided to enable comparisons with
 * MICMAC and EXIT methods.
 * 
 * @author jmpaon
 */
public class MicmacMatrix extends SquareDataMatrix {
    
    public static enum Orientation {
        byDependency,
        byInfluence;
        
        @Override
        public String toString() {
            if(this==byDependency) return "by dependency";
            if(this==byInfluence)  return "by influence";
            return "Unknown";
        }
    }
    
    public MicmacMatrix(SquareDataMatrix matrix) {
        super(matrix.varCount, matrix.onlyIntegers, matrix.names.clone(), matrix.values.clone());
    }
    
    public MicmacMatrix(int varCount, boolean onlyIntegers, String[] names, double[] values) {
        super(varCount, onlyIntegers, names, values);
    }
    
    public MicmacMatrix(int varCount, boolean onlyIntegers, String[] names) {
        super(varCount, onlyIntegers, names);
    }
    
    
    
    /**
     * Returns a info table about the MICMAC rankings of the variables.
     * @param orientation [byInfluence|byDependency]
     * @return <code>VarInfoTable</code> containing initial and MICMAC rankings of the matrix variables
     * @see MicmacMatrix#altMICMAC(exit.MicmacMatrix.Orientation) alternative implementation
     */
    public VarInfoTable<String> MICMACranking(Orientation orientation) {
        
        Ordering initialOrdering = new Ordering(this, orientation);
        
        /* Place initial ordering to the iterated ordering variable */
        Ordering iterOrdering = new Ordering(this, orientation);
        
        /* Place the first power matrix in the iterated matrix variable */
        MicmacMatrix iterMatrix = this.power();
        
        int iter=0;
        
        /* 
        System.out.println("MICMAC calculation, " + orientation.toString());
        System.out.println("MICMAC iteration 0 (Initial matrix)");
        System.out.println(this);
        System.out.println(this.getOrdering(orientation));
        */
        
        while(true) {
            
            /*
            System.out.println("MICMAC iteration " + ++iter);
            System.out.println(iterMatrix);
            System.out.println(iterMatrix.getOrdering(orientation));
            */
            
            /* Stop MICMAC iteration if the previous ordering is equal to current power matrix' ordering:
            This means that the ordering has become stable and the MICMAC end condition has been reached. */
            if(iterOrdering.equals(iterMatrix.getOrdering(orientation))) break;
            
            /* Save this iteration's ordering */
            iterOrdering = new Ordering(iterMatrix, orientation);
            
            /* Iterate to the next power matrix */
            iterMatrix = iterMatrix.power();
            
            
        }
        
        VarInfoTable<String> rankings = new VarInfoTable<>(String.format("Initial ranking and MICMAC ranking of variables %s", orientation.toString()), Arrays.asList("Initial", "MICMAC"));
        
        Collections.sort(initialOrdering.ordering, initialOrdering.getVariableIndexComparator());
        Collections.sort(iterOrdering.ordering, iterOrdering.getVariableIndexComparator());
        
        for(int i = 0; i < initialOrdering.ordering.size(); i++) {
            String varName = this.getNamePrint(i+1);
            String initPos   = String.valueOf(initialOrdering.ordering.get(i).position);
            String micmacPos = String.valueOf(iterOrdering.ordering.get(i).position);
            rankings.put(varName, Arrays.asList(initPos, micmacPos));
        }
        
        return rankings;
        
    }
    
    /**
     * Multiplies the matrix by itself (resulting in power matrix).
     * This functionality is provided to implement the MICMAC method
     * inside EXIT for comparisons.
     * @return Squared matrix
     */
    public MicmacMatrix power() {
        
        MicmacMatrix powerMatrix = new MicmacMatrix(this);
        
        for (int row = 1; row <= varCount; row++) {
            for (int col = 1; col <= varCount; col++) {
                powerMatrix.setValue(row, col, this.matrixMultiplication(row, col));
            }
        }
        return powerMatrix;
    }
    
    
    /**
     * 
     * @param orientation
     * @return 
     */
    public MicmacMatrix iteratedPowerMatrix(Orientation orientation) {
        AltOrdering initialOrdering = new AltOrdering(this, orientation);
        AltOrdering powerOrdering = new AltOrdering(this.power(), orientation);
        
        MicmacMatrix powerMatrix = new MicmacMatrix(this);
        
        /**
         * Square the matrix and derive a new ordering from that 
         * as long as the ordering changes.
         * Iteration is stopped when the ordering isn't different from
         * the ordering of the previous power matrix.
         */
        while(!initialOrdering.equals(powerOrdering)) {
            initialOrdering = powerMatrix.getAltOrdering(orientation);
            powerMatrix     = powerMatrix.power();            
            powerOrdering   = powerMatrix.getAltOrdering(orientation);
        }
        
        return powerMatrix;
    }
    
    
    /**
     * Returns a representation of the matrix where 
     * only the presence of impacts are described.
     * This transformation can be useful in MICMAC analysis.
     * For all matrix values, the transformed matrix will contain
     * <i>1</i> if the original value is greater or equal to <b>threshold</b>
     * and <i>0</i> if the original value is smaller than <b>threshold</b>.
     * @param threshold The value used in booleanization of the matrix values
     * @return Matrix representing presence of impacts in a boolean fashion
     */
    public MicmacMatrix booleanImpactMatrix(double threshold) {
        if(threshold<=0) throw new IllegalArgumentException("Threshold value must be greater than 0");
        if(threshold>this.greatestValue()) 
            throw new IllegalStateException(String.format("Threshold value is %2.2f when the greatest value in the matrix is %2.2f", threshold, greatestValue()));
        
        MicmacMatrix transformedMatrix = new MicmacMatrix(this);
        for (int i = 0; i < transformedMatrix.values.length ; i++) {
            transformedMatrix.values[i] = Math.abs(transformedMatrix.values[i]) >= threshold ? 1 : 0;
        }
        return transformedMatrix;
    }
    
    
    /**
     * Returns a representation of the matrix where 
     * only the presence of impacts are described.
     * 
     * @param factor Values greater than or equal to factor * median will be transformed to 1,
     * smaller will be transformed to 0.
     * @return Matrix representing presence of impacts in a boolean fashion
     */
    public MicmacMatrix booleanImpactMatrix_byMedian(double factor) {
        if(factor<=0) throw new IllegalArgumentException(String.format("Factor (value=%2.2f) must be greater than 0", factor));
        double threshold = this.matrixMedian(true) * factor;
        return booleanImpactMatrix(threshold);
    }
    
    
    /**
     * Returns a representation of the matrix where 
     * only the presence of impacts are described.
     * This method returns a boolean impact matrix with desired
     * density (share of entries with impacts per total entries).
     * If density is <i>0.5</i>, about 50% of the entries will have value 1 
     * indicating presence of impact and the rest will have value 0.
     * @param density Desired share of matrix entries which should have an impact
     * @return Matrix representing presence of impacts in a boolean fashion
     */
    public MicmacMatrix booleanImpactMatrix_byDensity(double density) {
        if(density <= 0 || density >= 1) throw new IllegalArgumentException(String.format("Density value (%2.2f) must be in range ]0..1[", density));
        
        double[] sortedValues = this.values.clone();
        for(int i=0;i<sortedValues.length;i++) 
            sortedValues[i] = Math.abs(sortedValues[i]);
        
        double threshold = sortedValues[(int)(Math.round(sortedValues.length * density)-1)];
        return booleanImpactMatrix(threshold);
    }
    
    
    
    /**
     * Tests if matrix is "boolean", i.e.contains only values 0 and 1.
     * Boolean matrices are useful in MICMAC-style cross-impact analysis.
     * @return 
     */
    public boolean isMatrixBoolean() {
        for(Double val : values) {
            if(!(val == 1 || val == 0)) return false;
        }
        return true;
    }
    
    
    
    /**
     * Returns the ordering of the variables in this matrix.
     * Ordering of a variable is based on either the apparent influence
     * (row sum) or the apparent dependency (column sum) in the cross-impact system.
     * <b>Orientation</b> determines which ordering is returned.
     * @param orientation <i>[byInfluence|byDependency]</i> 
     * @return Ordering of the variables in this matrix
     */
    Ordering getOrdering(Orientation orientation) {
        return new Ordering(this, orientation);
    }
    
    
    /**
     * Returns the ranking of a variable in an ordering of the variables
     * in the matrix by row or column sum.
     * <b>orientation</b> determines whether row or column sum is used.
     * For example, if the variable has 2 variables that have a 
     * higher absolute row/column sum, the sumRanking for that variable will be
     * 3 (2+1). The variable with the highest row/column sum will have ranking 1.
     * @param varIndex Index of the variable for which the sumRanking is calculated
     * @param orientation <i>[byInfluence|byDependency]</i> 
     * if <i>byInfluence</i>, row sums are used;
     * if <i>byDependency</i>, column sums are used;
     * @return the ranking of the variable with index <b>varIndex</b>
     */
    public int sumRanking(int varIndex, Orientation orientation) {
        if(varIndex < 1 || varIndex > this.varCount) 
            throw new IndexOutOfBoundsException(String.format("varIndex %d is out of bounds (varCount %d)", varIndex, this.varCount));
        
        double sum = orientation == Orientation.byInfluence ? rowSum(varIndex, true) : columnSum(varIndex, true);
        int greaterCount=0;
        
        for(int i=1 ; i<=this.varCount ; i++) {
            double comparedSum = orientation == Orientation.byInfluence ? rowSum(i, true) : columnSum(i, true);
            if(comparedSum > sum) greaterCount++;
        }
        
        return greaterCount+1;
        
    }
    
    
    /**
     * Returns the initial ranking
     * and the MICMAC ranking
     * of the variables in this matrix
     * in a <code>VarInfoTable</code>.
     * Variable with rank 1 has the highest score
     * by influence/dependency, rank 2 has the second highest score etc.
     * @param orientation <i>[byInfluence|byDependency]</i> is the ranking based on row or column sums?
     * Summing absolute row values gives a score indicating influence in the cross-impact system,
     * summing absolute column values gives a score indicating dependency.
     * @return Initial and MICMAC rankings of the matrix variables
     * @see MicmacMatrix#MICMACranking(exit.MicmacMatrix.Orientation) Alternative implementation
     */
    public VarInfoTable altMICMAC(Orientation orientation) {
        AltOrdering ord = new AltOrdering(this, orientation);
        MicmacMatrix powerMatrix = this.power();
        
        /**
         * Square the matrix and derive a new ordering from that 
         * as long as the ordering changes.
         * Iteration is stopped when the ordering isn't different from
         * the ordering of the previous power matrix.
         */
        while(! ord.equals(powerMatrix.getAltOrdering(orientation))  ) {
            System.out.println(powerMatrix);
            System.out.println(ord);
            System.out.println(ord.isUnambiguous());
            
            ord = powerMatrix.getAltOrdering(orientation);
            powerMatrix = powerMatrix.power();
        }
        
        VarInfoTable<Integer> rankings = new VarInfoTable<>(
                String.format("Initial ranking and MICMAC ranking of variables %s (alternative method):", orientation), 
                Arrays.asList("Initial","MICMAC")
        );
        
        for(int i = 0; i < ord.positions.length; i++) {
            String varname = this.getNameShort(i+1);
            int micmacpos = ord.positions[i];
            int initpos = this.getAltOrdering(orientation).positions[i];
            
            rankings.put(varname, Arrays.asList(initpos, micmacpos));
            
        }
        return rankings;
    }
    
    /**
     * 
     * @param orientation
     * @return 
     */
    public AltOrdering getAltOrdering(Orientation orientation) {
        return new AltOrdering(this, orientation);
    }
    
    
    /**
     * Sums the products of each entry in <b>row</b> and corresponding entry in <b>col</b>.
     * Used in {@link MicmacMatrix#power()} calculation.
     * @param row Index of row
     * @param col Index of col
     * @return Sum of pairwise products of entries in row <b>row</b> and column <b>col</b>.
     */
    private double matrixMultiplication(int row, int col) {
        double result=0;
        for (int i = 1; i <= varCount; i++) {
            result += getValue(row, i) * getValue(i, col);
        }
        return result;
    }
    
    
    /**
     * This class represents a MICMAC ordering.
     * The orderings can be such that several variables in the matrix
     * have the same position, if their "scores" 
     * (row or column sums, depending on the orientation)
     * are equal.
     */
    class Ordering {
        
        /** Matrix from which the ordering is derived */
        public final MicmacMatrix matrix;
        
        /** The ordering */
        public final List<Info> ordering;
        
        
        /**
         * Constructor for <code>Ordering</code>.
         * @param matrix The matrix this ordering relates to
         * @param orientation <i>[byInfluence|byDependency]</i> Is the ordering constructed based on influence or dependency?
         */
        public Ordering(MicmacMatrix matrix, Orientation orientation) {
            if(matrix==null) throw new NullPointerException("matrix argument is null");
            this.matrix = matrix;
            this.ordering = new ArrayList<>();
            
            // Collect the values for the ordering
            for(int i=1;i<=matrix.varCount;i++) {
                this.ordering.add(new Info(i, orientation==Orientation.byInfluence ? matrix.rowSum(i, true) : matrix.columnSum(i, true)));
            }
            
            // Sort the ordering by sums (row or column sums), greatest value first
            Collections.sort(ordering);
            
            /* Assign positions for the items in ordering; 
            if the sum for two consecutive items is the same, 
            they get the same position value */
            int position=1;
            for(int i=0;i<ordering.size();i++) {
                ordering.get(i).position = position;
                if(i<ordering.size()-1 && !Objects.equals(ordering.get(i).sum, ordering.get(i+1).sum))
                    position++;
            }
        }
        
        
        /**
         * Returns a comparator that compares ordering <code>Info</code> entries 
         * by variable indices
         * @return Comparator for {@link Ordering$info} based on variable indices
         */
        public Comparator<Info> getVariableIndexComparator() {
            return (Info o1, Info o2) -> o1.index.compareTo(o2.index);
        }        
        
        
        /**
         * Two <code>Ordering</code>s are equal if they have the same 
         * variable index values and ordering position values 
         * in the same array indices.
         * @param o Compared object, only <code>Ordering</code>s can be equal
         * @return <i>true</i> if the orderings have the same positions and indices in the same order
         */
        @Override
        public boolean equals(Object o) {
            if(o==null) return false;
            if(!(o instanceof Ordering)) return false;
            
            final Ordering ord = (Ordering)o;
            if(this.ordering.size() != ord.ordering.size()) return false;
            for(int i=0;i<this.ordering.size();i++) {
                if(this.ordering.get(i).position != ord.ordering.get(i).position) return false;
                if(this.ordering.get(i).index != ord.ordering.get(i).index) return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 83 * hash + Objects.hashCode(this.matrix);
            hash = 83 * hash + Objects.hashCode(this.ordering);
            return hash;
        }
        
        
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for(Info info : this.ordering) {
                sb.append(info.position).append(": ").append(this.matrix.getNameShort(info.index)).append(String.format(" (%2.2f)     ", info.sum));
            }
            return sb.append("\n").toString();
        }
        
        /**
         * This class holds the information for ordering of the matrix variables.
         */
        class Info implements Comparable<Info> {
            public final Integer index;
            public final Double sum;
            public Integer position;

            public Info(int index, double sum) {
                this.index = index;
                this.sum = sum;
            }

            @Override
            public int compareTo(Info o) {
                if(this.sum > o.sum) return -1;
                if(this.sum < o.sum) return  1;
                return this.index.compareTo(o.index);
            }
            
        }
        
    }
    
    class AltOrdering {
        
        public final MicmacMatrix matrix;
        public final Orientation orientation;
        public final int[] positions;
        public final double[] sums;
        
        
        public AltOrdering(MicmacMatrix matrix, Orientation orientation) {
            
            if(matrix==null) throw new NullPointerException("matrix argument is null");
            this.matrix = matrix;
            this.orientation = orientation;
            this.positions = new int[this.matrix.varCount];
            this.sums = new double[this.matrix.varCount];
            
            for (int i = 0; i < this.matrix.varCount; i++) {
                positions[i] = this.matrix.sumRanking(i+1, orientation);
                sums[i] = this.orientation == Orientation.byInfluence ?
                        this.matrix.rowSum(i+1, true) :
                        this.matrix.columnSum(i+1, true);
            }
        }
        
        
        /**
         * Tests if the ordering is unambiguous:
         * returns <i>true</i> if no ranking is shared.
         * It would be ideal that the MICMAC iteration would 
         * continue even after the first stable ranking, 
         * if some rankings are shared between variables at that point.
         * It is however possible that iteration will not ever result
         * in an unambiguous ranking 
         * and the termination condition for the iteration
         * is never reached.
         * This will also happen if the double values in the iteratively
         * squared matrix overflow to infinity, causing them all to rank equally.
         * @return 
         */
        public boolean isUnambiguous() {
            List<Integer> usedPositions = new ArrayList<>();
            for(Integer i : this.positions) {
                if(usedPositions.contains(i)) return false;
                usedPositions.add(i);
            }
            return true;
        }
        
        @Override
        public boolean equals(Object o) {
            if(! (o instanceof AltOrdering)) return false;
            AltOrdering ao = (AltOrdering) o;
            
            if(!ao.orientation.equals(this.orientation)) return false;
            if(!Arrays.equals(ao.positions, this.positions)) return false;
            
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 89 * hash + Objects.hashCode(this.orientation);
            hash = 89 * hash + Arrays.hashCode(this.positions);
            return hash;
        }
        
        
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for(int i=0;i<positions.length;i++) {
                sb.append(String.format("%s: %d (%2.1f)", this.matrix.getNameShort(i+1), positions[i], sums[i] ));
                if(i < positions.length) sb.append(", ");
            }
            return sb.toString();
        }
        
        
    }
    
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;


/**
 *
 * @author jmpaon
 */
public class MicmacMatrix extends SquareDataMatrix {
    
    public static enum Orientation {
        byDependency,
        byInfluence
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
     * @param orientation
     * @return 
     */
    public VarInfoTable<String> MICMACranking(Orientation orientation) {
        
        Ordering initialOrdering = new Ordering(this, orientation);
        
        boolean cont = true;
        Ordering iterOrdering = new Ordering(this, orientation);
        MicmacMatrix iterMatrix = this.power();
        int iter=1;
        
        System.out.println("MICMAC iteration 0");
        System.out.println(this);
        System.out.println(this.getOrdering(orientation));
        
        while(cont) {
            System.out.println("MICMAC iteration " + iter);
            System.out.println(iterMatrix);
            System.out.println(iterOrdering);
            //System.out.println(iterMatrix.power().getOrdering(orientation));
            if(iterOrdering.equals(iterMatrix.getOrdering(orientation))) break;
            iterOrdering = new Ordering(iterMatrix, orientation);
            iterMatrix = iterMatrix.power();
            iter++;
        }
        
        VarInfoTable<String> rankings = new VarInfoTable<>(Arrays.asList("Init", "Initp", "mm", "mmp"));
        for (int i = 1; i <= this.varCount; i++) {
            String initialVar = this.getNameShort(initialOrdering.ordering.get(i-1).index);
            String micmacVar  = this.getNameShort(iterOrdering.ordering.get(i-1).index);
            String initPos = String.valueOf(initialOrdering.ordering.get(i-1).position);
            String micmacPos = String.valueOf(iterOrdering.ordering.get(i-1).position);
            
            //String initialVar = this.getNameShort(initialOrdering.get(i-1));
            //String micmacVar  = this.getNameShort(iterOrdering.get(i-1));
            //String pos = String.valueOf(i);
            
            rankings.put(" MICMAC ", Arrays.asList(initialVar, initPos, micmacVar, micmacPos));
            
        }
        
        return rankings;
        
    }
    
    /**
     * Multiplies the matrix by itself (resulting in power matrix).
     * This functionality is provided to implement the MICMAC method
     * inside EXIT for comparisons.
     * @return 
     */
    public MicmacMatrix power() {
        
        //MicmacMatrix powerMatrix = new MicmacMatrix(this.varCount, this.onlyIntegers, this.names);
        MicmacMatrix powerMatrix = new MicmacMatrix(this);
        
        for (int row = 1; row <= varCount; row++) {
            for (int col = 1; col <= varCount; col++) {
                powerMatrix.setValue(row, col, this.matrixMultiplication(row, col));
            }
        }
        return powerMatrix;
    }
    
    
    public MicmacMatrix simplify(double threshold) {
        MicmacMatrix simplifiedMatrix = new MicmacMatrix(this);
        for (int i = 0; i < simplifiedMatrix.values.length ; i++) {
            simplifiedMatrix.values[i] = Math.abs(simplifiedMatrix.values[i]) >= threshold ? 1 : 0;
        }
        
        return simplifiedMatrix;
        
    }
    
    
    
    Ordering getOrdering(Orientation orientation) {
        if(orientation == Orientation.byDependency) {
            return new Ordering(this, orientation);
        } else {
            return new Ordering(this, orientation);
        }
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
     * Here several variables can share the same position
     * if their scores (row or column sum) are equal.
     * This ordering gives slightly more information
     * than the "normal" ordering implemented in
     * {@link MicmacMatrix$Ordering}
     */
    class Ordering {
        public final MicmacMatrix matrix;
        public final List<Info> ordering;
        
        public Ordering(MicmacMatrix matrix, Orientation orientation) {
            if(matrix==null) throw new NullPointerException("matrix argument is null");
            this.matrix = matrix;
            this.ordering = new ArrayList<>();
            
            for(int i=1;i<=matrix.varCount;i++) {
                this.ordering.add(new Info(i, orientation==Orientation.byInfluence ? matrix.rowSum(i, true) : matrix.columnSum(i, true)));
            }
            
            Collections.sort(ordering);
            
            int position=1;
            for(int i=0;i<ordering.size();i++) {
                ordering.get(i).position = position;
                if(i<ordering.size()-1 && !Objects.equals(ordering.get(i).sum, ordering.get(i+1).sum))
                    position++;
            }
        }
        
        
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
    

    

    
    
    
}

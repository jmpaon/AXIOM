/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exit;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author jmpaon
 */
public class VarPair {
    
    private final CrossImpactMatrix matrix;
    private final int first;
    private final int last;
    
    public VarPair(CrossImpactMatrix matrix, int first, int last) {
        if(matrix == null) throw new NullPointerException("null matrix argument");
        if(first < 1 || first > matrix.getVarCount()) throw new IndexOutOfBoundsException("matrix does not have variable with index " + first);
        if(last  < 1 || last  > matrix.getVarCount()) throw new IndexOutOfBoundsException("matrix does not have variable with index " + last );
        if(first == last) throw new IllegalArgumentException("index for first is equal to index for last: " + this.toString());
        
        this.matrix = matrix;
        this.first = first;
        this.last = last;
    }
    
    public List<Integer> expanders() {
        List<Integer> list = new LinkedList<>();
        for(int i=1;i<=matrix.getVarCount();i++) {
            if(i != first && i != last) list.add(i);
        }
        return list;
    }
    
    
    
    
}

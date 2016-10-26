/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package axiom.model;

import java.util.Comparator;
import java.util.Objects;

/**
 * L 2-tuple.
 * @author jmpaon
 * @param <L> Type of <b>left</b>
 * @param <R> Type of <b>right</b>
 */
public class Pair<L, R> {
    public L left;
    public R right;

    /**
     * Constructor for <code>Pair</code>.
     *
     * @param left  the left object in the Pair
     * @param right the right object in the pair
     */
    public Pair(L left, R right) {
        this.left  = left;
        this.right = right;
    }

    @Override
    public boolean equals(Object o) {
        
        if(o == null) return false;
        if(!o.getClass().isAssignableFrom(Pair.class)) return false;
        
        //if (!(o instanceof Pair)) {
        //    return false;
        //}
        
        Pair<?, ?> p = (Pair<?, ?>) o;
        return p.left.equals(left) && p.right.equals(right);
        
        //return Objects.equals(p.left, left) && Objects.equals(p.right, right);
    }

    @Override
    public int hashCode() {
        return (left == null ? 0 : left.hashCode()) ^ (right == null ? 0 : right.hashCode());
    }

    /**
     * Static method for creating a pair.
     * @param left the left object in the Pair
     * @param right the right object in the pair
     * @return left Pair that is templatized with the types of left and right
     */
    public static <L, R> Pair <L, R> create(L left, R right) {
        return new Pair<>(left, right);
    }
    
    @Override
    public String toString() {
        return "Pair["+this.left + " , " + this.right + "]";
    }
    

}
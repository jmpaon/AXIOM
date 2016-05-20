/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exit;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.List;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * @author jmpaon
 */
public class ImpactChain implements Comparable<ImpactChain> {
    public final CrossImpactMatrix matrix;
    public final List<Integer> chainMembers;
    public final int memberCount;
    
    
    public ImpactChain(CrossImpactMatrix matrix, List<Integer> chainMembers) throws ImpactChainException {
        if(matrix == null) throw new NullPointerException("matrix is null");
        this.matrix = matrix;
        if(chainMembers == null) {
            this.chainMembers = new LinkedList();
        } else {
            this.chainMembers = chainMembers;
        }
        
        this.memberCount = this.chainMembers.size();
    }
    
    /**
     * Calculates the impact contribution of the <u>last</u> variable in the chain
     * through the impact chain.
     * The direct impact of the last variable can be and usually is greater
     * than the indirect impact through the chain.
     * TODO explain impact contribution
     * @return
     * @throws ArgumentException 
     */
    public double chainedImpact() throws ArgumentException {
        return chainedImpact(chainMembers);
    }
    
    /**
     * 
     * @param chain
     * @return
     * @throws ArgumentException 
     */
    private double chainedImpact(List<Integer> chain) throws ArgumentException {
        if(chain == null) { throw new NullPointerException("chain argument is null"); }
        if(chain.isEmpty()) return 1;
        if(chain.size()==1) return 1;
        return (matrix.getImpact(chain.get(0),chain.get(1))/matrix.getMaxImpact()) * chainedImpact(chain.subList(1, chain.size()-1));
    }
    
    public Set<ImpactChain> continuedByOneVariable() throws ImpactChainException {
        Set<ImpactChain> continued = new TreeSet<>();
        Set<Integer> notIncluded = notInThisChain();
        
        for(Integer i : notIncluded) {
            List cm = new LinkedList(chainMembers);
            cm.add(i);
            ImpactChain c = new ImpactChain(this.matrix, cm);
            continued.add(c);
        }
        
        return continued;
        
    }
    
    public Set<Integer> notInThisChain() {
        Set<Integer> s = new TreeSet<>();
        for(int i = 1; i <= matrix.getVarCount() ; i++ ) {
            if(! chainMembers.contains(i)) {
                s.add(i);
            }
        }
        return s;
    }
    
    @Override
    public String toString() {
        String s="";
        
        for(Integer i : chainMembers) {
            try {
                s += matrix.getName(i);
                if( ! i.equals(chainMembers.get(chainMembers.size()-1))) { s += " -> "; }
                // if( i != chainMembers.get(chainMembers.size()-1)) { s += " -> "; }
            } catch (ArgumentException ex) {
                Logger.getLogger(ImpactChain.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        return s;
    }
    
    /**
     * Compares two <code>ImpactChain</code>s.
     * Shorter impact chains are ordered before longer impact chains.
     * Equal-length impact chains are ordered by the member indices.
     * @param ic <code>ImpactChain</code> to compare against.
     * @return -1 if <b>ic</b> is greater, 0 if equal, 1 if smaller.
     */
    @Override
    public int compareTo(ImpactChain ic) {
        if(ic == null) return 1;
        if(this.memberCount > ic.memberCount) return 1;
        if(this.memberCount < ic.memberCount) return -1;
        Iterator<Integer> i_this = this.chainMembers.iterator(), i_ic = ic.chainMembers.iterator();
        while(i_this.hasNext() && i_ic.hasNext()) {
            int i1 = i_this.next(), i2 = i_ic.next();
            if(i1 > i2) return  1;
            if(i1 < i2) return -1;
        }
        return 0;
    }

    
    
}

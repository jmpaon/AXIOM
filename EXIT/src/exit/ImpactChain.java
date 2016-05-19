/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exit;

import java.util.Set;
import java.util.TreeSet;

/**
 * 
 * @author jmpaon
 */
public class ImpactChain {
    public final CrossImpactMatrix matrix;
    public final Set<Integer> chainMembers;
    public final int memberCount;
    
    
    public ImpactChain(CrossImpactMatrix matrix, Set<Integer> chainMembers) throws ImpactChainException {
        if(matrix == null) throw new NullPointerException("matrix is null");
        this.matrix = matrix;
        if(chainMembers == null) {
            this.chainMembers = new TreeSet();
        } else {
            this.chainMembers = chainMembers;
        }
        
        this.memberCount = this.chainMembers.size();
    }
    
    public Set<ImpactChain> continuedByOneVariable() throws ImpactChainException {
        Set<ImpactChain> continued = new TreeSet<>();
        Set<Integer> notIncluded = notInThisChain();
        
        for(Integer i : notIncluded) {
            Set cm = new TreeSet(chainMembers);
            cm.add(i);
            ImpactChain c = new ImpactChain(this.matrix, cm);
            continued.add(c);
        }
        return continued;
        
    }
    
    Set<Integer> notInThisChain() {
        Set<Integer> s = new TreeSet<>();
        for(int i = 1; i <= matrix.getVarCount() ; i++ ) {
            if(! chainMembers.contains(i)) {
                s.add(i);
            }
        }
        return s;
    }
    
    
}

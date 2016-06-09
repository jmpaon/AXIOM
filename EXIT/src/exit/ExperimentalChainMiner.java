/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exit;

import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import java.util.HashSet;

/**
 *
 * @author jmpaon
 */
public class ExperimentalChainMiner {
    
    private final CrossImpactMatrix matrix;
    
    
    public ExperimentalChainMiner(CrossImpactMatrix matrix) {
        if(matrix == null) throw new NullPointerException("null matrix argument");
        this.matrix = matrix;
    }
    
    
    public Set<ImpactChain> sigPairs(double impactTreshold) {
        Set<ImpactChain> pairs = new HashSet<>();
        for(int first=1; first <= matrix.getVarCount(); first++) {
            for(int last=1; last <= matrix.getVarCount(); last++) {
                if(first != last) {
                    ImpactChain ic = new ImpactChain(matrix, first, last);
                    if(Math.abs(ic.impact()) >= impactTreshold)
                        pairs.add(ic);
                }
            }
        }
        return pairs;
    }
    
    
    public void mineChains(double impactTreshold) {
        Set<ImpactChain> pairs = sigPairs(impactTreshold);
        for(ImpactChain ic : pairs) {
            for(ImpactChain icc : pairs) {
                if(ic.isCombinableWith(icc)) {
                    
                }
            }
        }
        
        
    }
    
    
    
    
    
    
}

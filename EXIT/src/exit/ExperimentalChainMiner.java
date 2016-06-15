/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exit;

import java.util.ArrayList;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeSet;

/**
 * This class contains some more experimental methods for
 * efficient mining the significant impact chains.
 * @author jmpaon
 */
public class ExperimentalChainMiner {
    
    private final CrossImpactMatrix matrix;
    
    
    public ExperimentalChainMiner(CrossImpactMatrix matrix) {
        if(matrix == null) throw new NullPointerException("null matrix argument");
        this.matrix = matrix;
    }
    
    
    public List<ImpactChain> sigPairs(double impactTreshold) {
        List<ImpactChain> pairs = new ArrayList<>();
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
        
        List<ImpactChain> pairs = sigPairs(impactTreshold);
        List<ImpactChain> chains = sigPairs(impactTreshold);
        List<ImpactChain> continued = new ArrayList<>();
        List<ImpactChain> found = new ArrayList<>();
        
        found.addAll(chains);
        boolean newChainsFound= !chains.isEmpty() && !pairs.isEmpty();
        
        while( newChainsFound &&  !chains.isEmpty() ) {
            newChainsFound = false;
            Iterator<ImpactChain> chainIt = chains.iterator();            
            while(chainIt.hasNext()) {
                ImpactChain chain = chainIt.next();
                Iterator<ImpactChain> pairIt  = pairs.iterator();
                while(pairIt.hasNext()) {
                    ImpactChain pair = pairIt.next();
                    if(chain.isCombinableWith(pair)) {
                        ImpactChain combined = chain.combineWith(pair);
                        if(Math.abs(combined.impact()) >= impactTreshold) {
                            continued.add(combined);
                            newChainsFound = true;
                        }
                    }
                }
                
            }
            
            found.addAll(continued);
            chains.clear();
            chains.addAll(continued);
            continued.clear();
        }
        
        // printSet(found);
        
        Set<ImpactChain> significantChains = new TreeSet<>();
        significantChains.addAll(found);
        for(ImpactChain c : found) {
            if(c.hasExpansion()) {
                for(ImpactChain expansion : c.continuedByOne()) {
                    if(Math.abs(expansion.impact()) >= impactTreshold) {
                        significantChains.add(expansion);
                    }
                }
            }   
        }
        
        System.out.println(significantChains.size());
        
    }
    
    public void printSet(Set s) {
        Iterator i = s.iterator();
        while(i.hasNext()) {
            System.out.println(i.next().toString());
        }
        System.out.println(s.size());
    }
    
    public void printList(List l) {
        System.out.println("----------------------------------------------------------------------");
        Iterator i = l.iterator();
        while(i.hasNext()) {
            System.out.println(i.next().toString());
        }
    }    
    
    
    
    
    
}

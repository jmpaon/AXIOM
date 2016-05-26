/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exit;


import java.util.Comparator;
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
    
    
    public ImpactChain(CrossImpactMatrix matrix, List<Integer> chainMembers) {
        if(matrix == null) throw new NullPointerException("matrix is null");
        this.matrix = matrix;
        if(chainMembers == null) {
            this.chainMembers = new LinkedList();
        } else {
            
            // Test chainMembers for duplicates
            Set<Integer> woDuplicates = new TreeSet<>(chainMembers);
            if(woDuplicates.size() != chainMembers.size()) { throw new IllegalArgumentException("duplicate items in chainMembers"); }
            
            // Test that indices in chainMembers are present in matrix of this impactChain
            for(Integer i : chainMembers) {
                if(i < 0 || i > matrix.getVarCount()) {
                    throw new IndexOutOfBoundsException("Chain member %d not present in impact matrix");
                }
            }
            this.chainMembers = chainMembers;
        }
        
        this.memberCount = this.chainMembers.size();
    }
    
    public int lastVariableIndex() {
        return chainMembers.get(memberCount-1);
    }
    
    public String lastVariableName() {
        return matrix.getName(chainMembers.get(memberCount-1));
    }
    
    public boolean chainEndsToIndex(int index) {
        if(memberCount == 0) return false;
        return index == chainMembers.get(memberCount-1);
    }
    
    /**
     * Calculates the impact contribution of the <u>last</u> variable 
     * in the chain through the impact chain.
     * The direct impact of the last variable can be and usually is greater
     * than the indirect impact through the chain.
     * TODO explain impact contribution
     * @return Impact contribution of the last variable of the chain through this particular chain
     */
    public double chainedImpact() {
        return chainedImpact(chainMembers);
    }
    
    /**
     * Calculates the impact contribution of the <u>last</u> variable in the chain
     * through the impact chain.
     * @param chain
     * @return
     */
    private double chainedImpact(List<Integer> chain)  {
        if(chain == null) { throw new NullPointerException("chain argument is null"); }
        if(chain.isEmpty()) return 1;
        if(chain.size()==1) return 1;
        return (matrix.getImpact(chain.get(0),chain.get(1))/matrix.getMaxImpact()) * chainedImpact(chain.subList(1, chain.size()));
    }
    
    public Set<ImpactChain> continuedByOneIntermediary() {
        
        // if(chainMembers.size() < 2) throw new IllegalStateException("This chain has less than 2 variables");
        if(chainMembers.size() < 2) {
            return continuedByOne();
        }
        
        Set<ImpactChain> continued = new TreeSet<>();
        Set<Integer> notIncluded = notInThisChain();
        for(Integer i : notIncluded) {
            List cm = new LinkedList(chainMembers);
            cm.add(cm.size()-1, i);
            ImpactChain c = new ImpactChain(this.matrix, cm);
            continued.add(c);
        }
        
        return continued;
    }
    
    public Set<ImpactChain> highImpactChainsIM(double treshold) {
        if(treshold <=0 || treshold >1 ) throw new IllegalArgumentException("impactTreshold should be in range ]0..1]");
        
        Set<ImpactChain> chains = new TreeSet<>();
        
        if(this.chainedImpact() >= treshold) { 
            chains.add(this);
            Set<ImpactChain> immediateExpansions = continuedByOneIntermediary();
            for(ImpactChain ic : immediateExpansions) {
                chains.addAll(ic.highImpactChains(treshold));
            }
        }
        
        return chains;        
    }
    
    
    private Set<ImpactChain> continuedByOne()  {
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
    
    /**
     * Generates all possible impact chains that can be expanded from
     * this impact chain using the variables in the matrix of this impact chain
     * @return All possible impact chains expanded from this impact chain 
     */
    public Set<ImpactChain> allExpandedChains()  {
        Set<ImpactChain> allChains = new TreeSet<>();
        allChains.add(this);
        Set<ImpactChain> immediateExpansions = continuedByOne();
        allChains.addAll(immediateExpansions);
        for(ImpactChain ic : immediateExpansions) {
            allChains.addAll(ic.allExpandedChains());
        }
        
        return allChains;
    }
    
    /**
     * Generates all impact chains expanded from this impact chain
     * that are also high-impact (having higher <code>chainedImpact</code> 
     * than <code>impactTreshold</code>).
     * @param impactTreshold The minimum impact a chain should have to be included in the returned chain
     * @return All impact chains expanded from this chain that have higher impact than treshold.
     */
    public Set<ImpactChain> highImpactChains(double impactTreshold)  {
        if(impactTreshold <0 || impactTreshold >1 ) throw new IllegalArgumentException("impactTreshold should be in range [0..1]");
        
        Set<ImpactChain> chains = new TreeSet<>();
        
        if(this.chainedImpact() >= impactTreshold) { 
            chains.add(this);
            Set<ImpactChain> immediateExpansions = continuedByOne();
            for(ImpactChain ic : immediateExpansions) {
                chains.addAll(ic.highImpactChains(impactTreshold));
            }
        }
        
        return chains;
    }

    /**
     * Returns the variable indices in <code>matrix</code> 
     * that are not present in this impact chain
     * @return Variable indices of <code>matrix</code> not included in this chain
     */
    private Set<Integer> notInThisChain() {
        Set<Integer> notInThisChain = new TreeSet<>();
        for(int i = 1; i <= matrix.getVarCount() ; i++ ) {
            if(! chainMembers.contains(i)) {
                notInThisChain.add(i);
            }
        }
        return notInThisChain;
    }
    
    @Override
    public String toString() {
        String s = String.format("  %+2.2f : ", chainedImpact());
        
        for(Integer i : chainMembers) {
            s += matrix.getName(i);
            if( ! i.equals(chainMembers.get(chainMembers.size()-1))) { s += " -> "; }
        }
        
        return s;
    }
    
   
    /**
     * Compares two <code>ImpactChain</code>notInThisChain.
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

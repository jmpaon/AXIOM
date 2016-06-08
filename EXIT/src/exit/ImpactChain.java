/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exit;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.List;
import java.util.LinkedList;


/**
 * Instances of this class represent chains of variables in a cross-impact matrix.
 * The indices of variables are stored in <code>chainMembers</code>.
 * <code>ImpactChain</code> consists of 
 * <i>impactor</i> variable, <i>intermediary variables</i> and <i>impacted</i> variable.
 * The <code>chainedImpact</code> of the chain is the impact of <i>impactor</i>
 * on the <i>impacted</i> variable, through the chain of intermediary variables
 * between <i>impactor</i> and <i>impacted</i>. 
 * In a direct impact, there are no intermediary variables.
 * @author jmpaon
 */
public class ImpactChain implements Comparable<ImpactChain> {
    
    public final CrossImpactMatrix matrix;
    public final List<Integer> chainMembers;
    public final int memberCount;
    
    
    /**
     * @param matrix The cross-impact matrix from whose variables 
     * this <code>ImpactChain</code> is constructed of
     * @param chainMembers The indices of the variables in the matrix.
     * All indices must be present in <b>matrix</b>.
     */
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
    
    public ImpactChain(CrossImpactMatrix matrix, int... chainMembers) {
        if(matrix == null) throw new NullPointerException("matrix is null");
        this.matrix = matrix;
        if(chainMembers == null || chainMembers.length == 0) {
            this.chainMembers = new LinkedList();
        } else {
            // Test chainMembers for duplicates
            List<Integer> list = new LinkedList<>();
            for(int i : chainMembers) {list.add(i);}
            Set<Integer> woDuplicates = new TreeSet<>(list);
            if(woDuplicates.size() != list.size()) { throw new IllegalArgumentException("duplicate items in chainMembers"); }
            
            // Test that indices in chainMembers are present in matrix of this impactChain
            for(Integer i : chainMembers) {
                if(i < 0 || i > matrix.getVarCount()) {
                    throw new IndexOutOfBoundsException(String.format("Chain member %d not present in impact matrix", i));
                }
            }
            this.chainMembers = list;
        }
        this.memberCount = this.chainMembers.size();
    }
    
    
    /**
     * @return The index of the last (impacted) variable in the chain
     */
    public int impactedIndex() {
        return chainMembers.get(memberCount-1);
    }
    
    /**
     * @return The index of the first (impactor) variable in the chain
     */
    public int impactorIndex() {
        return chainMembers.get(0);
    }
    
    
    /**
     * @return The name of the last (impacted) variable in the chain
     */
    public String impactedName() {
        return matrix.getName(chainMembers.get(memberCount-1));
    }
    
    
    /**
     * @return The name of the first (impactor) variable in the chain
     */
    public String impactorName() {
        return matrix.getName(chainMembers.get(0));
    }
    
    
    /**
     * Returns the impact of the first variable of the chain (impactor) 
     * on the last variable of the chain (impacted)
     * through the entire chain.
     * If chain has only one variable, <code>chainedImpact</code> is 1.
     * If chain has two variables, it represents a direct impact from
     * first variable to the last (second) variable and the impact is equal
     * to the direct impact expressed in the <code>matrix</code>.
     * For longer chains, the chain represents impact of <i>impactor</i> on <i>impacted</i>
     * through all the intermediary variables.
     * @return The effect of first variable on the last variable, through the intermediary variables in the chain
     */
    public double chainedImpact() {
        return chainedImpact(chainMembers);
    }
    
    
    /**
     * Calculates the impact of the impactor on the impacted through the 
     * intermediary variables in <code>chain</code>.
     * @param chain List containing indices of the variables in the chain whose impact is calculated
     * @return The impact of first variable in the chain on the last variable of the chain trough the chain
     * @see ImpactChain#chainedImpact()
     */
    private double chainedImpact(List<Integer> chain)  {
        if(chain == null) { throw new NullPointerException("chain argument is null"); }
        if(chain.isEmpty()) return 1;
        if(chain.size()==1) return 1;
        return (matrix.getImpact(chain.get(0),chain.get(1))/matrix.getMaxImpact()) * chainedImpact(chain.subList(1, chain.size()));
    }
    
    
    /**
     * Returns a <code>Set</code> of <code>ImpactChain</code>s,
     * which are one variable longer than this chain;
     * the chains are continued by variables that are not present in this chain
     * but are present in the matrix.
     * There will be as many continued chains in the returned set as there
     * are variables in the matrix that are not yet present in the chain.
     * @return Set of <code>ImpactChain</code>s.
     */
    Set<ImpactChain> continuedByOne()  {
        Set<ImpactChain> continued = new TreeSet<>();
        Set<Integer> notIncluded = expandableBy();
        
        for(Integer i : notIncluded) {
            List cm = new LinkedList(chainMembers);
            cm.add(i);
            ImpactChain c = new ImpactChain(this.matrix, cm);
            continued.add(c);
        }
        
        return continued;
        
    }
    
    
    /**
     * Returns a <code>Set</code> of <code>ImpactChain</code>s,
     * that are one variable longer than this chain
     * and where the first and last variables are the same as in this chain.
     * The method is used for generating the <code>ImpactChain</code>s
     * that represents the different direct and indirect impacts between
     * the impactor variable and impacted variable in this chain.
     * The new variables are introduced to the second to last place of the chain,
     * before the impacted variable.
     * @return <code>Set</code> of <code>ImpactChain</code>s
     * that have been expanded to be longer than this chain by one variable.
     */
    Set<ImpactChain> continuedByOneIntermediary() {
        
        if(chainMembers.size() < 2) {
            return continuedByOne();
        }
        
        Set<ImpactChain> continued = new TreeSet<>();
        Set<Integer> notIncluded = expandableBy();
        for(Integer i : notIncluded) {
            List cm = new LinkedList(chainMembers);
            cm.add(cm.size()-1, i);
            ImpactChain c = new ImpactChain(this.matrix, cm);
            continued.add(c);
        }
        
        return continued;
    }

 
    /**
     * Generates all impact chains expanded from this impact chain
     * that are also high-impact 
     * (having higher or equal <code>chainedImpact</code> 
     * than <code>impactTreshold</code>).
     * This method generates chains by adding variables to the end of the chain,
     * as impacted variable.
     * @param impactTreshold The minimum impact a chain should have to be included in the returned chain;
     * must be greater than 0 and smaller than 1
     * @return All impact chains expanded from this chain that have higher <code>chainedImpact</code> than treshold.
     * @see ImpactChain#highImpactChainsIntermediary(double) 
     */
    public Set<ImpactChain> highImpactChains(double impactTreshold)  {
        if(impactTreshold <=0 || impactTreshold >=1) throw new IllegalArgumentException("impactTreshold should be in range ]0..1[");
        
        Set<ImpactChain> chains = new TreeSet<>();
        
        if(Math.abs(this.chainedImpact()) >= impactTreshold) { 
            chains.add(this);
            
            Set<ImpactChain> immediateExpansions = this.continuedByOne();
            for(ImpactChain ic : immediateExpansions) {
                chains.addAll(ic.highImpactChains(impactTreshold));
            }
        }
        
        return chains;
    }    
    
    
    /**
     * Returns a <code>Set</code> of <code>ImpactChain</code>s
     * that are high impact (having an impact value ({@see ImpactChain#chainedImpact})
     * higher than <b>impactTreshold</b>). 
     * The chains are generated by continuing chains
     * by adding variables missing from them 
     * to the second-to-last place in the chain, 
     * so the impacted variable stays the same in the generated chains.
     * @param impactTreshold <code>ImpactChain</code>s that have impact 
     * equal to or higher than impactTreshold will be included in the returned <code>Set</code>.
     * @return <code>Set</code> of <code>ImpactChain</code>s 
     * that have a higher impact than <b>impactTreshold</b>
     * @deprecated This candidate chain generation strategy might
     * result in some significant impact chains not being generated.
     */
    @Deprecated
    public Set<ImpactChain> highImpactChainsIntermediary(double impactTreshold) {
        if(impactTreshold <=0 || impactTreshold >=1 ) throw new IllegalArgumentException("impactTreshold should be in range ]0..1[");
        
        Set<ImpactChain> chains = new TreeSet<>();
        
        if(Math.abs(this.chainedImpact()) >= impactTreshold) { 
            chains.add(this);
            Set<ImpactChain> immediateExpansions = continuedByOneIntermediary();
            for(ImpactChain ic : immediateExpansions) {
                chains.addAll(ic.highImpactChains(impactTreshold));
            }
        }
        return chains;        
    }
    
    

    /**
     * @return <b>true</b> if this chain can be expanded, false otherwise
     */
    public boolean hasExpansion() {
        return !expandableBy().isEmpty();
    }
    
    
    /**
     * Returns a <code>Set</code> containing 
     * the variable indices in <code>matrix</code> 
     * that are not present in this <code>ImpactChain</code>
     * @return <code>Set</code> with variable indices not included in this <code>ImpactChain</code>
     */
    private Set<Integer> expandableBy() {
        Set<Integer> notInThisChain = new TreeSet<>();
        for(int i = 1; i <= matrix.getVarCount() ; i++ ) {
            if(! chainMembers.contains(i)) {
                notInThisChain.add(i);
            }
        }
        return notInThisChain;
    }
    
    
    /**
     * @return String representation of the impact chain, using long variable names.
     */
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
     * @return String representation of the impact chain, using short variable names.
     */
    public String toStringShort() {
        String s = String.format(" %+2.2f : ", chainedImpact());
        
        for(Integer i : chainMembers) {
            s += matrix.getNameShort(i);
            if( ! i.equals(chainMembers.get(chainMembers.size()-1))) { s += " -> "; }
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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exit;

import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jmpaon
 */
public class ImpactComparator implements Comparator<ImpactChain> {
    
    @Override
    public int compare(ImpactChain ic1, ImpactChain ic2) {
        
        int result = Double.compare(ic2.chainedImpact(), ic1.chainedImpact());
        
        if(result == 0) {
            result = ic1.compareTo(ic2);
        }
        
        return result;
    }
    
}


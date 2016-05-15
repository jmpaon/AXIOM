/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package axiom;

import axiom.model.*;
import axiom.probabilityAdjusters.*;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author juha
 */
public class AXIOM {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        try {
            ProbabilityAdjusterFactory paf = new ProbabilityAdjusterFactory();
            NameProbabilityAdjuster adj = paf.createDefaultNameProbabilityAdjuster();
            
            Probability p = new Probability(0.5);
            p = adj.adjustedProbability(p, "+5");
            System.out.println(p);
            p = adj.adjustedProbability(p, "-5");
            System.out.println(p);
            
        } catch (ProbabilityAdjustmentException ex) {
            Logger.getLogger(AXIOM.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ArgumentException ex) {
            Logger.getLogger(AXIOM.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
    
}

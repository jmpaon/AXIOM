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
import static java.lang.System.out;
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
            NameProbabilityAdjuster adj = new ProbabilityAdjusterFactory().createDefaultNameProbabilityAdjuster();
            Model m = new Model("Testmodel", adj);
            
            System.out.println(m.optionCount());
            m.add.statement("B", "B-description", false, 1);
            m.add.statement("C", "C-description", false, 1);
            m.add.statement("A", "A-description", false, 1);
            m.add.statement("D", "A-description", false, 2);
            m.add.statement("E", "A-description", false, 2);
            m.add.statement("F", "A-description", false, 2);
            m.add.option("B", "2", 0.7);
            m.add.option("B", "1", 0.3);
            m.add.option("A", "2", 0.2);
            m.add.option("A", "1", 0.3);
            m.add.option("A", "3", 0.5);
            m.add.option("C", "1", 0.1);
            m.add.option("C", "3", 0.2);
            m.add.option("C", "2", 0.1);
            m.add.option("C", "4", 0.6);
            m.add.option("D", "1", 0.1);
            m.add.option("D", "2", 0.9);
            m.add.option("E", "1", 0.2);
            m.add.option("E", "2", 0.7);
            m.add.option("E", "3", 0.1);
            m.add.option("F", "1", 0.45);
            m.add.option("F", "2", 0.55);
            System.out.println(m.optionCount());
            System.out.println(m.getOption(1));
            Configuration c = m.evaluate();
            System.out.println(c.toStringAsOptionValues());
            for(int i = 1; i <= m.optionCount(); i++) {
                Option o = m.getOption(i);
                System.out.print(i + " " + o + "  ");
                System.out.println(c.isOptionTrue(o));
            }
            System.out.println(c);
            
            //System.out.println(m.statementsByTimestep().toString());
            //Option o = m.getOptionAtIndex(1);
            //System.out.println(m.getModelStates());
                    
                    
            
            
        } catch (ProbabilityAdjustmentException ex) {
            Logger.getLogger(AXIOM.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(AXIOM.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
    
    public static void testAdjustment() {
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

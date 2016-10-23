/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package axiom;

import axiom.model.*;
import axiom.probabilityAdjusters.*;
import axiom.reader.Command;
import axiom.reader.Reader;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.lang.System.out;
import java.security.cert.PKIXRevocationChecker;
import java.util.LinkedList;
import java.util.List;
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
            
            m.add.statement("A", "A-description", false, 1);
            m.add.statement("B", "B-description", false, 1);
            m.add.statement("C", "C-description", false, 1);
            m.add.statement("D", "D-description", false, 2);
            m.add.statement("E", "E-description", false, 2);
            m.add.statement("F", "F-description", false, 2);

            m.add.option("A", "2", 0.2);
            m.add.option("A", "1", 0.3);
            m.add.option("A", "3", 0.5);            
            m.add.option("B", "2", 0.7);
            m.add.option("B", "1", 0.3);
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
            m.add.impact("A", "1", "B", "1", "+2");
            m.add.impact("B", "2", "A", "1", "+3");
            m.add.impact("C", "1", "A", "1", "+5");
            m.add.impact("D", "2", "A", "2", "-1");
            m.add.impact("E", "1", "C", "1", "-4");
            m.add.impact("F", "2", "C", "2", "-2");
            
            String inputfilename = System.getProperty("user.dir") + "\\" + "input2.txt";
            Reader r = new Reader("input2.txt");
            Model m2 = r.createAXIOMmodelFromInput();
            System.out.println(m2);
            
            //IterationSet is = new IterationSet(m2, 50);
            //System.out.println(is);

            //TestInputGenerator tig = new TestInputGenerator();
            //System.out.println(tig.generateInput(100));
            
            
            
            
            //IterationSet is = new IterationSet(m, 30000);
            //System.out.println(is);

            //Configuration c = m.evaluate();
            //System.out.println(c.toStringAsOptionValues());
            //System.out.println(c);
            
            
            
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
        } catch (Exception ex) {
            Logger.getLogger(AXIOM.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }
    
    
}

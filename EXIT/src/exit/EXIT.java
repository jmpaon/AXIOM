/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exit;

import java.io.IOException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author jmpaon
 */
public class EXIT {

    /**
     * @param args the command line arguments
     * @throws exit.ModelBuildingException
     */
    public static void main(String[] args) throws ModelBuildingException {
        
        try {
            
            InputFileReader ifr = new InputFileReader();
            CrossImpactMatrix matrix;            
            matrix = ifr.readInputFile("src/exit/inputfile4.csv");
            System.out.println(matrix.toString());

            
            ImpactChain ic = new ImpactChain(matrix, null);
            // System.out.println(ic.toString());
            // printSet(ic.notInThisChain());
            Set<ImpactChain> sic = ic.allExpandedChains();
            for(ImpactChain ic2 : sic)
                for(ImpactChain ic3 : ic2.continuedByOneVariable()) {
                    System.out.println(ic3.toString());
                    System.out.println(ic3.chainedImpact());
                }
                    
            
            
            
            
            
            
        } catch (IOException | EXITException ex) {
            Logger.getLogger(EXIT.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public static void printSet(Set<Integer> s) {
        System.out.println(s.toString());
    }
    
}
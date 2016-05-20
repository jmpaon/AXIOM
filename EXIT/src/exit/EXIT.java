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
            matrix = ifr.readInputFile("src/exit/inputfile8.csv");
            System.out.println(matrix.toString());

            
            ImpactChain ic = new ImpactChain(matrix, null);

            Set<ImpactChain> sic = ic.allExpandedChains();
            
            
            int counter=0;
            for(ImpactChain i : sic) {
                System.out.println(i.toString());
                counter++;
            }
            System.out.println(counter);
//            for(ImpactChain ic2 : sic)
//                for(ImpactChain ic3 : ic2.continuedByOneVariable()) {
//                    System.out.println(ic3.toString());
//                }
                    
            
            
            
            
            
            
        } catch (IOException | EXITException ex) {
            Logger.getLogger(EXIT.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public static void printSet(Set<Integer> s) {
        System.out.println(s.toString());
    }
    
}
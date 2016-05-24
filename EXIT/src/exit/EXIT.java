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
import java.util.List;
import java.util.LinkedList;

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
            matrix = ifr.readInputFile("src/exit/inputfile35L.csv");
            System.out.println(matrix.toString());
            
            ImpactChain ic = new ImpactChain(matrix, null);
            List<Integer> list = new LinkedList<>();
            list.add(4); list.add(6); list.add(3);
            ImpactChain ict = new ImpactChain(matrix, list);


            List<ImpactChain> sic = matrix.indirectImpacts(0.1);
            
            
            int counter=0;
            for(ImpactChain i : sic) {
                System.out.println(i.toString());
                counter++;
            }
            System.out.println(counter + " impact chains printed");
            
            
            
        } catch (IOException | EXITException ex) {
            Logger.getLogger(EXIT.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public static void printSet(Set<Integer> s) {
        System.out.println(s.toString());
    }
    
}
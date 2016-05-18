/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exit;

import java.io.IOException;
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
            matrix = ifr.readInputFile("src/exit/inputfile.csv");
            System.out.println(matrix.toString());            
            
            
            
        } catch (IOException | EXITException ex) {
            Logger.getLogger(EXIT.class.getName()).log(Level.SEVERE, null, ex);
        }
        
            

        
        
        
    }
}
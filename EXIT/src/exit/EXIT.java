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
public class EXIT_cross_impact_analysis {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws ModelBuildingException {
        
        InputFileReader ifr = new InputFileReader();
        CrossImpactMatrix matrix;
        
        try {
            matrix = ifr.readInputFile("src/exit_cross_impact_analysis/inputfile.csv");
            System.out.println(matrix.toString());
            
            
            
        } catch (IOException ex) {
            Logger.getLogger(EXIT_cross_impact_analysis.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(EXIT_cross_impact_analysis.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        
    }
}
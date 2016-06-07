/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exit;

import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;



/**
 *
 * @author jmpaon
 */
public class EXIT {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, EXITexception {

        /* Fast calculation procedure */
        // fast_exit_analysis(args);
        
        /* Normal calculation procedure */
        standard_exit_analysis(args);
        
        /* JL-procedure */
        // JL_exit();
    
    }

    /**
     * The standard EXIT analysis :
     * get direct impact matrix in,
     * output the direct+indirect impact matrix.
     * @param args 
     */
    public static void standard_exit_analysis(String[] args) {
        try {
            
            Reporter.requiredReportingLevel = 0;
            String[] arggs = {"src/exit/inputfile12.csv", "-max", "5", "-t", "0.10"};
            EXITarguments arguments = new EXITarguments(arggs);
            
            InputFileReader ifr = new InputFileReader();
            CrossImpactMatrix inputMatrix = ifr.readInputFile(arguments);
            System.out.println(arguments);
            
            System.out.println("Impact matrix describing direct impacts between variables:");
            System.out.println(inputMatrix.toString());
            
            CrossImpactMatrix resultMatrix = inputMatrix.summedImpactMatrix(arguments.treshold);
            System.out.println("\nResult impact matrix with summed direct and indirect impacts between variables, scaled to have max of 5:");
            System.out.println(resultMatrix.scaleByMax(inputMatrix.getMaxImpact()).toString());
            
            System.out.println(resultMatrix.reportDrivingVariables());
         
        } catch(EXITexception eex) {
            System.out.println(eex.getMessage());
        } catch(Exception ex) {
            Logger.getLogger(EXIT.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void fast_exit_analysis(String[] args) {
        try {
            
            Reporter.requiredReportingLevel = 0;
            String[] arggs = {"src/exit/inputfile12.csv", "-max", "5", "-t", "0.10"};
            EXITarguments arguments = new EXITarguments(arggs);
            
            InputFileReader ifr = new InputFileReader();
            CrossImpactMatrix inputMatrix = ifr.readInputFile(arguments);
            System.out.println(arguments);
            
            System.out.println("Impact matrix describing direct impacts between variables:");
            System.out.println(inputMatrix.toString());
            
            CrossImpactMatrix resultMatrix = inputMatrix.summedImpactMatrixFast(arguments.treshold);
            System.out.println("\nResult impact matrix with summed direct and indirect impacts between variables, scaled to have max of 5:");
            System.out.println(resultMatrix.scaleByMax(inputMatrix.getMaxImpact()).toString());
            
            System.out.println(resultMatrix.reportDrivingVariables());
         
        } catch(EXITexception eex) {
            System.out.println(eex.getMessage());
        } catch(Exception ex) {
            Logger.getLogger(EXIT.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }
    

    
    public static void JL_exit() {
        try {
            InputFileReader ifr = new InputFileReader();
            String[] args = {"src/exit/test5.csv", "-max", "5"};
            EXITarguments arguments = new EXITarguments(args);            
            CrossImpactMatrix matrix = ifr.readInputFile(arguments);
            
            System.out.println("\nImpact matrix describing direct impacts between variables:");
            System.out.println(matrix.toString());
            
            CrossImpactMatrix result = matrix.summedImpactMatrix(0.000001);
            
            System.out.println("\nImpact matrix describing total direct and indirect impacts between variables:");
            System.out.println(result.toString());
            System.out.println("\nImpact matrix scaled to be similar in terms of impact sizes as the original matrix:");
            System.out.println(result.scaleByMax(matrix.getMaxImpact()));

            for(int iter = 1; iter <= 15; iter++) {
                result = result.summedImpactMatrix(0.0001);
                System.out.printf("Iteration %d:%n", iter);
                System.out.println(result.scaleByMax(result.getMaxImpact()));
            }
            
            
        } catch (IOException | EXITexception ex) {
            Logger.getLogger(EXIT.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}
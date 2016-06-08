/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exit;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
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
     */
    public static void main(String[] args) throws IOException, EXITexception {
        
        String[] arggs = {"src/exit/eltran1.csv", "-max", "5", "-t", "0.00000000000001"};
        
        /* Fast calculation procedure */
        standard_exit_analysis(arggs);
        
        /* Normal calculation procedure */
        // standard_exit_analysis(arggs);
        
        /* JL-procedure */
        // JL_exit();
        
        /* test */
        //test_features(args);
    
    }
    
    public static void test_features(String[] args) {
        try {
            Reporter.requiredReportingLevel = 0;
            String[] arggs = {"src/exit/eltran1.csv", "-max", "5", "-t", "0.0010000"};
            EXITarguments arguments = new EXITarguments(arggs);
            
            InputFileReader ifr = new InputFileReader();
            CrossImpactMatrix inputMatrix = ifr.readInputFile(arguments);
            
            ImpactChain ic = new ImpactChain(inputMatrix, Arrays.asList(4,1));
            Set<ImpactChain> s1 = ic.highImpactChains(arguments.treshold);
            Set<ImpactChain> s2 = ic.highImpactChains(arguments.treshold);
            
            System.out.printf("%d %d%n", s1.size(), s2.size());
            
            
        } catch (Exception ex) {
            Logger.getLogger(EXIT.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    
    public static void standard_exit_analysis(String[] args) {
        try {
            
            Reporter.requiredReportingLevel = 0;
            EXITarguments arguments = new EXITarguments(args);
            
            InputFileReader ifr = new InputFileReader();
            CrossImpactMatrix inputMatrix = ifr.readInputFile(arguments);
            System.out.println(arguments);
            
            System.out.println("Impact matrix describing direct impacts between variables:");
            System.out.println(inputMatrix.toString());
            
            CrossImpactMatrix resultMatrix = inputMatrix.summedImpactMatrix(arguments.treshold);
            
            /* Show non-scaled result matrix */
            System.out.println("\nResult impact matrix with summed direct and indirect impacts between variables, not scaled:");
            System.out.println(resultMatrix);            
            
            /* Show result matrix scaled to maxImpact of input matrix */
            //System.out.println("\nResult impact matrix with summed direct and indirect impacts between variables, scaled to maxImpact of input matrix:");
            //System.out.println(resultMatrix.scaleByMax(inputMatrix.getMaxImpact()).toString());
            
         
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
            
            CrossImpactMatrix result = matrix.summedImpactMatrix_slow(0.000001);
            
            System.out.println("\nImpact matrix describing total direct and indirect impacts between variables:");
            System.out.println(result.toString());
            System.out.println("\nImpact matrix scaled to be similar in terms of impact sizes as the original matrix:");
            System.out.println(result.scaleByMax(matrix.getMaxImpact()));

            for(int iter = 1; iter <= 15; iter++) {
                result = result.summedImpactMatrix_slow(0.0001);
                System.out.printf("Iteration %d:%n", iter);
                System.out.println(result.scaleByMax(result.getMaxImpact()));
            }
            
            
        } catch (IOException | EXITexception ex) {
            Logger.getLogger(EXIT.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}
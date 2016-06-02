/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exit;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.List;
import java.util.LinkedList;
import java.math.BigInteger;
import java.util.ArrayList;

/**
 *
 * @author jmpaon
 */
public class EXIT {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, EXITException {
        
        args[0] = "src/exit/test5.csv";
        standard_exit_analysis(args);
        
//        Reporter.requiredReportingLevel = 0;
//        String[] names = {"purr","kurr","surr"};
//        double[] imps  = {0,1,4,  5,0,4,  -2,4,0};
//        CrossImpactMatrix m = new CrossImpactMatrix(5, 3, true, names, imps);
//        System.out.println(m.toString());
//        CrossImpactMatrix m2 = m.summedImpactMatrix(0.001).scaleByMax(5);
//        System.out.println(m2.toString());
    
    }
    
    /**
     * The standard EXIT analysis :
     * get direct impact matrix in,
     * output the direct+indirect impact matrix.
     * @param args 
     */
    public static void standard_exit_analysis(String[] args) {
        try {
            String[] arggs = {"src/exit/test5.csv", "-max", "5", "-int"};
            EXITarguments arguments = new EXITarguments(arggs);
            
            InputFileReader ifr = new InputFileReader();
            CrossImpactMatrix inputMatrix = ifr.readInputFile(arguments);
            System.out.println(arguments);
            
            System.out.println("Impact matrix describing direct impacts between variables:");
            System.out.println(inputMatrix.toString());
            
            CrossImpactMatrix resultMatrix = inputMatrix.summedImpactMatrix(arguments.treshold);
            System.out.println("\nResult impact matrix with summed direct and indirect impacts between variables, scaled to have max of 5:");
            System.out.println(resultMatrix.scaleByMax(inputMatrix.getMaxImpact()).toString());
            
        } catch(Exception ex) {
            Logger.getLogger(EXIT.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    public static void testWOargs() {
        
        try {
            
            Reporter.requiredReportingLevel = 10;
            InputFileReader ifr = new InputFileReader();
            String[] args = {"src/exit/test5.csv", "-max", "5"};
            EXITarguments arguments = new EXITarguments(args);
            CrossImpactMatrix matrix = ifr.readInputFile(arguments);
            
            System.out.println("\nImpact matrix describing direct impacts between variables:");
            System.out.println(matrix.toString());
            
            CrossImpactMatrix result = matrix.summedImpactMatrix(0.000001);
            
            System.out.println("\nImpact matrix describing total direct and indirect impacts between variables:");
            System.out.println(result.toString());
            System.out.println(result.scaleByMax(matrix.getMaxImpact()));
            
          
            
        } catch (IOException | EXITException ex) {
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
            
            
        } catch (IOException | EXITException ex) {
            Logger.getLogger(EXIT.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public static void testWargs(String[] args) {
        try {
            
            EXITarguments arguments = new EXITarguments(args);
            System.out.println(arguments.toString());            
            
            InputFileReader ifr = new InputFileReader();
            CrossImpactMatrix matrix = ifr.readInputFile(arguments);

            List<ImpactChain> sic = matrix.indirectImpacts(Integer.valueOf(arguments.impactOf), Integer.valueOf(arguments.impactOn), 0.01);
            
            System.out.println(matrix.toString());
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
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exit;

import com.sun.xml.internal.ws.util.StringUtils;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
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
        
        String[] commandLineArguments = {"src/exit/eltran1.csv", "-max", "5", "-t", "0.0000001"};
        
        /* Normal calculation procedure */
        standard_exit_analysis(commandLineArguments);
        
        /* JL-procedure */
        // JL_exit();
        
        /* test */
        //test_features(args);
    
    }
    
    public static void standard_exit_analysis(String[] args) {
        try {
            
            Reporter.requiredReportingLevel = 0;
            EXITarguments arguments = new EXITarguments(args);
            
            InputFileReader ifr = new InputFileReader();
            CrossImpactMatrix inputMatrix = ifr.readInputFile(arguments);
            
            System.out.printf("Impact matrix describing direct impacts between variables read from %s%n", arguments.inputFilename);
            System.out.println(inputMatrix.toString());
            
            PrintStream output;
            
            if(arguments.outputFilename == null) {
                output = System.out;
            } else {
                output = new PrintStream(arguments.outputFilename);
            }
            
            if (arguments.impactOf != null || arguments.impactOn != null) {
                
                Integer impactOfIndex = isInteger(arguments.impactOf) ? Integer.parseInt(arguments.impactOf) : null;
                Integer impactOnIndex = isInteger(arguments.impactOn) ? Integer.parseInt(arguments.impactOn) : null;
                
                
                
                if (arguments.impactOf != null && arguments.impactOn != null) {
                    
                    inputMatrix.indirectImpacts(arguments.impactOf, arguments.impactOn, 0)
                } 

                else if (arguments.impactOf != null && arguments.impactOn == null) {

                } 

                else if (arguments.impactOf == null && arguments.impactOn != null) {

                }                 
                
                
                
                
            } else {
                Timer timer = new Timer();
                CrossImpactMatrix resultMatrix = inputMatrix.summedImpactMatrix(arguments.treshold);
                timer.stopTime("Process duration: ");
                output.println("\nResult impact matrix with summed direct and indirect impacts between variables, not scaled:");
                output.println(resultMatrix);                
            }
            
            


            
            
            

        }catch(EXITargumentException ex) {
            
        } catch(EXITexception ex) {
            System.out.println(ex.getMessage());
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
            
            CrossImpactMatrix result = matrix.summedImpactMatrix(0.005);
            
            System.out.println("\nImpact matrix describing total direct and indirect impacts between variables:");
            System.out.println(result.toString());
            System.out.println("\nImpact matrix scaled to be similar in terms of impact sizes as the original matrix:");
            System.out.println(result.scaleByMax(matrix.getMaxImpact()));

            for(int iter = 1; iter <= 15; iter++) {
                result = result.summedImpactMatrix_slow(0.005);
                System.out.printf("Iteration %d:%n", iter);
                System.out.println(result.scaleByMax(result.getMaxImpact()));
            }
            
            
        } catch (IOException | EXITexception ex) {
            Logger.getLogger(EXIT.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }    
    
    
    public static void test_features(String[] args) {
        try {
            Reporter.requiredReportingLevel = 0;
            String[] arggs = {"src/exit/inputfile12.csv", "-max", "5", "-t", "0.0010000"};
            EXITarguments arguments = new EXITarguments(arggs);
            
            InputFileReader ifr = new InputFileReader();
            CrossImpactMatrix inputMatrix = ifr.readInputFile(arguments);
            
            ExperimentalChainMiner e = new ExperimentalChainMiner(inputMatrix);
            Timer timer = new Timer();
            
            timer.startTime();
            e.mineChains(0.25);
            timer.stopTime("Experimental strategy time: ");
            
            timer.startTime();
            System.out.println("Normal strategy: " + inputMatrix.indirectImpacts(null, null, 0.25).size() + " chains found");
            timer.stopTime("Normal strategy time: ");
            
            
            
        } catch (Exception ex) {
            Logger.getLogger(EXIT.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static boolean isInteger(String str) {  
        try {int d = Integer.parseInt(str);}  
        catch(NumberFormatException nfe){ return false;}  
        return true;  
    }    
    

}
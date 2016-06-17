/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exit;


import java.io.IOException;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
        
        String[] commandLineArguments = {"src/exit/inputfile5.csv", "-max", "5", "-t", "0.05"};
        
        /* Normal calculation procedure */
        // standard_exit_analysis(commandLineArguments);
        
        /* JL-procedure */
        //JL_exit(100);
        
        /* test */
        test_features(args);
    
    }
    
    public static void standard_exit_analysis(String[] args) {
        try {

            EXITarguments arguments = new EXITarguments(args);
            PrintStream output;
            
            if(arguments.outputFilename == null) {
                output = System.out;
            } else {
                output = new PrintStream(arguments.outputFilename);
            }
            
            output.println( arguments );
            Reporter.requiredReportingLevel = 0;
            Reporter.output = output;
            
            InputFileReader ifr = new InputFileReader();
            CrossImpactMatrix inputMatrix = ifr.readInputFile(arguments);
            
            
            output.println(inputMatrix.toString());
            

            
            if (arguments.impactOf != null || arguments.impactOn != null) 
            /* Print requested impact chains and their summed impacts */
            {
                
                Integer impactOfIndex = arguments.impactOf != null ? 
                        isInteger(arguments.impactOf) ? Integer.parseInt(arguments.impactOf) : inputMatrix.getIndex(arguments.impactOf)
                        : null;
                Integer impactOnIndex = arguments.impactOn != null ?
                        isInteger(arguments.impactOn) ? Integer.parseInt(arguments.impactOn) : inputMatrix.getIndex(arguments.impactOn)
                        : null;
                    
                List<ImpactChain> significantChains = inputMatrix.indirectImpacts(impactOfIndex, impactOnIndex, arguments.treshold);
                Map<List<String>, Double> chainsSummed = new HashMap<>();
                
                String impactChainDescription = impactOfIndex != null ?
                        impactOnIndex != null ? 
                            String.format("Impacts of %s on %s", inputMatrix.getName(impactOfIndex), inputMatrix.getName(impactOnIndex))
                            : String.format("Impacts of %s", inputMatrix.getName(impactOfIndex))
                        : String.format("Impacts on %s", inputMatrix.getName(impactOnIndex));
                
                        
                output.printf("%s with significant (%1.3f) impact:%n", impactChainDescription,  arguments.treshold );
                for (ImpactChain chain : significantChains) {
                    output.println( chain );
                    List<String> key = new LinkedList<>(Arrays.asList( chain.impactorName(), chain.impactedName() ));
                    if(chainsSummed.containsKey(key)) {
                        chainsSummed.put(key, chainsSummed.get(key) + chain.impact() );
                    } else {
                        chainsSummed.put(key, chain.impact());
                    }
                }
                
                for (Map.Entry<List<String>, Double> entry : chainsSummed.entrySet()) {
                    List<String> key = entry.getKey();
                    Double value = entry.getValue();
                    output.printf("Impact of %s on %s : %2.2f%n", key.get(0), key.get(1), value);
                }
                
                
            } else 
            /* Print full cross-impact matrix displaying direct and indirect impacts */
            {
                Timer timer = new Timer();
                CrossImpactMatrix resultMatrix = inputMatrix.summedImpactMatrix(arguments.treshold);
                timer.stopTime("Process duration: ");
                output.printf("%nResult impact matrix with summed direct and indirect impacts between variables, not scaled:%n");
                output.printf("%s%n", resultMatrix);
                output.printf("Result impact matrix with summed direct and indirect impacts between variables, scaled to %f:%n", inputMatrix.getMaxImpact());
                output.println(resultMatrix.scaleByMax(inputMatrix.getMaxImpact()));
            }
            
            


            
            
            

        }catch(EXITargumentException ex) {
            System.out.println("Argument error occurred: " + ex.getMessage());            
        } catch(EXITexception ex) {
            System.out.println("EXIT error occurred: " + ex.getMessage());
        } catch(Exception ex) {
            System.out.println("Error occurred: " + ex.getMessage());
            // Logger.getLogger(EXIT.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    

    
    public static void JL_exit(int iterations) {
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

            for(int iter = 1; iter <= iterations; iter++) {
                result = result.summedImpactMatrix(0.005);
                System.out.printf("%n%nIteration %d:%n", iter);
                System.out.println(result.scaleByMax(result.getMaxImpact()));
            }
            
            
        } catch (IOException | EXITexception ex) {
            Logger.getLogger(EXIT.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }    
    
    
    public static void test_features(String[] args) {
        try {
            Reporter.requiredReportingLevel = 0;
            String[] arggs = {"src/exit/eltran1.csv", "-max", "5", "-t", "0.0010000"};
            EXITarguments arguments = new EXITarguments(arggs);
            
            InputFileReader ifr = new InputFileReader();
            
            Plot2D plot = new Plot2D();
            plot.addPoint(10, 15, "Mem");
            plot.addPoint(10, 12, "Mau");
            plot.addPoint(5, 20, "Kur");
            System.out.println(plot.plot(30, 30));
            
            
//            CrossImpactMatrix inputMatrix = ifr.readInputFile(arguments);
//            CrossImpactMatrix impMatrix = inputMatrix.importanceMatrix();
//            CrossImpactMatrix resultMatrix = inputMatrix.summedImpactMatrix(0.001);
//            CrossImpactMatrix diffMatrix = resultMatrix.importanceMatrix().differenceMatrix(impMatrix);
//            CrossImpactMatrix diffMatrix2 = resultMatrix.differenceMatrix(inputMatrix).importanceMatrix();
//            
//            System.out.println("Importance difference matrix:");
//            System.out.println(diffMatrix);
//            
//            System.out.println("Importance difference matrix 2:");
//            System.out.println(diffMatrix2);
//                        
//            System.out.println(inputMatrix);
//            System.out.println(resultMatrix);
//            System.out.println("Difference matrix:");
//            System.out.println(resultMatrix.differenceMatrix(inputMatrix));
//            
//            System.out.println(inputMatrix.driverDriven());
//            
//            System.out.println(inputMatrix.driverDrivenReport());
            
            
            
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
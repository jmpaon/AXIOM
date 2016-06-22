/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exit;


import java.io.FileNotFoundException;
import java.io.IOException;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
    public static void main(String[] args)  {
        
        /* String[] commandLineArguments = {"src/exit/eltran1.csv", "-extra",  "-max", "5", "-t", "0.005"}; */
        
        // 
        if(args.length < 1) {
            new EXITargumentException("").printUsage();
        }
        
        /* Normal calculation procedure */
        standard_exit_analysis(args);
        
        /* JL-procedure */
        //JL_exit(3500);
    
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
            
            /* output.println( arguments ); */
            Reporter.requiredReportingLevel = 0;
            Reporter.output = output;
            
            InputFileReader ifr = new InputFileReader();
            CrossImpactMatrix inputMatrix = ifr.readInputFile(arguments);
            
            output.println("Input matrix describing direct impacts:");
            output.println(inputMatrix.toString());
            if(arguments.extraReports) {
                output.println("Importance matrix derived from input matrix; max value 5:");
                output.println(inputMatrix.importanceMatrix().round(5));
                
                output.println("Input matrix driver-driven report");
                output.println(inputMatrix.scale(1).driverDriven().toString());                
                
            }
            

            
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
                
                output.printf("Result impact matrix with summed direct and indirect impacts between variables, scaled to %f:%n", inputMatrix.getMaxImpact());
                output.println(resultMatrix.scale(inputMatrix.getMaxImpact()));
                
                if(arguments.extraReports) {
                    output.println("Importance matrix derived from result matrix; max value 5:");
                    output.println(resultMatrix.importanceMatrix().round(5));
                }
                
                output.println("Difference matrix of result matrix and input matrix, both scaled to max value 5:");
                output.println(resultMatrix.scale(5).differenceMatrix(inputMatrix.scale(5)));
                
                if(arguments.extraReports) {
                    output.println("Difference matrix of result matrix and input matrix scaled to 5 and rounded:");
                    output.println(resultMatrix.scale(inputMatrix.getMaxImpact()).differenceMatrix(inputMatrix).round(5));                    
                
                    output.println("Result matrix driver-driven report");
                    output.println(resultMatrix.scale(1).driverDriven().toString());
                
                    // output.println(resultMatrix.reportDrivingVariables());
                }
                
            }
     
        }catch(FileNotFoundException ex) {
            System.out.printf("Input file not found: %s%n", ex.getMessage());
        }catch(IOException ex) {
            System.out.printf("Error reading input file: %s%n", ex.getMessage());
        }catch(EXITargumentException ex) {
            System.out.println("Argument error occurred: " + ex.getMessage());            
        } catch(EXITexception ex) {
            System.out.println("EXIT error occurred: " + ex.getMessage());
        } catch(Exception ex) {
            System.out.println("Error occurred: " + ex.getMessage());
            // Logger.getLogger(EXIT.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    

    
    static void JL_exit(int iterations) {
        try {
            InputFileReader ifr = new InputFileReader();
            String[] args = {"src/exit/eltran1.csv", "-max", "5"};
            EXITarguments arguments = new EXITarguments(args);            
            CrossImpactMatrix matrix = ifr.readInputFile(arguments);
            
            System.out.println("\nImpact matrix describing direct impacts between variables:");
            System.out.println(matrix.toString());
            
            CrossImpactMatrix result = matrix.summedImpactMatrix(0.005);
            
            System.out.println("\nImpact matrix describing total direct and indirect impacts between variables:");
            System.out.println(result.toString());
            System.out.println("\nImpact matrix scaled to be similar in terms of impact sizes as the original matrix:");
            System.out.println(result.scale(matrix.getMaxImpact()));
            
            
            CrossImpactMatrix co1=null;
            int sameIt = -1;
            
            
            for(int iter = 1; iter <= iterations; iter++) {
                co1 = result.clone();
                result = result.summedImpactMatrix(0.005);
                if(sameIt==-1) if(result.areValuesApproximatelySame(co1, 0.005)) sameIt = iter;
                System.out.printf("%n%nIteration %d:%n", iter);
                System.out.println(result.scale(result.getMaxImpact()));
            }
            
            System.out.printf("Importance matrix for iteration %d%n", iterations);
            System.out.println(result.importanceMatrix());
            System.out.println(result.importanceMatrix().round(5));
            System.out.println("Iteration results in a noticeably different matrix until iteration " + sameIt);
            
            
            
        } catch (IOException | EXITexception ex) {
            Logger.getLogger(EXIT.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }    
    
    
    private static void test_features(String[] args) {
        try {
            Reporter.requiredReportingLevel = 0;
            String[] arggs = {"src/exit/eltran1.csv", "-max", "5", "-t", "0.0010000"};
            EXITarguments arguments = new EXITarguments(arggs);
            
            InputFileReader ifr = new InputFileReader();
            
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
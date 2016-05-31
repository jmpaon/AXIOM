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
        
        // testWargs(args);
        testWOargs();
        // HugeNumber h = new HugeNumber(239085928);
        // System.out.println(h);
        
        
        
//        CrossImpactMatrix matrix = new InputFileReader().readInputFile("src/exit/inputfile6.csv");
//        ImpactChain ic = new ImpactChain(matrix, Arrays.asList(3,5,6));
//        System.out.println(ic.toString());
//        
//        for(Integer i : new ArrayList<Integer>(Arrays.asList(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16))) {
//            System.out.printf("%3d --> ", i);
//            System.out.println(matrix.approximateChainCount(i));
//        }
        
        
        // System.out.println(matrix.possibleChainCount(3));
//        ImpactChain ic = new ImpactChain(matrix, new LinkedList<>( Arrays.asList(1,2,3,4) ) );
//        Set<ImpactChain> set = ic.continuedByOneIntermediary();
//        for(ImpactChain i : set) System.out.println(i.toString());
//        set = set.iterator().next().continuedByOneIntermediary();
//        for(ImpactChain i : set) System.out.println(i.toString());
//        set = set.iterator().next().continuedByOneIntermediary();
//        for(ImpactChain i : set) System.out.println(i.toString());        
    }
    
    public static void testWOargs() {
        
        try {
            InputFileReader ifr = new InputFileReader();
            CrossImpactMatrix matrix = ifr.readInputFile("src/exit/inputfile12.csv");
            
            System.out.println("\nImpact matrix describing direct impacts between variables:");
            System.out.println(matrix.toString());
            

            
            CrossImpactMatrix result = matrix.summedImpactMatrix(0.5);
            
            System.out.println("\nImpact matrix describing total direct and indirect impacts between variables:");
            System.out.println(result.toString());
            

//            List<ImpactChain> sic = matrix.indirectImpacts(3, 5, 0.05);
//            
//            
//            int counter=0;
//            for(ImpactChain i : sic) {
//                System.out.println(i.toString());
//                counter++;
//            }
//            System.out.println(counter + " impact chains printed");
            
            
            
        } catch (IOException | EXITException ex) {
            Logger.getLogger(EXIT.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }
    
    public static void JL_exit(String[] args) {
        
    }
    
    public static void testWargs(String[] args) {
        try {
            
            EXITArguments arguments = new EXITArguments(args);
            System.out.println(arguments.toString());            
            
            InputFileReader ifr = new InputFileReader();
            CrossImpactMatrix matrix = ifr.readInputFile(arguments.inputFilename);

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
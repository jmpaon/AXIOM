/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exit;

/**
 *
 * @author jmpaon
 */
class EXITargumentException extends EXITexception {

    public EXITargumentException(String msg) {
        //super(msg);
        System.out.println(msg);
        printUsage();
        
    }
    
    public final void printUsage() {
        
        String s =
                "Usage:%n" +
                "exit-cia inputfile [options...]%n" +
                "e.g.: %n" +
                "exit-cia inputfile.csv -max 5 -t 0.05%n" +
                "%n" + 
                "AVAILABLE OPTIONS:%n" +
                " -max\tMaximum impact value available in the input matrix in the input file%n" +
                " -o\tOutput file name; if provided, output is written to this file%n" +
                " -int\tIf present, the input matrix should contain only integral values%n" +
                " -t\tTreshold value for mining the significant impact chains in the matrix.%n" + 
                "\tThe chains having a lower impact value than the treshold will not be included%n" +
                "\tin the summed impacts and their expansions will not be generated in the mining%n" +
                " -sep\tThe character used as a separator in the input file.%n" +
                " -of\tVariable index of impactor; if passed, impact chains starting from variable with this index are printed%n" +
                " -on\tVariable index of impacted; if passed, impact chains ending to variable with this index are printed%n" +
                " -extra\tIf present, extra outputs (importance matrices, driver-driven tables) are printed%n";
        
        System.out.printf(s);
        System.exit(1);
        
    }
   
}
                
                
    


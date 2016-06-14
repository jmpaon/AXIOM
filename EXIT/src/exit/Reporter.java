/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exit;

import java.io.PrintStream;
import java.text.Format;
import java.util.Arrays;

/**
 * The methods of this class take care of reporting progress in
 * the calculation procedure and printing out the results.
 * Behavior should be changed by changing these reporting operations.
 * @author jmpaon
 */
public class Reporter {
    
    /**
     *
     */
    public static int requiredReportingLevel;
    public static PrintStream output = System.out;
        
    public static void indicateProgress(final String msg, int level) {
        if (level >= Reporter.requiredReportingLevel) {
            output.print(msg);
        } 
    }
    
    public static void msg(String format, Object... args) {
        output.print(String.format(format, (Object[]) args));
    }
    
    public static void reportError(final String msg) {
        output.print(msg);
        System.exit(1);
    }
    
    public static void reportError(Exception e) {
        output.println(e.getMessage());
        output.println(Arrays.toString(e.getStackTrace()));
    }
    
}

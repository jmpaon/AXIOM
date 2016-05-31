/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exit;

import java.text.Format;

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
    public static int verbosityLevel;
    
    public static void indicateProgress(final String msg) {
        System.out.print(msg);
    }
    
    public static void indicateProgress(final String msg, int verbosityLevel) {
        
    }
    
    public static void reportError(final String msg) {
        System.out.print(msg);
        System.exit(1);
    }
    
}

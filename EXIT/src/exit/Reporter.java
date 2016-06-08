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
    public static int requiredReportingLevel;
        
    public static void indicateProgress(final String msg, int level) {
        if (level >= Reporter.requiredReportingLevel) {
            System.out.print(msg);
        } 
    }
    
    public static void msg(String format, Object... args) {
        System.out.print(String.format(format, (Object[]) args));
    }
    
    public static void reportError(final String msg) {
        System.out.print(msg);
        System.exit(1);
    }
    
}

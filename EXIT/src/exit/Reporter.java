/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exit;

import java.io.PrintStream;
import java.util.Arrays;

/**
 * The methods of this class take care of reporting progress in
 * the calculation procedure and printing out the results.
 * Behavior should be changed by changing these reporting operations.
 * @author jmpaon
 */
public class Reporter {
    
    /**
     * <b>requiredReportingLevel</b> is the level that is required from
     * progress messages to be printed. The higher the level, 
     * the fewer progress messages will be printed.
     * <b>output</b> is the <code>PrintStream</code> 
     * the progress messages will be printed into.
     * By default the messages are printed to <code>System.out</code>.
     */
    public static int requiredReportingLevel;
    public static PrintStream output = System.out;
        
    
    /**
     * Prints a message indicating the progress of EXIT calculation.
     * @param msg A message describing how the calculation procedure progresses.
     * @param level The importance of this message. If <b>level</b> is lower than
     * <b>requiredReportingLevel</b> the message will not be printed.
     */
    public static void msg(final String msg, int level) {
        if (level >= Reporter.requiredReportingLevel) {
            output.print(msg);
        }
    }
    
    /**
     * Prints a formatted message to <code>Reporter.output</code>.
     * @param format String containing the format of the message.
     * @param args Objects referenced in the format
     */
    public static void msg(String format, Object... args) {
        output.print(String.format(format, (Object[]) args));
    }

    
}

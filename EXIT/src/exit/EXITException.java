/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package exit;

/**
 *
 * @author jmpaon
 */
public class EXITException extends Exception {

    /**
     * Creates a new instance of
     * <code>EXITException</code> without detail message.
     */
    public EXITException() {
    }

    /**
     * Constructs an instance of
     * <code>EXITException</code> with the specified detail message.
     *
     * @param msg the detail message.
     */
    public EXITException(String msg) {
        super(msg);
    }
}

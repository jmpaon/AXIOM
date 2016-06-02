/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package exit;

/**
 *
 * @author jmpaon
 */
public class EXITexception extends Exception {

    /**
     * Creates a new instance of
     * <code>EXITException</code> without detail message.
     */
    public EXITexception() {
    }

    /**
     * Constructs an instance of
     * <code>EXITException</code> with the specified detail message.
     *
     * @param msg the detail message.
     */
    public EXITexception(String msg) {
        super(msg);
    }
}

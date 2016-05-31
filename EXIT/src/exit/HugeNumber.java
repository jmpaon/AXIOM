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
public class HugeNumber {
    
    double val = 0;
    int    exp = 0;
    
    public HugeNumber(double value) {
        boolean negative = value < 0;
        this.val = value;
        while(Math.abs(val) >= 10) {
            val /= 10;
            exp++;
        }
    }
    
    @Override
    public String toString() {
        return String.format("%1.2f x 10^%d", val, exp);
    }
}

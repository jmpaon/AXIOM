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
public class Mem {
    public int value;
    
    public Mem(int value) {
        this.value = value;
    }
    
    public void set(int value) {
        this.value = value;
    }
    
    public int get() {
        return this.value;
    }
    
    @Override
    public String toString() {
        return String.valueOf(value);
    }
    


    
    
}

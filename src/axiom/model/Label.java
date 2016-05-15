/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package axiom.model;

/**
 *
 * @author juha
 */
public class Label implements Comparable<Label> {
    
    public final String label;
    
    public Label(String label) throws ArgumentException {
        if(label.length()>20)
            throw new ArgumentException("Label ("+label+" is too long; maximum length for label is 20 characters");
        this.label = label;
    }

    @Override
    public int compareTo(Label l) {
        return this.label.compareTo(l.label);
    }
    
}

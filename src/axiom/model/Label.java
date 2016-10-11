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
    
    public static final int MAX_LABEL_LENGTH = 20;
    public final String value;
    public final LabelNamespace namespace;
    
    /**
     * This Label constructor is meant to be used in adding labels 
     * to AXIOM model components (statements and options at this point).
     * For statements, the namespace argument is the AXIOM model to which the statement belongs to;
     * For options, the namespace is the statement the option belongs to.
     * @param label Name/identifier for the AXIOM model component
     * @param namespace The context (a <code>LabelNamespace</code> for which the value must be unique. 
     */
    public Label(String label, LabelNamespace namespace) {
        
        assert namespace != null;
        assert label != null;
        
        this.namespace = namespace;
        if( label.length() > MAX_LABEL_LENGTH )
            throw new IllegalArgumentException("Label ("+label+" is too long; maximum length for label is 20 characters");
        
        // Test if value is already in use in the intended namespace
        Label l = new Label(label);
        if(namespace.getNamespaceLabels().contains(l))
            throw new IllegalArgumentException("Label " + label + " is already in use");
        
        this.value = label;
    }
    
    /**
     * This constructor should be used only for creating temporary labels
     * for finding AXIOM components with labels.
     * When labels are added to components, they should be provided a namespace.
     * @param label 
     */
    Label(String label) {
        assert label != null;
        
        if( label.length() > MAX_LABEL_LENGTH )
            throw new IllegalArgumentException("Label ("+label+" is too long; maximum length for label is 20 characters");
        
        this.namespace = null;
        this.value = label;
    }
    

    @Override
    public int compareTo(Label l) {
        assert l != null;
        return this.value.compareTo(l.value);
    }
    
    @Override
    public String toString() {
        return this.value;
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package axiom.model;

import java.util.Objects;

/**
 * The label class represents identifying labels of AXIOM model components.
 * These components can have a {@link LabelNamespace}, within which 
 * they should be unique.
 * @author juha
 */
public class Label implements Comparable<Label> {
    
    /**
     * Constant maximum length that labels can have.
     */
    public static final int MAX_LABEL_LENGTH = 20;
    public static final char[] NON_ALLOWED_CHARS = {':'};
    
    /**
     * The String value of the label.
     */
    public final String value;
    
    /**
     * Namespace where this label is unique.
     * Namespace is a set of a particular type of AXIOM model components, 
     * such as the set of statements or the set of options under a statement
     */
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
        for(char c : NON_ALLOWED_CHARS) assert !label.contains(String.valueOf(c)) : "A non-allowed character (" + c + ") in label value " + label;
        
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
        for(char c : NON_ALLOWED_CHARS) assert !label.contains(String.valueOf(c));
        
        if( label.length() > MAX_LABEL_LENGTH )
            throw new IllegalArgumentException("Label ("+label+" is too long; maximum length for label is 20 characters");
        
        this.namespace = null;
        this.value = label;
    }
    
    
    
    @Override
    public boolean equals(Object o) {
        if(o == null) return false;
        if(!Label.class.isAssignableFrom(o.getClass())) return false;
        final Label l = (Label) o;
        if(l.namespace != null && this.namespace != null) {
            if(!this.namespace.equals(l.namespace)) return false;
        }
        if(!this.value.equalsIgnoreCase(l.value)) return false;
        return true;
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Objects.hashCode(this.value.toLowerCase());
        hash = 67 * hash + Objects.hashCode(this.namespace);
        return hash;
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

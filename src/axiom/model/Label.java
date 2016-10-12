/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package axiom.model;

import java.util.Objects;

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

    
//public boolean equals(Object obj) {
//    if (obj == null) {
//        return false;
//    }
//    if (!Person.class.isAssignableFrom(obj.getClass())) {
//        return false;
//    }
//    final Person other = (Person) obj;
//    if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
//        return false;
//    }
//    if (this.age != other.age) {
//        return false;
//    }
//    return true;
//}    
    
    
    
    
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

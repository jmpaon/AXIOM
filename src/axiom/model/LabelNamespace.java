/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package axiom.model;

import java.util.Collection;

/**
 *
 * @author jmpaon
 */
public interface LabelNamespace {
    
    /**
     * Returns a collection with all the labels in the namespace.
     * This is for testing whether or not the label already exists in the namespace.
     * @return 
     */
    public Collection<Label> getNamespaceLabels();
    
    
}

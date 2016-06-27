/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exit;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Objects of this class are used to store and display information
 * about variables in the cross-impact matrix.
 * @author jmpaon
 */
public class VarInfoTable<T> {
    
    public final List<String> varNames;
    public final List<String> valueHeadings;
    public final List<List<T>> values;
    
    /**
     * Constructor for <code>VarInfoTable</code>
     * @param valueHeadings A list containing headings for the value columns of the table
     */
    public VarInfoTable(List<String> valueHeadings) {
        if(valueHeadings == null) throw new NullPointerException("Value headings list null");
        if(valueHeadings.isEmpty()) throw new IllegalArgumentException("Value headings list empty");
        this.valueHeadings = valueHeadings;
        this.varNames = new LinkedList<>();
        this.values = new LinkedList<>();
    }
    
    public VarInfoTable() {
        this.varNames = new LinkedList<>();
        this.values = new LinkedList<>();
        this.valueHeadings = null;
    }
    
    /**
     * Adds a row of information about a variable to the table
     * @param varName Name of the variable / row heading
     * @param value List of values related to the variable (or row heading)
     */
    public void put(String varName, List<T> value) {
        if(varName == null) throw new NullPointerException("varName is null");
        if(value == null) throw new NullPointerException("value list is null");
        if(value.isEmpty()) throw new IllegalArgumentException("value list is empty");
        this.varNames.add(varName);
        this.values.add(value);
    }
    
    @Override
    public String toString() {
        Iterator<String> it_s = varNames.iterator();
        Iterator<List<T>> it_l = values.iterator();
        StringBuilder sb = new StringBuilder();
        if(valueHeadings != null) {
            StringBuilder hsb = new StringBuilder();
            Iterator<String> it_vhs = valueHeadings.iterator();
            while(it_vhs.hasNext()) {
                hsb.append(it_vhs.next());
                if(it_vhs.hasNext()) hsb.append(String.format("\t"));
            }
            sb.append(String.format("%55s\t\t%s%n", " ", hsb.toString()));
        }

        
        while( it_s.hasNext() && it_l.hasNext() ) {
            String varName = it_s.next();
            List<T> list = it_l.next();
            StringBuilder lsb = new StringBuilder();
            
            Iterator<T> it = list.iterator();
            while(it.hasNext()) {
                T t = it.next();
                if(t instanceof Number) {
                    lsb.append(String.format("%3.2f", ((Double) t)));
                } else {
                    lsb.append(t.toString());
                }
                
                
                if (it.hasNext() ) lsb.append("\t");
            }
            sb.append(String.format("%55s", varName)).append(":\t").append(lsb).append("\n");            
        }
        return sb.toString();
    }
    
}

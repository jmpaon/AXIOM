/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package axiom.reader;

import axiom.model.Model;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Objects of this class can be used to extract information from 
 * input data file lines to create <tt>ModelBuildingAction</tt>s 
 * that are then executed to create an AXIOM {@link Model}. 
 * @author juha
 */
class Command {
    
    public final String context_statementLabel;
    public final String context_optionLabel;
    
    public final String text;
    public final List<String> parts;
    private final int lastIndex;
    
    public Command(String commandText) {
        this(commandText, null, null);
    }
    
    public Command(String commandText, String statementLabel) {
        this(commandText, statementLabel, null);
    }
    
    /**
     * Constructor for command.
     * Splits the <b>commandText</b> by whitespace characters and places splitted parts to <b>parts</b> list.
     * @param commandText A part of the input file containing the command in text format.
     * @param statementLabel The label of the statement the added component (option or impact) is associated with
     * @param optionLabel The label of the option the added component (impact) is associated with
     */
    public Command(String commandText, String statementLabel, String optionLabel) {
        this.text = commandText;
        parts = new LinkedList<>();
        String[] cmd = commandText.split("(?=[\\s+])");
        for(String s : cmd) {
            String ss = s.replaceAll("\\s", "").trim().toLowerCase();
            if(!ss.equals(""))  parts.add(ss.trim());
        }
        this.lastIndex = parts.size()-1;
        this.context_statementLabel = statementLabel;
        this.context_optionLabel = optionLabel;
    }
    
    /**
     * Does <b>parts</b> have <b>s</b>?
     * @param s 
     * @return <i>true</i> if s is present in <b>parts</b>
     */
    public boolean has(String s) {
        return parts.contains(s);
    }
    
    /**
     * Returns the String in <b>parts</b> right from the position of <b>s</b>.
     * @param s
     * @return String
     */
    public String right(String s) {
        if(parts.contains(s)) {
            if(parts.indexOf(s) == lastIndex) return null;
            return get(parts.indexOf(s)+1);
        }
        return null;
    }
    
    /**
     * Returns the String in <b>parts</b> left from the position of <b>s</b>.
     * @param s
     * @return 
     */
    public String left(String s) {
        if(parts.contains(s)) {
            if(parts.indexOf(s) == 0) return null;
            return get(parts.indexOf(s)-1);
        }
        return null;
    }
    
    /**
     * Returns the String in <b>parts</b> at position <b>index</b>. 
     * @param index
     * @return 
     */
    public String get(int index) {
        return parts.get(index);
    }
    

    
    
    @Override
    public String toString() {
        String ss = ""; int i=1;
        for(String s : parts) ss += i++ + " " + s + "\n";
        return ss;
        
    }
}

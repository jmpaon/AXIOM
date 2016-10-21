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
 *
 * @author juha
 */
public class Command {
    
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
    
    
    public boolean has(String s) {
        return parts.contains(s);
    }
    
    public String right(String s) {
        if(parts.contains(s)) {
            if(parts.indexOf(s) == lastIndex) return null;
            return get(parts.indexOf(s)+1);
        }
        return null;
    }
    
    public String left(String s) {
        if(parts.contains(s)) {
            if(parts.indexOf(s) == 0) return null;
            return get(parts.indexOf(s)-1);
        }
        return null;
    }
    
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

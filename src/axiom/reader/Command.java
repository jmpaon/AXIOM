/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package axiom.reader;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author juha
 */
public class Command {
    public String text;
    public List<String> parts;
    
    public Command(String commandText) {
        parts = new LinkedList<>();
        String[] cmd = commandText.split("(?=[\\s+])");
        //String[] cmd = commandText.split("(?=[#\\*>\\'])");
        Collections.addAll(parts, cmd);
    }
    
    // public String findAfter
    
    @Override
    public String toString() {
        String ss = "";
        for(String s : parts) ss += s + " == ";
        return ss;
        
    }
}

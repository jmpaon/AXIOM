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
        String[] cmd = commandText.split("(?=[#\\*>\\'])");
        for(String s : cmd) System.out.println(s + ":");
        Collections.addAll(parts, cmd);
    }
    
    @Override
    public String toString() {
        return this.parts.toString();
    }
}

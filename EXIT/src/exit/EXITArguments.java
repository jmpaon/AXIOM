package exit;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author jmpaon
 */
public class EXITArguments {
    
    public final List<String> knownOptions = initializeKnownOptions();
    public final List<String> args;
    public final String inputFilename;
    public final Double maxImpact;
    public final String outputFilename;
    public final String impactOf;
    public final String impactOn;
    public final boolean onlyIntegers;
    
    
    private List<String> initializeKnownOptions() {
        return new ArrayList<>(Arrays.asList(
                "-o",      // Output file name
                "-int",    // All impacts are integers
                "-of",     // Print impacts of 
                "-on",     // Print impacts on
                "-max"     // Maximum impact value
        ));
    }
    
    
    public EXITArguments(String[] args) throws EXITArgumentException {
        
        this.args = Arrays.asList(args);
        if(hasUnknownOptions()) {
            throw new EXITArgumentException("Unknown options");
        }
        
        inputFilename  = this.args.get(0);
        outputFilename = extractArgumentValue("-o");
        impactOf       = extractArgumentValue("-of");
        impactOn       = extractArgumentValue("-on");
        maxImpact      = Double.valueOf(extractArgumentValue("-max"));
        onlyIntegers   = hasFlag("-int");
    }
    
    private boolean hasUnknownOptions() {
        for(String arg : args) {
            if(arg.startsWith("-")) {
                if(!knownOptions.contains(arg)) {
                    return true;
                } 
            }
        }
        return false;
    }
    
    
    private String extractArgumentValue(String id) {
        int idPos = args.indexOf(id);
        if(idPos == -1) return null;
        if(idPos == args.size()-1) return null;
        return args.get(idPos+1);
    }
    
    
    private boolean hasFlag(final String id) {
        return args.contains(id);
    }
    
    
    @Override
    public String toString() {
        Class<?> objClass = this.getClass();
        String s = "";

        Field[] fields = objClass.getFields();
        
        for(Field field : fields) {
            String name = field.getName();
            Object value;
            try { value = field.get(this);} 
            catch (Exception ex) { value = new String("No value"); }
            s += String.format("%s%n", (name + ": " + (value == null ? "No value" : value.toString())));
        }
        
        return s;
    }    
    
}

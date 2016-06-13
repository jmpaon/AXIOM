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
public class EXITarguments {
    
    public final List<String> knownOptions = initializeKnownOptions();
    public final List<String> args;
    public final String inputFilename;
    public final Double maxImpact;
    public final String outputFilename;
    public final String impactOf;
    public final String impactOn;
    public final boolean onlyIntegers;
    public final double treshold;
    public final Character separator;
    
    
    private List<String> initializeKnownOptions() {
        return new ArrayList<>(Arrays.asList(
                "-o",      // Output file name
                "-int",    // All impacts are integers
                "-of",     // Print impacts of 
                "-on",     // Print impacts on
                "-max",    // Maximum impact value
                "-t",      // Impact treshold
                "-sep"     // Separator character in input data
        ));
    }
    
    
    public EXITarguments(String[] args) throws EXITargumentException {
        
        this.args = Arrays.asList(args);
        if(hasUnknownOptions()) {
            throw new EXITargumentException(String.format("Unknown options used. Known options are the following: %s%n", knownOptions.toString()));
        }
        
        inputFilename  = this.args.get(0);
        outputFilename = extractArgumentValue("-o");
        impactOf       = extractArgumentValue("-of");
        impactOn       = extractArgumentValue("-on");
        maxImpact      = hasFlag("-max") ? Double.valueOf(extractArgumentValue("-max")) : 5;
        onlyIntegers   = hasFlag("-int");
        treshold       = hasFlag("-t") ? Double.valueOf(extractArgumentValue("-t")) : 0.20;
        separator      = hasFlag("-sep") ? extractArgumentValue("-sep").charAt(0) : ';' ;
    }
    
    
    /**
     * Tests if args list contains flags (entries that have '-' character in front of them) 
     * that are not in the known options list.
     * @return <i>true</i> if args list contains flags not in <b>knownOptions</b> list, false otherwise.
     */
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
    
    /**
     * Extracts a value than follows a specific flag in the args list.
     * @param id flag to search from the args list
     * @return The value that is in the args list at the succeeding index of <b>id</b>
     * @throws EXITargumentException 
     */
    private String extractArgumentValue(String id) throws EXITargumentException {
        int idPos = args.indexOf(id);
        if(idPos == -1) return null;
        if(idPos == args.size()-1) throw new EXITargumentException(String.format("value for argument %s is missing", id));
        return args.get(idPos+1);
    }
    
    /**
     * Tests if args list contains a specific flag (something that has a '-' character in front of it).
     * @param id The flag that is sought from args list
     * @return <i>true</i> if args list contains flag, false otherwise
     */
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
            catch (Exception ex) { value = "No value"; }
            s += String.format("%s%n", (name + ": " + (value == null ? "No value" : value.toString())));
        }
        
        return s;
    }    
    
}

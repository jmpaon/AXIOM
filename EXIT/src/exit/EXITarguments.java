package exit;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
    
    
    public final List<String> args;
    public final String inputFilename;
    public final Double maxImpact;
    public final String outputFilename;
    public final String impactOf;
    public final String impactOn;
    public final boolean onlyIntegers;
    public final boolean extraReports;
    public final double treshold;
    public final Character separator;
    
    
    /**
     * Returns a map that contains the 
     * accepted options that can be passed to the cross-impact analysis
     * and meanings of options
     * @return Map where legal options are keys and their explanations are values
     */
    public static Map<String, String> knownOptions() {
        Map<String, String> options = new LinkedHashMap<>();
        
        options.put("-o",     "Output file name");
        options.put("-max",   "Maximum impact value in the impact matrix");
        options.put("-t",     "Impact treshold used in impact chain mining");
        options.put("-sep",   "Separator character used in input data");
        options.put("-of",    "Print impacts of variable with index");
        options.put("-on",    "Print impacts on variable with index");
        options.put("-int",   "Flag if all impacts in input matrix are integers");
        options.put("-extra", "Flag to print extra reports");
        
        return options;
    }
    
    
    public EXITarguments(String[] args) throws EXITargumentException {
        
        this.args = Arrays.asList(args);
        if(hasUnknownOptions()) {
            throw new EXITargumentException(String.format("Unknown options %s used. Known options are the following: %s%n", unknownOptionsUsed(),  knownOptions().keySet().toString()));
        }
        
        if(args.length < 1) throw new EXITargumentException("No input file specified.");
        
        inputFilename  = this.args.get(0);
        outputFilename = extractArgumentValue("-o");
        impactOf       = extractArgumentValue("-of");
        impactOn       = extractArgumentValue("-on");
        maxImpact      = hasFlag("-max") ? Double.valueOf(extractArgumentValue("-max")) : 5;
        onlyIntegers   = hasFlag("-int");
        extraReports   = hasFlag("-extra");
        treshold       = hasFlag("-t") ? Double.valueOf(extractArgumentValue("-t")) : 0.10;
        separator      = hasFlag("-sep") ? extractArgumentValue("-sep").charAt(0) : ';' ;
        
    }
    
    
    /**
     * Tests if args list contains flags (entries that have '-' character in front of them) 
     * that are not in the known options list.
     * @return <i>true</i> if args list contains flags not in list returned by <b>knownOptions()</b>, false otherwise.
     */
    private boolean hasUnknownOptions() {
        for(String arg : args) {
            if(arg.startsWith("-")) {
                if(!knownOptions().keySet().contains(arg)) {
                    return true;
                } 
            }
        }
        return false;
    }
    
    /**
     * Tests whether arguments contain unknown options.
     * @return <i>true</i> if arguments contain options that are unknown
     * (not in the known options list
     * @see EXITarguments#knownOptions() 
     */
    private List<String> unknownOptionsUsed() {
        List<String> unknown = new LinkedList<>();
        for(String arg : args) {
            if(arg.startsWith("-") && !knownOptions().keySet().contains(arg))
                unknown.add(arg);
        }
        return unknown;
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

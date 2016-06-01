/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package exit;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.List;


/**
 *
 * @author jmpaon
 */
public class InputFileReader {
    
    public CrossImpactMatrix readInputFile(String filename) throws IOException, EXITException {
        
        // At this point only CSV files are read
        return readCSVfile(filename, ';');
        
        
        

    }
    
    CrossImpactMatrix readCSVfile(String filename, char separator) throws IOException, EXITException {
        
        Reporter.indicateProgress(String.format("Reading impact matrix data from file %25s%n", filename),5);
        
        List<String> lines = Files.readAllLines(Paths.get(filename));
        eliminateEmptyLines(lines);

        int variableCount = lines.size();
        int var=1;

        // FIXME remove hard-coded maxImpact
        CrossImpactMatrix cim = new CrossImpactMatrix(5, variableCount);

        for(String l : lines) {

            Scanner sc = new Scanner(l).useDelimiter(String.valueOf(separator));
            cim.setName(var, sc.next() );
            int imp=0;
            while(sc.hasNextDouble()) {
                imp++;
                cim.setImpact(var, imp, sc.nextDouble());
            }

            if (imp != variableCount) {
                throw new EXITArgumentException(String.format("Wrong number of impact values: number of lines in input file suggests that there are %d variables, but line %d (Variable '%s') contains %d impact values", variableCount, var, cim.getName(var), imp));
            }

            var++;
        }

        cim.lock();
        Reporter.indicateProgress(String.format("Read %d variables from input file.%n", cim.getVarCount()),4);
        return cim;

    }
    
    /**
     * Reads 
     * @param filename
     * @param separator
     * @return 
     */
    CrossImpactMatrix readTXTfile(String filename, String separator) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
    
    /**
     * 
     * @param lines 
     */
    void eliminateEmptyLines(List<String> lines) {
        
        List<String> removed = new LinkedList<>();
        
        for(String l : lines) {
            if(l.trim().length() == 0) { removed.add(l); }
        }
        
        lines.removeAll(removed);
        
    }
    
}

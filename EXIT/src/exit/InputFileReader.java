/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package exit;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.regex.Pattern;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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
        
        List<String> lines = Files.readAllLines(Paths.get(filename));
        int variableCount = lines.size();
        
        
        try {
            
            int var=1;
            
            CrossImpactMatrix cim = new CrossImpactMatrix(null, variableCount);
            
            for(String l : lines) {
                int imp=1;

                Scanner sc = new Scanner(l).useDelimiter(String.valueOf(separator));
                cim.setName(var, sc.next());
                while(sc.hasNextDouble()) {
                    cim.setImpact(var, imp, sc.nextDouble());
                    imp++;
                }
                var++;
            }
            
            cim.lock();
            return cim;
            
        } catch (ModelBuildingException ex) {
            Logger.getLogger(InputFileReader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ArgumentException ex) {
            Logger.getLogger(InputFileReader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (EXITException ex) {
            Logger.getLogger(InputFileReader.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        throw new EXITException("No dice");
        
    }
}

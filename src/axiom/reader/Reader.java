/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package axiom.reader;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author juha
 */
public class Reader {
    public final String filename;
    public final String qualifiedFilename;
    public final String content;
    
    public Reader(String filename) throws IOException {
        this.filename = filename;
        this.qualifiedFilename = (System.getProperty("user.dir")) + this.filename;
        content = new String(Files.readAllBytes(Paths.get(filename)), Charset.forName("UTF-8"));
    }
    
    public List<ModelBuildingAction> isolateCommands() {
        
    } 
    
    @Override
    public String toString() {
        return String.format("%20s %s\n%20s %s\n%20s %s\n",
                "Filename:" , filename,
                "Qualified filename:" , qualifiedFilename,
                "Content: ", content.length()+" characters");
    }
    
}

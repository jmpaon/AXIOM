/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package axiom.reader;

import axiom.model.AXIOMException;
import axiom.model.Model;
import axiom.probabilityAdjusters.ProbabilityAdjusterFactory;
import axiom.probabilityAdjusters.ProbabilityAdjustmentException;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
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
        this.qualifiedFilename = (System.getProperty("user.dir")) + File.separator + this.filename;
        String allContent = new String(Files.readAllBytes(Paths.get(qualifiedFilename)), Charset.forName("UTF-8"));
        
        allContent = allContent.replaceAll("\n", " ");
        allContent = allContent.replaceAll("\t", "");
        allContent = allContent.replaceAll("\\s+", " ");
        allContent = allContent.trim();
        
        this.content = allContent.trim();
        
    }
    
    /**
     * Creates an AXIOM model on the basis of the file passed to this <tt>Reader</tt>.
     * @return New AXIOM <tt>Model</tt> based on the contents of file <b>filename</b>
     * @throws ProbabilityAdjustmentException
     * @throws AXIOMInputException
     * @throws AXIOMException 
     */
    public Model createAXIOMmodelFromInput() throws ProbabilityAdjustmentException, AXIOMInputException, AXIOMException {
        Model model = new Model("AXIOM model", new ProbabilityAdjusterFactory().createDefaultNameProbabilityAdjuster());
        List<ModelBuildingAction> mba = fileToModelBuildingActions(model);
        
        /* Model building actions are sorted to their natural ordering by precedence */
        Collections.sort(mba);
        
        /* Execute model building actions */
        for(ModelBuildingAction m : mba) m.execute();
        
        /* After model additions have been performed, ready model for computation */
        model.fixProbabilityDistributionErrors();
        model.add.disableAdditions();
        
        return model;
    }
    
    private List<ModelBuildingAction> fileToModelBuildingActions(Model model) throws AXIOMException, AXIOMInputException {
        
        final List<ModelBuildingAction> mba = new LinkedList<>();
        final String[] splitByDelimiters = this.content.split("(?=[#\\*>\\'])");
        String currentStatementLabel = null;
        String currentOptionLabel = null;
        
        List<Command> commands = new LinkedList<>();
        for(String s : splitByDelimiters) {
            Command c = new Command(s, currentStatementLabel, currentOptionLabel);
            ModelBuildingAction a = new ModelBuildingAction(model, c);
            if(a.type == ModelBuildingAction.ActionType.ADDSTATEMENT) currentStatementLabel = c.get(1);
            if(a.type == ModelBuildingAction.ActionType.ADDOPTION) currentOptionLabel = c.get(1);
            
            mba.add(a);
        }
        return mba;
    }
    
    private List<Command> fileToCommands() {
        String[] splitByDelimiters = this.content.split("(?=[#\\*>\\'])");
        String currentStatementLabel = null;
        String currentOptionLabel = null;
        
        List<Command> commands = new LinkedList<>();
        for(String s : splitByDelimiters) {
            commands.add(new Command(s));
        }
        return commands;
    }
    
    public void print() {
        for(Command c : fileToCommands()) System.out.println(c + "\n");
    }
    
    @Override
    public String toString() {
        return String.format("%20s %s\n%20s %s\n%20s %s\n",
                "Filename:" , filename,
                "Qualified filename:" , qualifiedFilename,
                "Content: ", content.length()+" characters");
    }
    
}

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
 * Class for reading AXIOM input files.
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
        
        this.content = allContent;
        
    }
    
    /**
     * Creates an AXIOM model on the basis of the file passed to this <tt>Reader</tt>.
     * @return New AXIOM <tt>Model</tt> based on the contents of file <b>filename</b>
     * @throws ProbabilityAdjustmentException
     * @throws AXIOMInputException
     * @throws AXIOMException 
     */
    public Model createAXIOMmodelFromInput() throws ProbabilityAdjustmentException, AXIOMInputException, AXIOMException {
        Model model = new Model(
                "AXIOM model from file " + this.qualifiedFilename, 
                new ProbabilityAdjusterFactory().createDefaultNameProbabilityAdjuster()); /* A default probability adjuster is used for now */
        
        /* Model building actions are sorted to their natural ordering by precedence */
        List<ModelBuildingAction> mba = fileToModelBuildingActions(model);
        
        /* Execute model building actions in the precedence order*/
        for(ModelBuildingAction m : mba) m.execute();
        
        /* After model additions have been performed, ready model for computation */
        
        /* The conversion of input data to an AXIOM model might result
        in the probability distributions of model statements not summing to
        exactly 1. For this reason, the probability distribution errors 
        must be corrected so a valid model can be returned.
        */
        model.add.fixProbabilityDistributionErrors();
        model.add.disableAdditions();
        
        return model;
    }
    
    /**
     * Creates a list of model building operations on the basis of the input file.
     * The list is sorted to the precedence order of <tt>ModelBuildingAction</tt>s, 
     * so that statements come before options that come before impacts.
     * @param model AXIOM model
     * @return List of <tt>ModelBuildingAction</tt>s in precedence order
     * @throws AXIOMException
     * @throws AXIOMInputException 
     */
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
        Collections.sort(mba);
        return mba;
    }
    
    /**
     * Splits the input file to <tt>Command</tt>s.
     * Each command in returned list can be converted to a 
     * <tt>ModelBuildingAction</tt>.
     * @return 
     */
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

    @Override
    public String toString() {
        return String.format("%20s %s\n%20s %s\n%20s %s\n",
                "Filename:" , filename,
                "Qualified filename:" , qualifiedFilename,
                "Content: ", content.length()+" characters");
    }
    
}

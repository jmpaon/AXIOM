/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package axiom.reader;

import axiom.model.AXIOMException;
import axiom.model.LabelNotFoundException;
import axiom.model.Model;

/**
 * Objects of this class are constructed on the basis of the information 
 * in the input file and perform the addition of AXIOM model components
 * to the model as they are executed.
 * @author juha
 */
class ModelBuildingAction implements Comparable<ModelBuildingAction> {

    /**
     * Enumeration for model building action type.
     * <tt>ModelBuildingAction</tt> with type 
     * ADDSTATEMENT adds a statement,
     * ADDOPTION adds an option and
     * ADDIMPACT adds an impact.
     */
    public enum ActionType {
        ADDSTATEMENT,
        ADDOPTION,
        ADDIMPACT
    }
    
    public final ActionType type;
    public final Model model;
    public final Command command;
    public final int precedence;
    
    
    /**
     * Constructor for <tt>ModelBuildingAction</tt>
     * @param model Model to which the components are added 
     * @param command Component addition command
     * @throws AXIOMInputException 
     */
    public ModelBuildingAction(Model model, Command command) throws AXIOMInputException {
        this.model = model;
        this.command = command;
        this.type = identifyActionType();
        this.precedence = determinePrecedence();
    };
    
    /**
     * Executes this model building action by performing 
     * the model component addition to <b>model</b>.
     * 
     * @throws AXIOMInputException 
     */
    public void execute() throws AXIOMInputException {
        try {
            switch (type) {
                case ADDSTATEMENT:
                    addStatement();
                    break;
                case ADDOPTION:
                    addOption();
                    break;
                case ADDIMPACT:
                    addImpact();
                    break;
                default:
                    throw new RuntimeException();
            }
        } catch (Exception e) {
            throw new AXIOMInputException("Input syntax error in '" + this.command.text + "': " + e.getMessage() + e.toString()  );
        }
    }
    
    /**
     * Returns an int representing the precedence of the model building action.
     * Higher value means higher precedence: 
     * model building actions with higher precedence are executed first.
     * @return 
     */
    private int determinePrecedence() {
        switch(type) {
            case ADDSTATEMENT : return 5;
            case ADDOPTION    : return 3;
            case ADDIMPACT    : return 1;
            default           : return 0;
        }        
    }
    
    /**
     * Identifies the model building action type on the basis of the 
     * symbol in the first index of <b>command</b>.
     * @return ActionType
     * @throws AXIOMInputException 
     */
    private ActionType identifyActionType() throws AXIOMInputException {
        switch(command.parts.get(0)) {
            case "#" : return ActionType.ADDSTATEMENT;
            case "*" : return ActionType.ADDOPTION;
            case ">" : return ActionType.ADDIMPACT;
            default  : throw new AXIOMInputException("Unknown model component type in input: " + command.text);
        }
    }
    
    /**
     * Adds a statement to the model.
     * Called by {@link ModelBuildingAction#execute()} 
     */
    private void addStatement() {
        String statementLabel = command.get(1);
        String description = "";
        boolean intervention = command.has("int");
        int timestep = command.has("ts") ? Integer.valueOf(command.right("ts")) : 0;
        
        model.add.statement(statementLabel, description, intervention, timestep);
    }
    
    /**
     * Adds an option to the model.
     * Called by {@link ModelBuildingAction#execute()} 
     */    
    private void addOption() throws LabelNotFoundException {
        String statementLabel = command.context_statementLabel;
        String optionLabel    = command.get(1);
        double apriori = Double.valueOf(command.get(2));
        model.add.option(statementLabel, optionLabel, apriori);
    }
    
    
    /**
     * Adds an impact to the model.
     * Called by {@link ModelBuildingAction#execute()} 
     */
    private void addImpact() throws AXIOMException {
        String fromStatementLabel = command.context_statementLabel;
        String fromOptionLabel    = command.context_optionLabel;
        String toStatementLabel   = command.get(1);
        String toOptionLabel      = command.get(2);
        String adjustmentFunctionName = command.get(3);
        
        model.add.impact(fromStatementLabel, fromOptionLabel, toStatementLabel, toOptionLabel, adjustmentFunctionName);
    }
    
    /**
     * <code>ModelBuildingAction</code>s with higher <b>precedence</b> 
     * should be ordered before ones with lower <b>precedence</b> 
     * (and executed first).
     * @param otherModelBuildingAction
     * @return 
     */
    @Override
    public int compareTo(ModelBuildingAction otherModelBuildingAction) {
        return -Integer.compare(this.precedence, otherModelBuildingAction.precedence);
    }
    
    
}
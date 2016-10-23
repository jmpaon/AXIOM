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
 *
 * @author juha
 */
public class ModelBuildingAction implements Comparable<ModelBuildingAction> {


    
    /**
     * Enumeration for model building action type.
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

    public ModelBuildingAction(Model model, Command command) throws AXIOMInputException {
        this.model = model;
        this.command = command;
        this.type = identifyActionType();
        this.precedence = determinePrecedence();
    };
    
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
    
    private int determinePrecedence() {
        switch(type) {
            case ADDSTATEMENT : return 5;
            case ADDOPTION    : return 3;
            case ADDIMPACT    : return 1;
            default           : return 0;
        }        
    }
    
    private ActionType identifyActionType() throws AXIOMInputException {
        switch(command.parts.get(0)) {
            case "#" : return ActionType.ADDSTATEMENT;
            case "*" : return ActionType.ADDOPTION;
            case ">" : return ActionType.ADDIMPACT;
            default  : throw new AXIOMInputException("Unknown model component type in input: " + command.text);
        }
    }
    
    private void addStatement() {
        String statementLabel = command.get(1);
        String description = "";
        boolean intervention = command.has("int");
        int timestep = command.has("TS") ? Integer.valueOf(command.right("TS")) : 0;
        
        model.add.statement(statementLabel, description, intervention, timestep);
    }
    
    private void addOption() throws LabelNotFoundException {
        String statementLabel = command.context_statementLabel;
        String optionLabel    = command.get(1);
        double apriori = Double.valueOf(command.get(2));
        model.add.option(statementLabel, optionLabel, apriori);
    }
    
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
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package axiom.reader;

import axiom.model.Model;

/**
 *
 * @author juha
 */
public class ModelBuildingAction {
    
    public final Model model;
    public final String command;

    public ModelBuildingAction(Model model, String command) {
        this.model = model;
        this.command = command;
    };    
    
    
    public void execute() {}; 
    
    public implGiver(String command) {
        
    }
    
    
    public class StatementAddition extends ModelBuildingAction {

        public StatementAddition(Model model, String command) {
            super(model, command);
        }
        
        @Override
        public void execute() {
            
        }
    }
    
    private class ImpactAddition extends ModelBuildingAction {

        public ImpactAddition(Model model, String command) {
            super(model, command);
        }

        @Override
        public void execute() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }


        
    }
    
    private class OptionAddition extends ModelBuildingAction {

        public OptionAddition(Model model, String command) {
            super(model, command);
        }

        @Override
        public void execute() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }


        
    }
    
}

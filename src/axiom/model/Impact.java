/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package axiom.model;

import axiom.probabilityAdjusters.*;

/**
 *
 * @author juha
 */
public class Impact {
    final ProbabilityAdjustmentFunction adjustmentFunction;
    final Option fromOption;
    final Option toOption;
    private boolean executed;

    Impact(ProbabilityAdjustmentFunction function, Option fromOption, Option toOption) {
        assert function != null;
        assert fromOption != null;
        assert toOption != null;
        assert fromOption != toOption;
        
        this.adjustmentFunction = function;
        this.fromOption = fromOption;
        this.toOption = toOption;
    }
    
    boolean isExecuted() {
        return executed;
    }
    
    void execute() throws ProbabilityAdjustmentException {
        assert !executed;
        
        // TODO logic
        Probability oldProbability = this.toOption.adjusted.get();
        Probability newProbability = adjustmentFunction.map(oldProbability);
        this.toOption.adjustOptionProbability(newProbability);
        
        System.out.println(this + " executed");
        System.out.println(oldProbability + " to " + newProbability);
        
        this.executed = true;
    }
    
    void reset() {
        assert executed;
        this.executed = false;
    }
    
    
    @Override
    public String toString() {
        return String.format("Impact from %s to %s by %s\n", fromOption, toOption, adjustmentFunction);
    }
    
    
    
}

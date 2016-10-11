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
    public final ProbabilityAdjustmentFunction adjustmentFunction;
    public final Option fromOption;
    public final Option toOption;
    private boolean evaluated;

    Impact(ProbabilityAdjustmentFunction function, Option fromOption, Option toOption) {
        assert function != null;
        assert fromOption != null;
        assert toOption != null;
        assert fromOption != toOption;
        
        this.adjustmentFunction = function;
        this.fromOption = fromOption;
        this.toOption = toOption;
    }
    
    boolean isEvaluated() {
        return evaluated;
    }
    
    void evaluate() {
        assert !evaluated;
        
        // TODO logic
        
        this.evaluated = true;
    }
    
    void reset() {
        assert evaluated;
        this.evaluated = false;
    }
    
    
    @Override
    public String toString() {
        return String.format("Impact from %20s to %20s by adjustment function %s\n", fromOption, toOption, adjustmentFunction);
    }
    
    
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package axiom.model;

import axiom.probabilityAdjusters.*;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author juha
 */
public class Impact {
    final ProbabilityAdjustmentFunction adjustmentFunction;
    final Option fromOption;
    final Option toOption;
    private boolean executed;

    /**
     * Constructor for <tt>Impact</tt>.
     * @param function A probability adjustment function to be used with this <tt>Impact</tt>
     * @param fromOption Option this impact belongs to: this impact is executed if <b>fromOption</b> is true
     * @param toOption Option this impact targets: the option whose probability might change as a result of this impact being executed
     */
    Impact(ProbabilityAdjustmentFunction function, Option fromOption, Option toOption) {
        assert function != null;
        assert fromOption != null;
        assert toOption != null;
        assert fromOption != toOption;
        
        this.adjustmentFunction = function;
        this.fromOption = fromOption;
        this.toOption = toOption;
    }
    
    /**
     * @return <i>true</i> if the impact has been executed 
     */
    boolean isExecuted() {
        return executed;
    }
    
    /**
     * Executes the impact by changing the probability of the target option
     * according to the {@link Impact#adjustmentFunction}.
     * @throws ProbabilityAdjustmentException 
     */
    void execute() throws ProbabilityAdjustmentException {
        assert !executed;
        if (!this.toOption.statement.isEvaluated()) {
            Probability newProbability = adjustmentFunction.map(this.toOption.adjusted.get());
            List<Probability> secondaryProbabilities = new LinkedList<>();
            for(Option o : toOption.complementOptions()) secondaryProbabilities.add(o.adjusted);
            this.toOption.adjusted.setWithSecondaryAdjustment(newProbability, secondaryProbabilities);
        }
        this.executed = true;
    }
    
    /**
     * Resets the <tt>Impact</tt>.
     */
    void reset() {
        this.executed = false;
    }
    
    
    @Override
    public String toString() {
        return String.format("Impact from %s to %s by %s\n", fromOption.getLongLabel(), toOption.getLongLabel(), adjustmentFunction);
    }
    
    
    
}

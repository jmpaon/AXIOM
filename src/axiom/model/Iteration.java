/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package axiom.model;

import axiom.probabilityAdjusters.ProbabilityAdjustmentException;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author jmpaon
 */
public class Iteration {
    
    final Model model;
    final List<Pair<Statement, Option>> activeInterventions;
    final List<Configuration> configurations;
    final public int evaluationCount;
    
    public Iteration(Model model, List<Pair<Statement, Option>> activeInterventions, int evaluationCount) throws ProbabilityAdjustmentException {
        this.model = model;
        this.activeInterventions = activeInterventions;
        this.configurations = new LinkedList<>();
        this.evaluationCount = evaluationCount;
        
        this.performIteration(this.evaluationCount);
    }
    
    private void performIteration(int evaluationCount) throws ProbabilityAdjustmentException {
        for(int i=0; i < evaluationCount ; i++) {
            this.configurations.add(this.model.evaluate(activeInterventions));
        }
    }
    
    
    public Probability getAposterioriProbability(Option o) {
        assert o.statement.model == this.model;
        int optionFrequency = 0;
        for(Configuration c : this.configurations) {
            if(c.isOptionTrue(o)) optionFrequency++;
        }
        return new Probability(optionFrequency / this.evaluationCount);
    }
    
    @Override
    public String toString() {
        throw new UnsupportedOperationException();
    }
    
    
    
}

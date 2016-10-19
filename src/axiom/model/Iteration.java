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
        return new Probability((double)optionFrequency / this.evaluationCount);
    }
    
    public Probability getAposterioriProbability(int index) {
        int optionFrequency = 0;
        for(Configuration c : this.configurations) {
            if(c.isOptionTrue(index)) optionFrequency++;
        }
        return new Probability((double)optionFrequency / this.evaluationCount);
    }
    
    public List<Pair<Option, Probability>> getAposterioriProbabilities() {
        List<Pair<Option, Probability>> aposterioris = new LinkedList<>();
        for(Option o : this.model.getOptions()) {
            Probability p = getAposterioriProbability(o);
            aposterioris.add(new Pair<>(o, p));
        }
        return aposterioris;
    }
    
    public String toString_pChanges() {
        StringBuilder sb = new StringBuilder();
        sb.append("\nActive interventions for iteration:\n").append(toString_activeInterventions());
        
        sb.append("\n");
        for(Pair<Option,Probability> p : this.getAposterioriProbabilities()) {
            if(!p.left.statement.intervention) {
                sb.append(String.format("%10s %6.6s --> %6.6s (%6.6s) \n", p.left.getLongLabel(), p.left.apriori, p.right, (p.right.toDouble() - p.left.apriori.toDouble()) ));
            } else {
                boolean b = this.activeInterventions.stream().filter(f -> f.right.equals(p.left)).findFirst().isPresent();
                sb.append(String.format("%10s (Active interv.) : %5s\n", p.left.getLongLabel(), b ? "TRUE" : "FALSE" ));
            }
        }
        return sb.toString(); 
    }
    
    @Override
    public String toString() {
        return "Iteration with active interventions " + toString_activeInterventions();
    }
    
    
    public String toString_activeInterventions() {
        StringBuilder sb = new StringBuilder();
        for(Pair<Statement,Option> activeIntervention : activeInterventions) {
            sb.append(activeIntervention.left.label).append(" <== ").append(activeIntervention.right.getLongLabel()).append("\n");
        }
        return sb.toString();
    }
    
    public String toString_configurationTable() {
        StringBuilder sb = new StringBuilder();
        for(Option o : this.model.getOptions()) {
            sb.append(String.format("%20s:", o.getLongLabel()));
            for(Configuration c : this.configurations) {
                sb.append( c.isOptionTrue(o) ? " 1 " : " 0 ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
    
    
    
}

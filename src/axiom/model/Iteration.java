/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package axiom.model;

import axiom.probabilityAdjusters.ProbabilityAdjustmentException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * <code>Iteration</code> is a set of <code>Configuration</code>s 
 * resulting from evaluations with the same intervention combination.
 * @author jmpaon
 */
public class Iteration {
    
    final Model model;
    final List<Pair<Statement, Option>> activeInterventions;
    final List<Configuration> configurations;
    final List<Pair<Option,Probability>> aposterioriProbabilities; // To store the a posteriori probability calculation result 
    final public int evaluationCount;
    
    /**
     * @param model AXIOM model 
     * @param activeInterventions List of <code>Statement</code>-<code>Option</code> <code>Pair</code>s 
     * representing the intervention statements and the options that are active 
     * (always evaluated true) in this <code>Iteration</code>.
     * @param evaluationCount The number of evaluations (and therefore <code>Configuration</code>s 
     * in this <code>Iteration</code>.
     * @throws ProbabilityAdjustmentException 
     */
    public Iteration(Model model, List<Pair<Statement, Option>> activeInterventions, int evaluationCount) throws ProbabilityAdjustmentException {
        assert evaluationCount > 0;        
        this.model = model;
        assert activeInterventions.stream().filter(i -> i.left.model==this.model && i.right.statement.model==this.model).count() == activeInterventions.size();
        assert activeInterventions.stream().allMatch(i -> i.right.statement == i.left);
        assert activeInterventions.stream().allMatch(i -> i.left.intervention);
        this.activeInterventions = activeInterventions;
        this.configurations = new LinkedList<>();
        this.evaluationCount = evaluationCount;
        this.computeIteration(this.evaluationCount);
        this.aposterioriProbabilities = this.computeAposterioriProbability();
    }
    
    /**
     * Returns the a posteriori probability of <code>Option</code> with index <b>optionIndex</b>
     * @param optionIndex Index of an <code>Option</code>
     * @return a posteriori probability of <code>Option</code> with index <b>optionIndex</b>
     */
    public Probability getAposterioriProbability(int optionIndex) {
        assert optionIndex > 0 && optionIndex <= this.model.optionCount() : "Invalid option index (" + optionIndex + ")";
        return aposterioriProbabilities.get(optionIndex).right;
    }
    
    /**
     * Returns the a posteriori probability of an <code>Option</code>.
     * @param option Option 
     * @return a posteriori probability of <code>Option</code> 
     * @throws NotFoundException 
     */
    public Probability getAposterioriProbability(Option option) throws NotFoundException {
        return aposterioriProbabilities.stream().filter(i->i.left.equals(option)).findFirst().get().right;
    }
    
    
    /**
     * Evaluate the <b>model</b> repeatedly to create the <code>Configuration</code>s of this <code>Iteration</code>.
     * @param evaluationCount How many times the model is evaluated (and how many configurations this iteration will contain)
     * @throws ProbabilityAdjustmentException 
     */
    private void computeIteration(int evaluationCount) throws ProbabilityAdjustmentException {
        while(evaluationCount-- > 0) {
            if(this.activeInterventions.isEmpty()) {
                this.configurations.add(this.model.evaluate());
            } else {
                this.configurations.add(this.model.evaluate(activeInterventions));
            }
            
        }
    }
    
    private Probability computeAposterioriProbability(Option o) {
        assert o.statement.model == this.model;
        int optionFrequency = 0;
        for(Configuration c : this.configurations) {
            if(c.isOptionTrue(o)) optionFrequency++;
        }
        return new Probability((double)optionFrequency / this.evaluationCount);
    }
    
    private Probability computeAposterioriProbability(int index) {
        int optionFrequency = 0;
        for(Configuration c : this.configurations) {
            if(c.isOptionTrue(index)) optionFrequency++;
        }
        return new Probability((double)optionFrequency / this.evaluationCount);
    }
    
    private List<Pair<Option, Probability>> computeAposterioriProbability() {
        List<Pair<Option, Probability>> aposterioris = new LinkedList<>();
        for(Option o : this.model.getOptions()) {
            Probability p = Iteration.this.computeAposterioriProbability(o);
            aposterioris.add(new Pair<>(o, p));
        }
        return aposterioris;
    }
    
    /**
     * Returns a String containing a tabulation of model options
     * and their a priori and a posteriori probabilities.
     * @return Tabulated probability changes
     */
    public String toString_probabilityChanges() {
        StringBuilder sb = new StringBuilder();
        sb.append("\nActive interventions for iteration:\n").append(toString_activeInterventions());
        
        sb.append("\n");
        for(Pair<Option,Probability> p : this.aposterioriProbabilities) {
            boolean isIntervention = p.left.statement.intervention;
            boolean noInterventions = this.activeInterventions.isEmpty();
            if( !isIntervention || noInterventions ) {
                double difference = p.right.toDouble()-p.left.apriori.toDouble();
                assert difference >= -1 && difference <= 1;
                sb.append(String.format("%10s %6.6s --> %6.6s (%2.4f) \n", p.left.getLongLabel(), p.left.apriori, p.right, difference ));
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
    
    
    /**
     * @return String containing information about the active interventions (options) for each intervention statement
     */
    public String toString_activeInterventions() {
        StringBuilder sb = new StringBuilder();
        activeInterventions.stream().forEach((activeIntervention) -> {
            sb.append(activeIntervention.left.label).append(" <== ").append(activeIntervention.right.getLongLabel()).append("\n");
        });
        return sb.toString();
    }
    
    /**
     * @return String with tabulation of the truth values of options in configurations of this iteration.
     */
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

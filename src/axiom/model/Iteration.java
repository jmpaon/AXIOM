/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package axiom.model;

import axiom.probabilityAdjusters.ProbabilityAdjustmentException;
import java.util.ArrayList;
import java.util.Iterator;
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
        return aposterioriProbabilities.get(optionIndex-1).right;
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
        int totalEvaluations = evaluationCount;
        int itNumber  = 1;
        int itCounter = 0;
        final double share = evaluationCount <= 5000 ? 100 : 2500 ;
        System.out.println(String.format("Computing iteration with %7d evaluations and with active interventions %s\n", totalEvaluations, toString_activeInterventions()));
        while(evaluationCount-- > 0) {
            itNumber++;
            if( ++itCounter >= share ) { 
                System.out.println(String.format("\t%1.0f%% computed", (double)itNumber/totalEvaluations*100));
                itCounter = 0;
                
            }
            // System.out.println(String.format("Model evaluation %7d with active interventions %s", itNumber++, toString_activeInterventions()));
            if(this.activeInterventions.isEmpty()) {
                this.configurations.add(this.model.evaluate());
            } else {
                this.configurations.add(this.model.evaluate(activeInterventions));
            }
        }
        System.out.println(String.format("\t%7d evaluations computed", totalEvaluations));
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
     * Returns a list of Option-Double pairs where the Double is the difference
     * between a posteriori probability of iteration1 for an <tt>Option</tt>
     * and a posteriori probability of iteration2 for the same <tt>Option</tt>.
     * @param iteration1 Iteration
     * @param iteration2 Compared iteration
     * @return List of Option-Double Pairs
     */
    public static List<Pair<Option, Double>> compareAposterioriProbabilities(Iteration iteration1, Iteration iteration2) {
        assert iteration1.model == iteration2.model;
        assert iteration1 != iteration2;
        List<Pair<Option,Double>> differences = new LinkedList<>();
        Iterator<Pair<Option, Probability>> iter1 = iteration1.aposterioriProbabilities.iterator();
        Iterator<Pair<Option, Probability>> iter2 = iteration2.aposterioriProbabilities.iterator();
        while(iter1.hasNext() && iter2.hasNext()) {
            Pair<Option, Probability> p1 = iter1.next();
            Pair<Option, Probability> p2 = iter2.next();
            assert p1.left == p2.left;
            differences.add(new Pair<Option,Double>(p1.left, p1.right.toDouble() - p2.right.toDouble()));
        }
        assert iter1.hasNext() == iter2.hasNext();
        return differences;
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
                sb.append(String.format("%41s\t%5.5s\t->\t%5.5s\t(%+4.3f)\n", p.left.getLongLabel(), p.left.apriori, p.right, difference ));
            } else {
                boolean b = this.activeInterventions.stream().filter(f -> f.right.equals(p.left)).findFirst().isPresent();
                sb.append(String.format("%41s as intervention:\t%5.5s\n", p.left.getLongLabel(), b ? "TRUE" : "FALSE" ));
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
        if(activeInterventions.isEmpty()) return "[No active interventions]";
        activeInterventions.stream().forEach((activeIntervention) -> {
            sb.append(String.format("[%s::%s] ", activeIntervention.left.label, activeIntervention.right.label));
            // sb.append(activeIntervention.left.label).append(" <== ").append(activeIntervention.right.getLongLabel()).append(" ");
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

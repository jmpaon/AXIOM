/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package axiom.probabilityAdjusters;



/**
 *
 * @author jmpaon
 */
public class ProbabilityAdjusterFactory {
    
    /**
     * 
     * @return
     * @throws ProbabilityAdjustmentException 
     */
    public NameProbabilityAdjuster createDefaultNameProbabilityAdjuster() throws ProbabilityAdjustmentException {

        LinearProbabilityAdjustmentFunction fm6, fm5, fm4, fm3, fm2, fm1, f0, fp1, fp2, fp3, fp4, fp5, fp6;
        
        fm6 = new LinearProbabilityAdjustmentFunction(-1);
        fm5 = new LinearProbabilityAdjustmentFunction(-0.95);
        fm4 = new LinearProbabilityAdjustmentFunction(-0.65);
        fm3 = new LinearProbabilityAdjustmentFunction(-0.45);
        fm2 = new LinearProbabilityAdjustmentFunction(-0.30);
        fm1 = new LinearProbabilityAdjustmentFunction(-0.15);
        f0  = new LinearProbabilityAdjustmentFunction( 0.00);
        fp1 = new LinearProbabilityAdjustmentFunction( 0.15);
        fp2 = new LinearProbabilityAdjustmentFunction( 0.30);
        fp3 = new LinearProbabilityAdjustmentFunction( 0.45);
        fp4 = new LinearProbabilityAdjustmentFunction( 0.65);
        fp5 = new LinearProbabilityAdjustmentFunction( 0.95);
        fp6 = new LinearProbabilityAdjustmentFunction( 1);
        
        NameProbabilityAdjuster npa = new NameProbabilityAdjuster();
        npa.addAdjustmentFunction(-6, fm6);
        npa.addAdjustmentFunction(-5, fm5);
        npa.addAdjustmentFunction(-4, fm4);
        npa.addAdjustmentFunction(-3, fm3);
        npa.addAdjustmentFunction(-2, fm2);
        npa.addAdjustmentFunction(-1, fm1);
        npa.addAdjustmentFunction( 0, f0);
        npa.addAdjustmentFunction( 1, fp1);
        npa.addAdjustmentFunction( 2, fp2);
        npa.addAdjustmentFunction( 3, fp3);
        npa.addAdjustmentFunction( 4, fp4);
        npa.addAdjustmentFunction( 5, fp5);
        npa.addAdjustmentFunction( 6, fp6);
        
        return npa;
        
    }
}

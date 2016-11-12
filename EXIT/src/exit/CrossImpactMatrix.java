/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exit;

/**
 *
 * @author juha
 */
public class CrossImpactMatrix extends SquareMatrix{

    public CrossImpactMatrix(int varCount, boolean onlyIntegers, String[] names, double[] values) {
        super(varCount, onlyIntegers, names, values);
    }

    public CrossImpactMatrix(int varCount, boolean onlyIntegers, String[] names) {
        super(varCount, onlyIntegers, names);
    }

    public CrossImpactMatrix(int varCount, boolean onlyIntegers) {
        super(varCount, onlyIntegers);
    }

    public CrossImpactMatrix(int varCount, String[] names) {
        super(varCount, names);
    }

    public CrossImpactMatrix(int varCount) {
        super(varCount);
    }

    public CrossImpactMatrix(boolean onlyIntegers, String[] names, double[][] values) {
        super(onlyIntegers, names, values);
    }
    
    
    
}

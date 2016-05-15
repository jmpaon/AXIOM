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
    public final ProbabilityAdjustmentFunction f;
    public final Option owner;
    public final Option target;

    Impact(ProbabilityAdjustmentFunction f, Option owner, Option target) {
        this.f = f;
        this.owner = owner;
        this.target = target;
    }
    
    
    
}

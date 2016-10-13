/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package axiom.model;

import java.util.List;

/**
 *
 * @author jmpaon
 */
public class Iteration {
    Model model;
    List<Option> activeInterventions;
    
    public Probability getAposterioriProbability(Option o) {
        assert o.statement.model == this.model;
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package axiom;

import axiom.model.Pair;
import java.util.LinkedList;
import java.util.List;


/**
 *
 * @author jmpaon
 */
public class TestInputGenerator {

    public String generateInput(int statements) {
        
        int statementCount = 1;
        String currStatement = "";
        String currOption = "";
        StringBuilder sb = new StringBuilder();
        
        for(int i=0;i<statements;i++) {
            currStatement = "Statement" + statementCount++;
            boolean intervention = Math.random() > 0.95 ? true : false;
            sb.append(statementInput(currStatement, intervention));
            
            for(int ii=1;ii<=4;ii++) {
                currOption = "Option" + ii;
                double apriori = Math.random();
                sb.append(optionInput(currOption, apriori));
                for(int iii=1;iii<=Math.random()*4 +1 ;iii++) {
                    int targetIndex = (int)(Math.random() * statements);
                    while(targetIndex == i) targetIndex = (int)(Math.random() * statements);
                    String targetStatement = "Statement" + String.valueOf(targetIndex);
                    String targetOption = "Option" + String.valueOf((int)(Math.random()*3 + 1));
                    
                    int adj_i = (int)(Math.random()*10 - 5 );
                    String adj_name = adj_i == 0 ? String.valueOf(0) : adj_i < 0 ? String.valueOf(adj_i) : "+"+String.valueOf(adj_i);
                    sb.append( impactInput(targetStatement, targetOption, adj_name) );
                }
            }
        }
        
        return sb.toString();
        
                
    }
    
    

    public String statementInput(String label, boolean intervention) {
        return String.format("# %s%s\n", label, intervention ? " INT" : "");
    }
    
    public String optionInput(String label, double apriori) {
        assert apriori >= 0 && apriori <= 1;
        return String.format("\t* %s %f\n", label, apriori);
    }
    

    
    public String impactInput(String targetStatement, String targetOption, String adjustmentFunction) {
        return String.format("\t\t> %s %s %s\n", targetStatement, targetOption, adjustmentFunction);
    }
    
    
}


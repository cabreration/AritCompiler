/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Instructions;

import Expressions.Expression;
import Symbols.SymbolsTable;

/**
 *
 * @author jacab
 */
public class Barplot implements Instruction {
    
    private Expression h;
    private Expression xLabel;
    private Expression yLabel;
    private Expression title;
    private Expression names;

    @Override
    public Object process(SymbolsTable env) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}

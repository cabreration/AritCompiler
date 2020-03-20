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
public class NumberFunction implements Instruction {

    private Expression number;
    private int type; // 1 trunk, 2 round
    private int line;
    private int column;

    public NumberFunction(Expression number, int type, int line, int column) {
        this.number = number;
        this.type = type;
        this.line = line;
        this.column = column;
    }
    
    @Override
    public Object process(SymbolsTable env) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}

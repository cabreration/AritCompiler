/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Instructions;

import Expressions.Expression;
import Symbols.Address;
import Symbols.SymbolsTable;

/**
 *
 * @author jacab
 */
public class StructureAsignment implements Instruction {

    private String id;
    private int line;
    private int column;
    private Address[] addresses;
    private Expression expression;

    public StructureAsignment(String id, int line, int column, Address[] addresses, Expression expression) {
        this.id = id;
        this.line = line;
        this.column = column;
        this.addresses = addresses;
        this.expression = expression;
    }
    
    @Override
    public Object process(SymbolsTable env) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}

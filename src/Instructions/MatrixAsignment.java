/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Instructions;

import Expressions.Expression;
import Expressions.MatrixAccess;
import Symbols.SymbolsTable;

/**
 *
 * @author jacab
 */
public class MatrixAsignment implements Instruction {

    private MatrixAccess access;
    private Expression exp;

    public MatrixAsignment(MatrixAccess access, Expression exp) {
        this.access = access;
        this.exp = exp;
    }
    
    @Override
    public Object process(SymbolsTable env) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Instructions;

import Expressions.Expression;
import Symbols.SymbolsTable;
import java.util.ArrayList;

/**
 *
 * @author jacab
 */
public class DoWhile_Sentence implements Instruction{
    
    private Expression condition;
    private ArrayList<Instruction> sentences;
    private int line;
    private int column;

    public DoWhile_Sentence(Expression condition, ArrayList<Instruction> sentences, int line, int column) {
        this.condition = condition;
        this.sentences = sentences;
        this.line = line;
        this.column = column;
    }

    @Override
    public Object process(SymbolsTable env) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}

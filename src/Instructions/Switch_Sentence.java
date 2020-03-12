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
public class Switch_Sentence implements Instruction {
    private Expression condition;
    private int line;
    private int column;
    private ArrayList<Case_Component> cases;

    public Switch_Sentence(Expression condition, int line, int column, ArrayList<Case_Component> cases) {
        this.condition = condition;
        this.line = line;
        this.column = column;
        this.cases = cases;
    }

    @Override
    public Object process(SymbolsTable env) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}

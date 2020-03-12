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
public class Case_Component implements Instruction {
    private Expression kase;
    private ArrayList<Instruction> sentences;
    private int line;
    private int column;
    private Object original;

    public Case_Component(Expression kase, ArrayList<Instruction> sentences, int line, int column) {
        this.kase = kase;
        this.sentences = sentences;
        this.line = line;
        this.column = column;
        this.original = null;
    }

    public Case_Component(ArrayList<Instruction> sentences, int line, int column) { /* THIS IS THE DEFAULT */
        this.sentences = sentences;
        this.line = line;
        this.column = column;
        this.original = null;
    }

    public void setOriginal(Object original) {
        this.original = original;
    }
    
    @Override
    public Object process(SymbolsTable env) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Instructions;

import APIServices.CompileError;
import Symbols.SymbolsTable;
import aritcompiler.Singleton;

/**
 *
 * @author jacab
 */
public class Continue_Sentence implements Instruction {

    private int line;
    private int column;

    public Continue_Sentence(int line, int column) {
        this.line = line;
        this.column = column;
    }
    
    @Override
    public Object process(SymbolsTable env) {
        if (env.getType().contains("loop"))
            return this;
        
        Singleton.insertError(new CompileError("Semantico", "La sentencia continue no tiene sentido dentro del contexto actual", this.line, this.column));
        return null;
    }
    
}

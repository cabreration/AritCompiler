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
public class Break_Sentence implements Instruction {

    private int line;
    private int column;

    public Break_Sentence(int line, int column) {
        this.line = line;
        this.column = column;
    }
    
    @Override
    public Object process(SymbolsTable env) {
        /*if (env.getType().equals("while") || env.getType().equals("case") || env.getType().equals("for") || env.getType().equals("do_while")
                || env.getType().equals("fwhile") || env.getType().equals("fcase") || env.getType().equals("ffor") || env.getType().equals("fdo_while")) 
            return this;
        
        Singleton.insertError(new CompileError("Semantico", "La sentencia break solo puede ser usada dentro de una sentencia de repeticion", this.line, this.column));
        return null;*/
        return this;
    }
    
}

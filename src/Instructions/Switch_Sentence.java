/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Instructions;

import APIServices.CompileError;
import Expressions.Atomic;
import Expressions.Expression;
import Symbols.SymbolsTable;
import Symbols.Vector;
import aritcompiler.Singleton;
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
        Object val = condition.process(env);
        
        if (val instanceof CompileError) {
            if (((CompileError)val).getRow() == 0 && ((CompileError)val).getColumn() == 0) {
                ((CompileError)val).setRow(this.line);
                ((CompileError)val).setColumn(this.column);
            }
            
            Singleton.insertError((CompileError)val);
            return null;
        }
        
        if (val instanceof Atomic) {
            if (((Atomic)val).getType() == Atomic.Type.IDENTIFIER) {
                String id = String.valueOf(((Atomic)val).getValue());
                int line = ((Atomic)val).getLine();
                int col = ((Atomic)val).getColumn();
                
                val = env.getSymbol(id);
                if (val == null) {
                    Singleton.insertError(new CompileError("Semantico", "La variable '" + id + "' no existe en el contexto actual", line, col));
                    return null;
                }
            }
        }
        
        if (val instanceof Vector) {
            // Retornamos el primer valor
            val = ((ArrayList<Atomic>)(((Vector)val).getValue())).get(0);
        }
        /* MATRIX, LIST, ARRAY - all will return the first value */
        
        for (Case_Component kase : this.cases) {
            kase.setOriginal(val);
            Object r = kase.process(env);
            
            if (r instanceof CompileError) {
                Singleton.insertError((CompileError)r);
                continue;
            }
            
            if (r != null) {
                if (r instanceof Break_Sentence)
                    break;
                //else if (r instanceof Return)
            }
        }
        
        return null;
    }
}

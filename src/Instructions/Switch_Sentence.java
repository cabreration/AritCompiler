/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Instructions;

import APIServices.CompileError;
import Expressions.Atomic;
import Expressions.Expression;
import Symbols.List;
import Symbols.Matrix;
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
        
        if (val instanceof Matrix)
            val = ((Atomic[][])((Matrix)val).getValue())[0][0];
        
        while (val instanceof List)
            val = ((ArrayList<Object>)((List)val).getValue()).get(0);
        
        if (val instanceof Vector) {
            // Retornamos el primer valor
            val = ((ArrayList<Atomic>)(((Vector)val).getValue())).get(0);
        }
        /* ARRAY - all will return the first value */
        
        for (Case_Component kase : this.cases) {
            kase.setOriginal(val);
            String name = "case";
            if (env.getType().contains("function"))
                name += "-function";
            if (env.getType().contains("loop"))
                name += "-loop";
            SymbolsTable local = new SymbolsTable(name, env);
            Object r = kase.process(env);
            
            if (r instanceof CompileError) {
                Singleton.insertError((CompileError)r);
                continue;
            }
            
            if (r != null) {
                if (r instanceof Break_Sentence) {
                    env.update(local);
                    break;
                }
                else if (r instanceof Continue_Sentence || r instanceof Return_Sentence) {
                    env.update(local);
                    return r;
                }
            }
            env.update(local);
        }
        
        return null;
    }
}

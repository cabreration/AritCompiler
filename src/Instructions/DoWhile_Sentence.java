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
        boolean cond = true;
        
        do {
            String name = "loop";
            if (env.getType().contains("function"))
                name += "-function";
            SymbolsTable local = new SymbolsTable(name, env);
            for (Instruction ins : this.sentences) {
                Object r = ins.process(local);
                
                if (r != null) {
                    if (r instanceof Break_Sentence) {
                        cond = false;
                        env.update(local);
                        break;
                    }
                    else if (r instanceof Continue_Sentence) {
                        env.update(local);
                        break;
                    }           
                    else if (r instanceof Return_Sentence) {
                        env.update(local);
                        return r;
                    }
                    else if (r instanceof CompileError) {
                        Singleton.insertError((CompileError)r);
                    }
                }
            }
            env.update(local);
            
            Object condit = determine(env);
            if (condit instanceof CompileError) {
                Singleton.insertError((CompileError)condit);
                return null;
            }
            cond = ((Boolean)condit).booleanValue();
        } while (cond); 
        return null;
    }
    
    private Object determine(SymbolsTable env) {
        Object val = condition.process(env);
        
        if (val == null)
            return new CompileError("Semantico", "Puntero a valor nulo", this.line, this.column); // Esto no deberia pasar
            
        if (val instanceof CompileError)
            return val;
        
        boolean cond = false;
        if (val instanceof Atomic) {
            if (((Atomic)val).getType() == Atomic.Type.IDENTIFIER) {
                String id = String.valueOf(((Atomic)val).getValue());
                int line = ((Atomic)val).getLine();
                int col = ((Atomic)val).getColumn();
                val = env.getSymbol(id, line);
                
                if (val == null) {
                     return new CompileError("Semantico", "La variable '" + id + "' no existe en el contexto actual", line, col);
                }
            }
        }
        
        if (val instanceof Matrix)
            val = ((Atomic[][])((Matrix)val).getValue())[0][0];
        
        while (val instanceof List) {
            val = ((ArrayList<Object>)((List)val).getValue()).get(0);
        }
        
        if (val instanceof Vector) {
            Atomic bool = ((ArrayList<Atomic>)(((Vector)val).getValue())).get(0);
            if (bool.getType() == Atomic.Type.BOOLEAN)
                cond = ((Boolean)bool.getValue()).booleanValue();
            else {
                return new CompileError("Semantico", "Las condiciones unicamente pueden ser evaluadas si son de tipo booleano", this.line, this.column);
            }
        }
        else if (val instanceof Atomic) {
            Atomic bool = (Atomic)val;
            if (bool.getType() == Atomic.Type.BOOLEAN) 
                cond = ((Boolean)bool.getValue()).booleanValue();
            else {
                return new CompileError("Semantico", "Las condiciones unicamente pueden ser evaluadas si son de tipo booleano", this.line, this.column);
            }
        }
        /* MATRIX, ARRAY */
        return Boolean.valueOf(cond);
    }
}

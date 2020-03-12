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
public class While_Sentence implements Instruction {

    private Expression condition;
    private ArrayList<Instruction> sentences;
    private int line;
    private int column;

    public While_Sentence(Expression condition, ArrayList<Instruction> sentences, int line, int column) {
        this.condition = condition;
        this.sentences = sentences;
        this.line = line;
        this.column = column;
    }
    
    @Override
    public Object process(SymbolsTable env) {     
        Object condit = determine(env);
        if (condit instanceof CompileError) {
            Singleton.insertError((CompileError)condit);
            return null;
        }
        boolean cond = ((Boolean)condit).booleanValue();
        
        while (cond) {
            condit = determine(env);
            if (condit instanceof CompileError) {
                Singleton.insertError((CompileError)condit);
                return null;
            }
            cond = ((Boolean)condit).booleanValue();
            
            SymbolsTable local = new SymbolsTable("loop", env);
            for (Instruction ins : this.sentences) {
                Object r = ins.process(local);
                
                if (r != null) {
                    if (r instanceof Break_Sentence) {
                        cond = false;
                        break;
                    }
                    else if (r instanceof Continue_Sentence)
                        continue;
                    else {
                        // Este seria para el return
                    }
                }
            }
        }
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
                val = env.getSymbol(id);
                
                if (val == null) {
                     return new CompileError("Semantico", "La variable '" + id + "' no existe en el contexto actual", line, col);
                }
            }
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
        /* MATRIX, LIST, ARRAY */
        return Boolean.valueOf(cond);
    }
    
}

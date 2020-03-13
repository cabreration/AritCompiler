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
public class If_Sentence implements Instruction {
    private Expression condition;
    private ArrayList<Instruction> sentences;
    private If_Sentence elseSentence;
    private int line;
    private int column;
    
    public If_Sentence(Expression condition, ArrayList<Instruction> sentences, int line, int column) {
        /* No hay else */
        this.condition = condition;
        this.sentences = sentences;
        this.elseSentence = null;
        this.line = line;
        this.column = column;
    }
    
    public If_Sentence(Expression condition, ArrayList<Instruction> sentences, If_Sentence elseSentence, int line, int column) {
        /* Si hay else */
        this.condition = condition;
        this.sentences = sentences;
        this.elseSentence = elseSentence;
        this.line = line;
        this.column = column;
    }
    
    public If_Sentence(ArrayList<Instruction> sentences, int line, int column) {
        /* Este es un else */
        this.sentences = sentences;
        this.condition = null;
        this.elseSentence = null;
        this.line = line;
        this.column = column;
    }

    @Override
    public Object process(SymbolsTable env) {
        if (this.condition == null) {
            String name = "if";
            if (env.getType().contains("loop"))
                name += "-loop";
            if (env.getType().contains("function"))
                name += "-function";
            
            SymbolsTable local = new SymbolsTable(name, env);
            for (Instruction ins : this.sentences) {
                Object r = ins.process(local);
                
                if (r != null) {
                    if ((r instanceof Break_Sentence || r instanceof Continue_Sentence) && name.contains("loop")) {
                        return r;
                    }
                    else {
                        /// Return
                    }
                }
            }
            env.update(local);
            return null;
        }
        
        
        Object val = condition.process(env);
        
        if (val == null)
            return null; // Esto no deberia pasar
            
        if (val instanceof CompileError)
            return val;
        
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
        
        boolean cond = false;
        if (val instanceof Vector) {
            Atomic bool = ((ArrayList<Atomic>)(((Vector)val).getValue())).get(0);
            if (bool.getType() == Atomic.Type.BOOLEAN)
                cond = ((Boolean)bool.getValue()).booleanValue();
            else {
                Singleton.insertError(new CompileError("Semantico", "Las condiciones unicamente pueden ser evaluadas si son de tipo booleano", this.line, this.column));
                return null;
            }
        }
        else if (val instanceof Atomic) {
            Atomic bool = (Atomic)val;
            if (bool.getType() == Atomic.Type.BOOLEAN) 
                cond = ((Boolean)bool.getValue()).booleanValue();
            else {
                Singleton.insertError(new CompileError("Semantico", "Las condiciones unicamente pueden ser evaluadas si son de tipo booleano", this.line, this.column));
                return null;
            }
        }
        /* MATRIX, LIST, ARRAY */
        String name = "if";
        if (env.getType().contains("loop"))
            name += "-loop";
        if (env.getType().contains("function"))
            name += "-function";
        SymbolsTable local = new SymbolsTable(name, env);
        
        if (cond) {
            for (Instruction ins : this.sentences) {
                Object r = ins.process(local);
                
                if (r != null) {
                    if ((r instanceof Break_Sentence || r instanceof Continue_Sentence) && name.contains("loop")) {
                        env.update(local);
                        return r;
                    }
                    /*else if (r instanceof Return_Sentence) {
                        env.update(local);
                        /// Return
                    }*/
                }
            }
            env.update(local);
        }
        else {
            if (elseSentence != null) {
                Object r = elseSentence.process(env);
                return r;
            }
        }
        return null;
    }
}

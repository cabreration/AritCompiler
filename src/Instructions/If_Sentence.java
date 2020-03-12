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
            SymbolsTable local = new SymbolsTable("if", env);
            for (Instruction ins : this.sentences) {
                ins.process(local);
            }
            
            /* TODAVIA FALTA AGREGAR LOS RETURN */
            return null;
        }
        
        
        Object val = condition.process(env);
        
        if (val == null)
            return null; // Esto no deberia pasar
            
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
                    Singleton.insertError(new CompileError("Semantico", "La variable '" + id + "' no existe en el contexto actual", line, col));
                    return null;
                }
            }
        }
        
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
        
        SymbolsTable local = new SymbolsTable("if", env);
        if (cond) {
            for (Instruction ins : this.sentences) {
                Object r = ins.process(local);
            }
        }
        else {
            if (elseSentence != null)
                elseSentence.process(env);
        }
         /* TODAVIA NO HE AGREGADO EL RETURN */
        return null;
    }
}

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
import java.util.ArrayList;

/**
 *
 * @author jacab
 */
public class For_Sentence implements Instruction{
    private int line;
    private int column;
    private String i;
    private Expression condition;
    private ArrayList<Instruction> sentences;

    public For_Sentence(int line, int column, String i, Expression condition, ArrayList<Instruction> sentences) {
        this.line = line;
        this.column = column;
        this.i = i;
        this.condition = condition;
        this.sentences = sentences;
    }

    @Override
    public Object process(SymbolsTable env) {
        Object val = condition.process(env);
        
        if (val == null)
            return new CompileError("Semantico", "Puntero a valor nulo", this.line, this.column); // Esto no deberia pasar
            
        if (val instanceof CompileError) {
            if (((CompileError)val).getRow() == 0 && ((CompileError)val).getColumn() == 0) {
                ((CompileError)val).setRow(this.line);
                ((CompileError)val).setColumn(this.column);
            }
            return val;
        }
        
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
            for (Atomic atom : (ArrayList<Atomic>)((Vector)val).getValue()) {
                SymbolsTable local = new SymbolsTable("loop", env);
                Vector vec = new Vector(atom);
                local.updateSymbol(i, vec);
                for (Instruction ins: this.sentences) {
                    Object r = ins.process(local);
                    if (r != null) {
                        if (r instanceof Break_Sentence) { //Falta el return
                            env.update(local);
                            return r;
                        }
                        else if (r instanceof Continue_Sentence) {
                            break;
                        }
                    }
                }
                env.update(local);
            }
        }
        else if (val instanceof Atomic) {
            SymbolsTable local = new SymbolsTable("loop", env);
            Vector vec = new Vector((Atomic)val);
            local.updateSymbol(i, vec);
            for (Instruction ins: this.sentences) {
                Object r = ins.process(local);
                if (r != null) {
                    if (r instanceof Break_Sentence || r instanceof Continue_Sentence) { //Falta el return
                        env.update(local);
                        return r;
                    }
                }
            }
            env.update(local);
        }
        /* MATRIX, LIST, ARRAY */
        return null;
    }
}

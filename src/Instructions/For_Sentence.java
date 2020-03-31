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
import Symbols.Symbol;
import Symbols.SymbolsTable;
import Symbols.Vector;
import aritcompiler.Singleton;
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
                val = env.getSymbol(id, line);
                
                if (val == null) {
                     return new CompileError("Semantico", "La variable '" + id + "' no existe en el contexto actual", line, col);
                }
            }
        }
        
        
        if (val instanceof Matrix) {
            try {
                Object r = forMatrix((Matrix)val, env);
                return r;
            }
            catch (Exception e) {
                Singleton.insertError(new CompileError("Semantico", "Es imposible modificar un elemento sobre el cual se esta iterando", this.line, this.column));
                return null;
            }
        }
        if (val instanceof List) {
            try { 
                Object r = forList((List)val, env);
                return r;
            }
            catch (Exception e) {
                Singleton.insertError(new CompileError("Semantico", "Es imposible modificar un elemento sobre el cual se esta iterando", this.line, this.column));
                return null;
            }
        }
        else if (val instanceof Vector) {
            try { 
                Object r = forVector((Vector)val, env);
                return r;
            }
            catch (Exception e) {
                Singleton.insertError(new CompileError("Semantico", "Es imposible modificar un elemento sobre el cual se esta iterando", this.line, this.column));
                return null;
            }
        }
        else if (val instanceof Atomic) {
            return forAtomic((Atomic)val, env);
        }
        /* ARRAY */
        return null;
    }
    
    private Object forAtomic(Atomic val, SymbolsTable env) {
        String name = "loop";
        if (env.getType().contains("function"))
            name += "-function";
        SymbolsTable local = new SymbolsTable(name, env);
        Vector vec = new Vector(val);
        local.updateSymbol(i, vec);
        for (Instruction ins: this.sentences) {
            Object r = ins.process(local);
            if (r != null) {
                if (r instanceof Break_Sentence || r instanceof Continue_Sentence) { 
                    env.update(local);
                    return null;
                }
                if (r instanceof Return_Sentence) {
                    env.update(local);
                    return r;
                }
                if (r instanceof CompileError) {
                    Singleton.insertError((CompileError)r);
                }
            }
        }
        env.update(local);
        return null;
    }
    
    private Object forVector(Vector val, SymbolsTable env) {
        int size = ((ArrayList<Atomic>)val.getValue()).size();
        for (int j = 0; j < size; j++) {
            Atomic atom = ((ArrayList<Atomic>)val.getValue()).get(j);
            String name = "loop";
            if (env.getType().contains("function"))
                name += "-function";
            SymbolsTable local = new SymbolsTable(name, env);
            Vector vec = new Vector(atom);
            local.updateSymbol(i, vec);
            for (Instruction ins: this.sentences) {
                Object r = ins.process(local);
                if (r != null) {
                    if (r instanceof Break_Sentence) {
                        env.update(local);
                        // actualizar el valor del vector
                        return null;
                    }
                    else if (r instanceof Continue_Sentence) {
                        env.update(local);
                        break;
                    }
                    else if (r instanceof Return_Sentence) {
                        env.update(local);
                        // actualizar el valor del vector
                        return r;
                    }
                    else if (r instanceof CompileError) {
                        Singleton.insertError((CompileError)r);
                    }
                }
            }
            // Aqui hay que actualizar el valor del vector
            env.update(local);
        }
        return null;
    }
    
    private Object forList(List val, SymbolsTable env) {
        int size = ((ArrayList<Object>)val.getValue()).size();
        for (int j = 0; j < size; j++) {
            Object vecList = ((ArrayList<Object>)val.getValue()).get(j);
            String name = "loop";
            if (env.getType().contains("function"))
                name += "-function";
            SymbolsTable local = new SymbolsTable(name, env);
            local.updateSymbol(i, (Symbol)vecList);
            for (Instruction ins: this.sentences) {
                Object r = ins.process(local);
                if (r != null) {
                    if (r instanceof Break_Sentence) {
                        env.update(local);
                        return null;
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
        }
        return null;
    }
    
    private Object forMatrix(Matrix val, SymbolsTable env) {
        for (Atomic[] row : ((Atomic[][])(val.getValue()))) {
            for (Atomic atom : row) {
                String name = "loop";
                if (env.getType().contains("function"))
                    name += "-function";
                SymbolsTable local = new SymbolsTable(name, env);
                Vector vec = new Vector(atom);
                local.updateSymbol(i, vec);
                for (Instruction ins: this.sentences) {
                    Object r = ins.process(local);
                    if (r != null) {
                        if (r instanceof Break_Sentence) {
                            env.update(local);
                            return null;
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
            }
        }
        return null;
    }
}

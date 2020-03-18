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
public class List_Function implements Instruction {
    private int line;
    private int column;
    private ArrayList<Object> params;

    public List_Function(int line, int column, ArrayList<Object> params) {
        this.line = line;
        this.column = column;
        this.params = params;
    }

    @Override
    public Object process(SymbolsTable env) {
        ArrayList<Object> elements = new ArrayList<Object>();
        for (Object param : this.params) {
            Object val = ((Expression)param).process(env);
            
            if (val instanceof CompileError) {
                Singleton.insertError((CompileError)val);
                return null;
            }
            
            if (val instanceof Atomic) {
                if (((Atomic)val).getType() == Atomic.Type.IDENTIFIER) {
                    String id = String.valueOf(((Atomic)val).getValue());
                    int line = ((Atomic)val).getLine();
                    int column = ((Atomic)val).getColumn();
                    
                    val = env.getSymbol(id);
                    if (val == null) {
                        Singleton.insertError(new CompileError("Semantico", "La variable '" + id + "' no existe en el contexto actual", line, column));
                        return null;
                    }
                    
                    if (val instanceof Matrix) {
                        Singleton.insertError(new CompileError("Semantico", "No es posible incluir matrices en listas", line, column));
                        return null;
                    }
                    //if (val instanceof Array) {} // Error
                }
                else {
                    val = new Vector((Atomic)val);
                }
            }
            
            elements.add(val);
        }
        
        return new List(elements);
    }
    
    
}

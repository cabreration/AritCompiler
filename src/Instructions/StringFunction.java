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
import java.util.ArrayList;

/**
 *
 * @author jacab
 */
public class StringFunction implements Instruction {
    private int type; // 1 length, 2 remove, 3 lower case, 4 upper case 
    private Expression str;
    private Expression regex;
    private int line;
    private int column;

    public StringFunction(int type, Expression str, int line, int column) {
        this.type = type;
        this.str = str;
        this.line = line;
        this.column = column;
    }

    public StringFunction(Expression str, Expression regex, int line, int column) {
        this.str = str;
        this.regex = regex;
        this.line = line;
        this.column = column;
        this.type = 2;
    }

    @Override
    public Object process(SymbolsTable env) {
        Object first = getString(env, this.str);
        
        if (first == null)
            return null;
        if (first instanceof CompileError)
            return first;
        
        if (this.type == 1) 
            return strLength(first);
        else if (this.type == 2) {
            Object second = getString(env, this.regex);
            
            if (second == null)
                return null;
            if (second instanceof CompileError)
                return second;
            
            return removeFromString(first, second);
        }
        else if (this.type == 3) 
            return toLowerCase(first);
        else 
            return toUpperCase(first);
    }
    
    private Object getString(SymbolsTable env, Expression exp) {
        Object original = exp.process(env);
        
        if (original == null)
            return null;
        
        if (original instanceof CompileError) {
            if (((CompileError)original).getRow() == 0 && ((CompileError)original).getColumn() == 0) {
                ((CompileError)original).setRow(this.line);
                ((CompileError)original).setColumn(this.column);
            }
            return original;
        }
        
        if (original instanceof Atomic) {
            if (((Atomic)original).getType() == Atomic.Type.IDENTIFIER) {
                String id = String.valueOf(((Atomic)original).getValue());
                int line = ((Atomic)original).getLine();
                int column = ((Atomic)original).getColumn();
                
                original = env.getSymbol(id, line);
                if (original == null)
                    return new CompileError("Semantico", "La variable '" + id + "' no existe en el contexto actual", line, column);
            }
        }
        
        if (original instanceof Matrix)
            original = ((Atomic[][])((Matrix)original).getValue())[0][0];
        
        while (original instanceof List) 
            original = ((ArrayList<Object>)((List)original).getValue()).get(0);
        
        if (original instanceof Vector) {
            if (((Vector)original).getSize() != 1)
                return new CompileError("Semantico", "Mas argumentos de los esperados para la funcion de cadena", this.line, this.column);
            
            original = ((ArrayList<Atomic>)((Vector)original).getValue()).get(0);
        }
            
        if (original instanceof Atomic) {
            if (((Atomic)original).getType() != Atomic.Type.STRING)
                return new CompileError("Semantico", "No puede realizarse la operacion sobre valores que no sean cadenas", this.line, this.column);
        }
        
        return original;
    }

    private Object strLength(Object first) {
        String mod = String.valueOf(((Atomic)first).getValue());
        int length = mod.length();
        
        return new Atomic(Atomic.Type.INTEGER, length);
    }

    private Object toLowerCase(Object first) {
        String mod = String.valueOf(((Atomic)first).getValue());
        
        return new Atomic(Atomic.Type.STRING, mod.toLowerCase());
    }

    private Object toUpperCase(Object first) {
        String mod = String.valueOf(((Atomic)first).getValue());
        
        return new Atomic(Atomic.Type.STRING, mod.toUpperCase());
    }

    private Object removeFromString(Object first, Object second) {
        String original = String.valueOf(((Atomic)first).getValue());
        String remover = String.valueOf(((Atomic)second).getValue());
        
        original = original.replace(remover, "");
        
        return new Atomic(Atomic.Type.STRING, original);
    }
}

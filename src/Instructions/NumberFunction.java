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
public class NumberFunction implements Instruction {

    private Expression number;
    private int type; // 1 trunk, 2 round
    private int line;
    private int column;

    public NumberFunction(Expression number, int type, int line, int column) {
        this.number = number;
        this.type = type;
        this.line = line;
        this.column = column;
    }
    
    @Override
    public Object process(SymbolsTable env) {
        
        Object first = getNumber(env, this.number);
        
        if (first == null)
            return null;
        if (first instanceof CompileError)
            return first;
        
        if (this.type == 1)
            return trunk(first);
        else
            return round(first);
    }
    
    private Object getNumber(SymbolsTable env, Expression exp) {
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
            if (((Atomic)original).getType() != Atomic.Type.NUMERIC && ((Atomic)original).getType() != Atomic.Type.INTEGER)
                return new CompileError("Semantico", "No puede realizarse la operacion sobre valores que no sean cadenas", this.line, this.column);
        }
        
        return original;
    }

    private Object trunk(Object first) {
        double doub = ((Number)((Atomic)first).getValue()).doubleValue();
        int nu = (int)(doub/1);
    
        return new Atomic(Atomic.Type.INTEGER, Integer.valueOf(nu));
    }

    private Object round(Object first) {
        float doub = ((Number)((Atomic)first).getValue()).floatValue();
        int nu = Math.round(doub);
        
        return new Atomic(Atomic.Type.INTEGER, Integer.valueOf(nu));
    }
    
}

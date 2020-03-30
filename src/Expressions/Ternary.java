/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Expressions;

import APIServices.CompileError;
import Symbols.List;
import Symbols.Matrix;
import Symbols.SymbolsTable;
import Symbols.Vector;
import java.util.ArrayList;

/**
 *
 * @author jacab
 */
public class Ternary implements Expression {

    private Expression arg1;
    private Expression arg2;
    private Expression arg3;
    private int line;
    private int column;

    public Ternary(Expression arg1, Expression arg2, Expression arg3, int line, int column) {
        this.arg1 = arg1;
        this.arg2 = arg2;
        this.arg3 = arg3;
        this.line = line;
        this.column = column;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }
    
    @Override
    public Object process(SymbolsTable env) {
        Object atom1 = arg1.process(env);
        
        if (atom1 == null)
            return null;
        
        if (atom1 instanceof CompileError)
            return atom1;
        
        /*if (!(atom1 instanceof Value) || !(atom2 instanceof Value) || !(atom3 instanceof Value)) {
            throw new Error("Esto no deberia estar pasando Binario");
        }*/
        
        if (atom1 instanceof Atomic) {
            if (((Atomic)atom1).getType() == Atomic.Type.IDENTIFIER) {
                String id = String.valueOf(((Atomic) atom1).getValue());
                atom1 = env.getSymbol(id, line);
                
                if (atom1 == null)
                    return new CompileError("Semantico", "La variable '" + id + "' no existe en el contexto actual", line, column);
            }
        }
        
        if (!(atom1 instanceof Atomic)) {
            if (atom1 instanceof Vector) {
                atom1 = ((ArrayList<Atomic>)((Vector)atom1).getValue()).get(0);
            }
            else if (atom1 instanceof Matrix) {
                atom1 = ((Atomic[][])((Matrix)atom1).getValue())[0][0];
            }
            
            while (atom1 instanceof List) {
                atom1 = ((ArrayList<Object>)((List)atom1).getValue()).get(0);
            }
        }
        
        boolean flag = false;
        if (((Atomic)atom1).getType() != Atomic.Type.BOOLEAN)
            return new CompileError("Semantico", "Tipo de operando invalido para el operador ?", this.line, this.column);
        
        flag = ((Boolean)(((Atomic)atom1).getValue())).booleanValue();
        if (flag) {
            Object atom2 = arg2.process(env);
            
            if (atom2 == null)
                return null;
            if (atom2 instanceof CompileError)
                return atom2;
            
            return atom2;
        }  
        else {
            Object atom3 = arg3.process(env);
            
            if (atom3 == null)
                return null;
            if (atom3 instanceof CompileError)
                return atom3;
            
            return atom3;
        }
            
    }
    
}

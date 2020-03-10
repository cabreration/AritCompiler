/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Expressions;

import APIServices.CompileError;
import Symbols.SymbolsTable;

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
        Object atom2 = arg2.process(env);
        Object atom3 = arg3.process(env);
        
        if (atom1 instanceof CompileError)
            return atom1;
        
        if (atom2 instanceof CompileError)
            return atom2;
        
        if (atom3 instanceof CompileError)
            return atom3;
        
        if (!(atom1 instanceof Value) || !(atom2 instanceof Value) || !(atom3 instanceof Value)) {
            throw new Error("Esto no deberia estar pasando Binario");
        }
        
        if (!(atom1 instanceof Atomic))
            return new CompileError("Semantico", "Tipo de operando invalido para el operador ?", this.line, this.column);
        
        boolean flag = false;
        if (((Atomic)atom1).getType() != Atomic.Type.BOOLEAN)
            return new CompileError("Semantico", "Tipo de operando invalido para el operador ?", this.line, this.column);
        
        flag = ((Boolean)(((Atomic)atom1).getValue())).booleanValue();
        if (flag)
            return atom2;
        else 
            return atom3;
    }
    
}

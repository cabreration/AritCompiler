/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Expressions;

import Symbols.SymbolsTable;

/**
 *
 * @author jacab
 */
public interface Value {
    
    public Object booleanNegation(SymbolsTable env);
    
    public Object aritmeticNegation(SymbolsTable env);
    
    public Object minus(SymbolsTable env, Value op, int order);
    
    public Object plus(SymbolsTable env, Value op, int order);
    
    public Object times(SymbolsTable env, Value op, int order);
    
    public Object div(SymbolsTable env, Value op, int order);
    
    public Object power(SymbolsTable env, Value op, int order);
    
    public Object mod(SymbolsTable env, Value op, int order);
    
    public Object lesser(SymbolsTable env, Value op, int order);
    
    public Object greater(SymbolsTable env, Value op, int order);
    
    public Object lesserEquals(SymbolsTable env, Value op, int order);
    
    public Object greaterEquals(SymbolsTable env, Value op, int order);
    
    public Object equals(SymbolsTable env, Value op, int order);
    
    public Object notEquals(SymbolsTable env, Value op, int order);
    
    public Object and(SymbolsTable env, Value op);
    
    public Object or(SymbolsTable env, Value op);
    
    public Atomic typeof(SymbolsTable env);
    
    public Atomic length(SymbolsTable env);
    
    public Atomic nCol(SymbolsTable env);
    
    public Atomic nRow(SymbolsTable env);
}

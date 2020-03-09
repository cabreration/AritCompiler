/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Expressions;

import Symbols.Enviroment;

/**
 *
 * @author jacab
 */
public interface Value {
    
    public Object booleanNegation(Enviroment env);
    
    public Object aritmeticNegation(Enviroment env);
    
    public Object minus(Enviroment env, Value op, int order);
    
    public Object plus(Enviroment env, Value op, int order);
    
    public Object times(Enviroment env, Value op, int order);
    
    public Object div(Enviroment env, Value op, int order);
    
    public Object power(Enviroment env, Value op, int order);
    
    public Object mod(Enviroment env, Value op, int order);
    
    public Object lesser(Enviroment env, Value op, int order);
    
    public Object greater(Enviroment env, Value op, int order);
    
    public Object lesserEquals(Enviroment env, Value op, int order);
    
    public Object greaterEquals(Enviroment env, Value op, int order);
    
    public Object equals(Enviroment env, Value op, int order);
    
    public Object notEquals(Enviroment env, Value op, int order);
    
    public Object and(Enviroment env, Value op);
    
    public Object or(Enviroment env, Value op);
}

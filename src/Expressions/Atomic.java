/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Expressions;

import APIServices.CompileError;
import Symbols.Enviroment;
import Symbols.Symbol;

/**
 *
 * @author jacab
 */
public class Atomic implements Expression, Value {

    /**
     * Values that can be used
     * <li>{@link #INTEGER}</li>
     * <li>{@link #NUMERIC}</li>
     * <li>{@link #BOOLEAN}</li>
     * <li>{@link #STRING}</li>
     * <li>{@link #IDENTIFIER}</li>
     */
    public enum Type { INTEGER, NUMERIC, BOOLEAN, STRING, IDENTIFIER }
    
    private Type type; //
    private int line;
    private int column;
    private Object value;
    
    public Atomic(Type type, int line, int column, Object value) {
        this.type = type;
        this.line = line;
        this.column = column;
        this.value = value;
    }
    
    public Atomic(Type type, Object value) {
        this.type = type;
        this.value = value;
    }
    
    /**
     * @return The Atomic Expression itself
    */
    @Override
    public Object process(Enviroment env) {
        return this;
    }
    
    /**
     * @return Either a Vector, List, Matrix or Array; Returns null if the Symbol doesnt exist
    */
    public Symbol findInTable(Enviroment env) {
        return env.getSymbol(String.valueOf(this.value));
    }

    public Type getType() {
        return type;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    public Object getValue() {
        return value;
    }
    
    
    /* A partir de aqui todos los metodos que sean sobre operaciones - Value interface*/
    
    @Override
    public Object booleanNegation(Enviroment env) {
        
        if (type == Type.BOOLEAN) {
            Boolean bool = !(((Boolean)value).booleanValue());
            return new Atomic(Type.BOOLEAN, bool);
        }
        
        if (type == Type.IDENTIFIER) {
            Value compound = env.getValue(String.valueOf(this.value));
            return compound.booleanNegation(env);
        }
        
        return new CompileError("Semantico", "Tipo de operando Invalido, incompatible con el operador '!'", this.line, this.column);
    }

    @Override
    public Object aritmeticNegation(Enviroment env) {
        
        if (type == Type.INTEGER) {
            Integer ent = -(((Integer)value).intValue());
            return new Atomic(Type.INTEGER, ent);
        }
        
        if (type == Type.NUMERIC) {
            Double doub = -(((Double)value).doubleValue());
            return new Atomic(Type.NUMERIC, doub);
        }
        
        if (type == Type.IDENTIFIER) {
            Value compound = env.getValue(String.valueOf(this.value));
            return compound.aritmeticNegation(env);
        }
        
        return new CompileError("Semantico", "Tipo de operando invalido, incompatible con el operador '-'", this.line, this.column);
        
    }
    
    @Override
    public Object minus(Enviroment env, Value op) {
        return null;
    }
    
    @Override
    public Object plus(Enviroment env, Value op) {
        return null;
    } 
    
    @Override
    public Object times(Enviroment env, Value op) {
        return null;
    }
    
    @Override
    public Object div(Enviroment env, Value op) {
        return null;
    }
    
    @Override
    public Object mod(Enviroment env, Value op) {
        return null;
    } 
    
    @Override
    public Object power(Enviroment env, Value op) {
        return null;
    } 
    
}

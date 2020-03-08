/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Expressions;

import APIServices.CompileError;
import Symbols.Enviroment;
import Symbols.Symbol;
import Symbols.Vector;

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
        this.line = 0;
        this.column = 0;
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
    
    private Object validateBaldor(Enviroment env, Value op, String operator) {
        
        if (this.type == Type.IDENTIFIER) {
            Value v = env.getValue(String.valueOf(this.value));
            
            if (v == null)
                return new CompileError("Semantico", "La variable '" + String.valueOf(this.value) + "' no ha sido declarada", this.line, this.column);
            
            if (operator.equals("-")) // en esta el orden si importa, tengo que arreglarlo
                return v.minus(env, this); 
            else if (operator.equals("+"))
                return v.plus(env, this);
            else if (operator.equals("*"))
                return v.times(env, this); 
            else if (operator.equals("/")) // en esta el orden si importa, tengo que arreglarlo
                return v.div(env, this);
            else if (operator.equals("%")) // en esta el orden si importa, tengo que arreglarlo
                return v.mod(env, this);
            else 
                return v.power(env, this); // en esta el orden si importa, tengo que arreglarlo
        }
        
        if (op instanceof Atomic) {
            if (((Atomic)op).getType() == Atomic.Type.IDENTIFIER) {
                op = env.getValue(String.valueOf(((Atomic)op).getValue()));
            }
        }
        
        if (op instanceof Vector) {
            if (operator.equals("-"))
                return op.minus(env, this);
            else if (operator.equals("+"))
                return op.plus(env, this);
            else if (operator.equals("*"))
                return op.times(env, this);
            else if (operator.equals("/"))
                return op.div(env, this);
            else if (operator.equals("%"))
                return op.mod(env, this);
            else 
                return op.power(env, this);
        }
        
        if (op instanceof Atomic) {
            if (this.type == Type.INTEGER) {
                if (((Atomic)op).type == Type.INTEGER) {
                    if (operator.equals("^"))
                        return baldorOperate(this, (Atomic)op, Type.NUMERIC, operator);
                    
                    return baldorOperate(this, (Atomic)op, Type.INTEGER, operator);
                }
                
                if (((Atomic)op).type == Type.NUMERIC)
                    return baldorOperate(this, (Atomic)op, Type.NUMERIC, operator);
                
                if (!operator.equals("+"))
                    return new CompileError("Semantico", "Tipo de operando invalido, no valido para '" + operator + "'", this.line, this.column);
            }
            if (this.type == Type.NUMERIC) {
                if (((Atomic)op).type == Type.INTEGER || ((Atomic)op).type == Type.NUMERIC)
                    return baldorOperate(this, (Atomic)op, Type.NUMERIC, operator);
                
                if (!operator.equals("+"))
                    return new CompileError("Semantico", "Tipo de operando invalido, no valido para '" + operator + "'", this.line, this.column);
            }
            
            if (operator.equals("+")) {
                if (this.type == Type.BOOLEAN) {
                    if (((Atomic)op).type == Type.STRING)
                        return baldorOperate(this, (Atomic)op, Type.STRING, operator);
                    
                    return new CompileError("Semantico", "Tipo de operando invalido, no valido para '" + operator + "'", this.line, this.column);
                }
                if (this.type == Type.STRING)
                    return baldorOperate(this, (Atomic)op, Type.NUMERIC, operator);
            }
        }
        
        //if (op instanceof Matrix) {}
        
        return new CompileError("Semantico", "Tipo de operando invalido, la operacion no esta definida para este tipo de dato", this.line, this.column);
    }
    
    private Atomic baldorOperate(Atomic one, Atomic two, Type type, String operator) {
        
        if (type == Type.INTEGER) {
            int o = ((Integer)one.getValue()).intValue();
            int t = ((Integer)two.getValue()).intValue();
            int r = 0;
            
            switch (operator) {
                case "-":
                    r  = o - t;
                case "+":
                    r = o + t;
                case "*":
                    r = o * t;
                case "/":
                    r = o / t;
                case "%":
                    r = o % t;
            }
            
            return new Atomic(Type.INTEGER, Integer.valueOf(r));
        }
        else if (type == Type.NUMERIC) {
            String o = String.valueOf(one.getValue());
            String t = String.valueOf(two.getValue());
            double first = Double.parseDouble(o);
            double second = Double.parseDouble(t);
            
            double r = 0;
            
            switch (operator) {
                case "-":
                    r  = first - second;
                case "+":
                    r = first + second;
                case "*":
                    r = first * second;
                case "/":
                    r = first / second;
                case "%":
                    r = first % second;
                case "^":
                    r = Math.pow(first, second);
            }
            
            return new Atomic(Type.NUMERIC, Double.valueOf(r));
        }
        else {
            String first = String.valueOf(one.getValue());
            if (first == null)
                first = "";
            String second = String.valueOf(two.getValue());
            if (second == null)
                second = "";
            
            return new Atomic(Type.STRING, first + second);
        }
        
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

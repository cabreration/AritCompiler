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
                return v.minus(env, op, 1); 
            else if (operator.equals("+"))
                return v.plus(env, op, 1);
            else if (operator.equals("*"))
                return v.times(env, op, 1); 
            else if (operator.equals("/")) // en esta el orden si importa, tengo que arreglarlo
                return v.div(env, op, 1);
            else if (operator.equals("%")) // en esta el orden si importa, tengo que arreglarlo
                return v.mod(env, op, 1);
            else 
                return v.power(env, op, 1); // en esta el orden si importa, tengo que arreglarlo
        }
        
        if (op instanceof Atomic) {
            if (((Atomic)op).getType() == Atomic.Type.IDENTIFIER) {
                op = env.getValue(String.valueOf(((Atomic)op).getValue()));
            }
        }
        
        if (op instanceof Vector) {
            if (operator.equals("-"))
                return op.minus(env, this, 2);
            else if (operator.equals("+"))
                return op.plus(env, this, 2);
            else if (operator.equals("*"))
                return op.times(env, this, 2);
            else if (operator.equals("/"))
                return op.div(env, this, 2);
            else if (operator.equals("%"))
                return op.mod(env, this, 2);
            else 
                return op.power(env, this, 2);
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
        
        // if it is an array or a list
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
                    break;
                case "+":
                    r = o + t;
                    break;
                case "*":
                    r = o * t;
                    break;
                case "/":
                    r = o / t;
                    break;
                case "%":
                    r = o % t;
                    break;
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
                    break;
                case "+":
                    r = first + second;
                    break;
                case "*":
                    r = first * second;
                    break;
                case "/":
                    r = first / second;
                    break;
                case "%":
                    r = first % second;
                    break;
                case "^":
                    r = Math.pow(first, second);
                    break;
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
    public Object minus(Enviroment env, Value op, int order) {
        return validateBaldor(env, op, "-");
    }
    
    @Override
    public Object plus(Enviroment env, Value op, int order) {
        return validateBaldor(env, op, "+");
    } 
    
    @Override
    public Object times(Enviroment env, Value op, int order) {
        return validateBaldor(env, op, "*");
    }
    
    @Override
    public Object div(Enviroment env, Value op, int order) {
        return validateBaldor(env, op, "/");
    }
    
    @Override
    public Object mod(Enviroment env, Value op, int order) {
        return validateBaldor(env, op, "%");
    } 
    
    @Override
    public Object power(Enviroment env, Value op, int order) {
        return validateBaldor(env, op, "^");
    } 
    
    
    private Object validateRelational(Enviroment env, Value op, String operator) {
        if (this.type == Type.IDENTIFIER) {
            Value v = env.getValue(String.valueOf(this.value));
            
            if (v == null)
                return new CompileError("Semantico", "La variable '" + String.valueOf(this.value) + "' no ha sido declarada", this.line, this.column);
            
            if (operator.equals("<")) // en esta el orden si importa, tengo que arreglarlo
                return v.lesser(env, op, 1); 
            else if (operator.equals(">"))
                return v.greater(env, op, 1);
            else if (operator.equals("<="))
                return v.lesserEquals(env, op, 1); 
            else if (operator.equals(">=")) // en esta el orden si importa, tengo que arreglarlo
                return v.greaterEquals(env, op, 1);
            else if (operator.equals("==")) // en esta el orden si importa, tengo que arreglarlo
                return v.equals(env, op, 1);
            else 
                return v.notEquals(env, op, 1); // en esta el orden si importa, tengo que arreglarlo
        }
        
        if (op instanceof Atomic) {
            if (((Atomic)op).getType() == Atomic.Type.IDENTIFIER) {
                op = env.getValue(String.valueOf(((Atomic)op).getValue()));
            }
        }
        
        if (op instanceof Vector) {
            if (operator.equals("<"))
                return op.lesser(env, this, 2);
            else if (operator.equals(">"))
                return op.greater(env, this, 2);
            else if (operator.equals("<="))
                return op.lesserEquals(env, this, 2);
            else if (operator.equals(">="))
                return op.greaterEquals(env, this, 2);
            else if (operator.equals("=="))
                return op.equals(env, this, 2);
            else 
                return op.notEquals(env, this, 2);
        }
        
        if (op instanceof Atomic) {
            if (this.type == Type.INTEGER || this.type == Type.NUMERIC) {
                if (((Atomic)op).getType() == Type.INTEGER || (((Atomic)op).getType()) ==  Type.NUMERIC) {
                    return operateRelational(this, (Atomic)op, Type.NUMERIC, operator);
                }
                
                return new CompileError("Semantico", "Tipo de Operando invalido, operacion no definida", 0 , 0);
            }
            
            if (this.type == Type.BOOLEAN) {
                if (!operator.equals("==") && !operator.equals("!="))
                    return new CompileError("Semantico", "Tipo de Operando invalido, operacion no definida", 0 , 0);
                
                if (((Atomic)op).getType() == Type.BOOLEAN) {
                    return operateRelational(this, (Atomic)op, Type.BOOLEAN, operator);
                }
                
                return new CompileError("Semantico", "Tipo de Operando invalido, operacion no definida", 0 , 0);
            }
            
            if (this.type == Type.STRING) {
                if (((Atomic)op).getType() == Type.STRING) {
                    return operateRelational(this, (Atomic)op, Type.STRING, operator);
                }
                
                return new CompileError("Semantico", "Tipo de Operando invalido, operacion no definida", 0 , 0);
            }
        }
        
        //if (op instanceof Matrix) {}
        return new CompileError("Semantico", "Tipo de Operando invalido, operacion no definida", 0, 0);
    }
    
    private Atomic operateRelational(Atomic one, Atomic two, Type type, String operator) {
        if (type == Type.NUMERIC) {
            String o = String.valueOf(one.getValue());
            String t = String.valueOf(two.getValue());
            double first = Double.parseDouble(o);
            double second = Double.parseDouble(t);
            
            boolean r = false;
            
            switch (operator) {
                case "<":
                    r  = first < second;
                    break;
                case ">":
                    r = first > second;
                    break;
                case "<=":
                    r = first <= second;
                    break;
                case ">=":
                    r = first >= second;
                    break;
                case "==":
                    r = first == second;
                    break;
                case "!=":
                    r = first != second;
                    break;
            }
            
            return new Atomic(Type.BOOLEAN, Boolean.valueOf(r));
        }
        else if (type == Type.BOOLEAN) {
            String o = String.valueOf(one.getValue());
            String t = String.valueOf(two.getValue());
            boolean first = Boolean.parseBoolean(o);
            boolean second = Boolean.parseBoolean(t);
            
            boolean r = false;
            if (operator.equals("=="))
                r = first == second;
            else
                r = first != second;
            
            return new Atomic(Type.BOOLEAN, Boolean.valueOf(r));
        }
        else {
            String first = String.valueOf(one.getValue());
            if (first == null)
                first = "";
            String second = String.valueOf(two.getValue());
            if (second == null)
                second = "";
            
            boolean r = false;
            
            switch (operator) {
                case "<":
                    r  = first.compareTo(second) < 0;
                    break;
                case ">":
                    r = first.compareTo(second) > 0;
                    break;
                case "<=":
                    r = first.compareTo(second) <= 0;
                    break;
                case ">=":
                    r = first.compareTo(second) >= 0;
                    break;
                case "==":
                    r = first.equals(second);
                    break;
                case "!=":
                    r = !first.equals(second);
                    break;
            }
            
            return new Atomic(Type.BOOLEAN, r);
        }
    }
    
    @Override
    public Object lesser(Enviroment env, Value op, int order) {
        return validateRelational(env, op, "<");
    }
    
    @Override
    public Object greater(Enviroment env, Value op, int order) {
        return validateRelational(env, op, ">");
    }
    
    @Override
    public Object lesserEquals(Enviroment env, Value op, int order) {
        return validateRelational(env, op, "<=");
    }
    
    @Override
    public Object greaterEquals(Enviroment env, Value op, int order) {
        return validateRelational(env, op, ">=");
    }
    
    @Override
    public Object equals(Enviroment env, Value op, int order) {
        return validateRelational(env, op, "==");
    }
    
    @Override
    public Object notEquals(Enviroment env, Value op, int order) {
        return validateRelational(env, op, "!=");
    }
    
}

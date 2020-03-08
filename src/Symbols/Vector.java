/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Symbols;

import APIServices.CompileError;
import Expressions.Atomic;
import Expressions.Value;
import java.util.ArrayList;

/**
 *
 * @author jacab
 */
public class Vector implements Symbol, Value {

    private ArrayList<Object> content;
    private int type; // 1 - integer, 2 - numeric, 3 - bool, 4 - string
    
    public Vector(Object value) {
        this.content = new ArrayList<Object>();
        
        if (value == null)
            type = 4;
        else if (value instanceof Integer)
            type = 1;
        else if (value instanceof Double)
            type = 2;
        else if (value instanceof Boolean)
            type = 3;
        else 
            type = 4;
        
        this.content.add(value);
    }
    
    public Vector(ArrayList<Object> content, int type) {
        this.content = content;
        this.type = type;
    }
    
    public Vector(int type) {
        this.type = type;
        this.content = new ArrayList<Object>();
    }
    
    @Override
    public Object getValue() {
        return this.content;
    }

    @Override
    public int getSize() {
        return this.content.size();
    }
    
        public int type() {
        return this.type;
    }

    @Override
    public Object booleanNegation(Enviroment env) {
        if (this.type != 3)
            return new CompileError("Semantico", "Tipo de operando incorrecto: no se puede operar valores no booleanos con el operador '!'", 0, 0);
        
        ArrayList<Object> store = new ArrayList<Object>();
        for (Object ob : this.content) {
            boolean val = !((Boolean)ob).booleanValue();
            store.add(val);
        }
        
        return new Vector(store, 3);
    }
    
    @Override
    public Object aritmeticNegation(Enviroment env) {
        if (this.type != 1 && this.type != 2)
            return new CompileError("Semantico", "Tipo de operando incorrecto: solo pueden operarse valores numericos y enteros con el operador '-'", 0, 0);
        
        ArrayList<Object> store = new ArrayList<Object>();
        for (Object ob : this.content) {
            
            if (ob instanceof Integer)
                store.add(Integer.valueOf(-((Integer)ob)));
            else 
                store.add(Double.valueOf(-((Double)ob)));
        }
        
        return this.type == 1 ? new Vector(store, 1) : new Vector(store, 2);
    }
    
    private Object validateBaldor(Enviroment env, Value op, String operator) {
        /* Valid cases */
        if (op instanceof Atomic) {
            if (((Atomic)op).getType() == Atomic.Type.IDENTIFIER) {
                String name = String.valueOf(((Atomic)op).getValue());
                int l = ((Atomic)op).getLine();
                int c = ((Atomic)op).getColumn();
                op = env.getValue(String.valueOf(((Atomic)op).getValue()));
                
                if (op == null)
                    return new CompileError("Semantico", "La variable '" + name + "' no ha sido declarada", l, c);
            }
        }
        
        // THE OPERAND IS ANOTHER VECTOR
        if (op instanceof Vector) {
            Vector vec = (Vector)op;
            if (this.getSize() == vec.getSize()) {
                if (this.type == 1) {
                    if (vec.type == 1) {
                        if (operator.equals("^"))
                            return baldorVectors(this.content, (ArrayList<Object>)vec.getValue(), 2, operator);
                            
                        return baldorVectors(this.content, (ArrayList<Object>)vec.getValue(), 1, operator);
                    }
                        
                    
                    if (vec.type == 2) 
                        return baldorVectors(this.content, (ArrayList<Object>)vec.getValue(), 2, operator);
                    
                    if (!operator.equals("+"))
                        return new CompileError("Semantico", "Tipo de operando invalido, no se puede aplicar al operador '" + operator + "'", 0, 0);
                }
                
                if (this.type == 2) {
                    if (vec.type == 1 || vec.type == 2) 
                        return baldorVectors(this.content, (ArrayList<Object>)vec.getValue(), 2, operator);
                    
                    if (!operator.equals("+"))
                        return new CompileError("Semantico", "Tipo de operando invalido, no se puede aplicar al operador '" + operator + "'", 0, 0);
                }
                
                if (operator.equals("+")) {
                    if (this.type == 3) {
                        if (vec.type == 4) {
                            return baldorVectors(this.content, (ArrayList<Object>)vec.getValue(), 4, "+");
                        }
                        
                        return new CompileError("Semantico", "Tipo de operando invalido, no se puede aplicar al operador '" + operator + "'", 0, 0);
                    }
                
                    if (this.type == 4) {
                        return baldorVectors(this.content, (ArrayList<Object>)vec.getValue(), 4, "+");
                    }
                }
                
                return new CompileError("Semantico", "Tipo de operando invalido, no se puede aplicar al operador '" + operator + "'", 0, 0);
            }
            
            return new CompileError("Semantico", "Los vectores que intenta operar no son del mismo tamaño", 0, 0);
        }
        
        // THE OPERAND IS A PRIMITIVE DATA TYPE
        if (op instanceof Atomic) {
            if (this.type == 2) {
                if (((Atomic)op).getType() == Atomic.Type.INTEGER) {
                    double val = ((Integer)(((Atomic)op).getValue())).doubleValue();
                    return baldorVector(this.content, val, operator);
                }
                
                if (((Atomic)op).getType() == Atomic.Type.NUMERIC) {
                    double val = ((Double)(((Atomic)op).getValue())).doubleValue();
                    return baldorVector(this.content, val, operator);
                }
                
                if (!operator.equals("+"))
                    return new CompileError("Semantico", "Tipo de operando invalido, incompatible con el operador '" + operator + "'", 0, 0);
            }
            
            if (this.type == 1) {
                if (((Atomic)op).getType() == Atomic.Type.INTEGER) {
                    int val = ((Integer)(((Atomic)op).getValue())).intValue();
                    return baldorVector(this.content, val, operator);
                }
                
                if (((Atomic)op).getType() == Atomic.Type.NUMERIC) {
                    double val = ((Double)(((Atomic)op).getValue())).doubleValue();
                    return baldorVector(this.content, val, operator);
                }
                
                if (!operator.equals("+"))
                    return new CompileError("Semantico", "Tipo de operando invalido, incompatible con el operador '" + operator + "'", 0, 0);
            }
            
            if (operator.equals("+")) {
                if (this.type == 3) {
                    if (((Atomic)op).getType() == Atomic.Type.STRING) {
                        return stringAdding(this.content, String.valueOf(((Atomic)op).getValue()));
                    }
                    
                    return new CompileError("Semantico", "Tipo de operando invalido, incompatible con el operador '" + operator + "'", 0, 0);
                }
                
                if (this.type == 4) {
                    return stringAdding(this.content, String.valueOf(((Atomic)op).getValue()));
                }
            }
                
            return new CompileError("Semantico", "Tipo de operando invalido, no se puede aplicar al operador '" + operator + "'", 0, 0);
        }
        
        //THE OPERAND IS A LIST, MATRIX OR ARRAY
        return new CompileError("Semantico", "Tipo de operando invalido, operacion imposible con valor de tipo vector", 0, 0);
    }
    
    private Vector baldorVectors(ArrayList<Object> v1, ArrayList<Object> v2, int type, String operator) { 
        ArrayList<Object> res = new ArrayList<Object>();
        
        for (int i = 0; i < v1.size(); i++) {
            if (type == 1) {
                int r = 0;
                
                if (operator.equals("-"))
                    r = ((Integer)v1.get(i)).intValue() - ((Integer)v2.get(i)).intValue();
                else if (operator.equals("*"))
                    r = ((Integer)v1.get(i)).intValue() * ((Integer)v2.get(i)).intValue();
                else if (operator.equals("/"))
                    r = ((Integer)v1.get(i)).intValue() / ((Integer)v2.get(i)).intValue();
                else if (operator.equals("%"))
                    r = ((Integer)v1.get(i)).intValue() % ((Integer)v2.get(i)).intValue();
                else 
                    r = ((Integer)v1.get(i)).intValue() + ((Integer)v2.get(i)).intValue();
                
                res.add(Integer.valueOf(r));
            }
            else if (type == 2){
                String one = String.valueOf(v1.get(i));
                String two = String.valueOf(v2.get(i));
                double r = 0;
                
                if (operator.equals("-"))
                    r = Double.valueOf(one) - Double.valueOf(two);
                else if (operator.equals("+"))
                    r = Double.valueOf(one) + Double.valueOf(two);
                else if (operator.equals("*"))
                    r = Double.valueOf(one) * Double.valueOf(two);
                else if (operator.equals("/"))
                    r = Double.valueOf(one) / Double.valueOf(two);
                else if (operator.equals("%"))
                    r = Double.valueOf(one) % Double.valueOf(two);
                else 
                    r = Math.pow(Double.valueOf(one), Double.valueOf(two));
                    
                res.add(Double.valueOf(r));
            }
            else {
                String one = String.valueOf(v1.get(i));
                if (one ==  null)
                    one = "";
                String two = String.valueOf(v2.get(i));
                if (two == null)
                    two = "";
                
                res.add(one + two);
            }
        }
        
        return new Vector(res, type);
    }
    
    private Vector baldorVector(ArrayList<Object> v1, double val, String operator) { 
        ArrayList<Object> res = new ArrayList<Object>();
        
        for (Object ob : v1) {
            String cur = String.valueOf(ob);
            double doub = Double.valueOf(cur);
            double r = 0;
            
            if (operator.equals("-"))
                r = doub - val;
            else if (operator.equals("+"))
                r = doub + val;
            else if (operator.equals("*"))
                r = doub * val;
            else if (operator.equals("/"))
                r = doub / val;
            else if (operator.equals("%"))
                r = doub % val;
            else 
                r = Math.pow(doub, val);
                    
            res.add(Double.valueOf(r));
        }
        
        return new Vector(res, 2);
    }
    
    private Vector baldorVector(ArrayList<Object> v1, int val, String operator) { 
        ArrayList<Object> res = new ArrayList<Object>();
        
        for (Object ob: v1) {
            int ent = ((Integer)ob).intValue();
            int r = 0;
            
            if (operator.equals("-"))
                r = ent - val;
            else if (operator.equals("+"))
                r = ent + val;
            else if (operator.equals("*"))
                r = ent * val;
            else if (operator.equals("/"))
                r = ent / val;
            else if (operator.equals("%"))
                r = ent % val;
                    
            res.add(Integer.valueOf(r));
        }
        
        return new Vector(res, 1);
    }
    
    private Vector stringAdding(ArrayList<Object> v1, String val) {
        ArrayList<Object> res = new ArrayList<Object>();
        if (val == null)
            val = "";
        
        for (Object ob: v1) {
            String one = String.valueOf(ob);
            if (one == null)
                one = "";
            
            res.add(one + val);
        }
        
        return new Vector(res, 4);
    }
    
    @Override
    public Object minus(Enviroment env, Value op) {
        return validateBaldor(env, op, "-");
    }
    
    @Override
    public Object plus(Enviroment env, Value op) {
        return validateBaldor(env, op, "+");
    } 
    
    @Override
    public Object times(Enviroment env, Value op) {
        return validateBaldor(env, op, "*");
    }
    
    @Override
    public Object div(Enviroment env, Value op) {
        return validateBaldor(env, op, "/");
    }
    
    @Override
    public Object mod(Enviroment env, Value op) {
        return validateBaldor(env, op, "%");
    } 
    
    @Override
    public Object power(Enviroment env, Value op) {
        return validateBaldor(env, op, "^");
    } 
}

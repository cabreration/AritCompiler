/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Symbols;

import APIServices.CompileError;
import Expressions.Atomic;
import Expressions.Value;
import aritcompiler.Singleton;
import java.util.ArrayList;

/**
 *
 * @author jacab
 */
public class Vector implements Symbol, Value {

    private ArrayList<Atomic> content;
    private int type; // 1 - integer, 2 - numeric, 3 - bool, 4 - string
    
    public Vector(Atomic value) {
        this.content = new ArrayList<Atomic>();
        
        if (value.getType() == Atomic.Type.STRING)
            type = 4;
        else if (value.getType() == Atomic.Type.INTEGER)
            type = 1;
        else if (value.getType() == Atomic.Type.NUMERIC)
            type = 2;
        else if (value.getType() == Atomic.Type.BOOLEAN)
            type = 3;
        
        if (value.getType() == Atomic.Type.IDENTIFIER)
            System.err.println("ESTO NO DEBERIA ESTAR PASANDO");
        
        this.content.add(value);
    }
    
    public Vector(ArrayList<Atomic> content, int type) {
        this.content = content;
        this.type = type;
    }
    
    public Vector(int type) {
        this.type = type;
        this.content = new ArrayList<Atomic>();
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
    public Object booleanNegation(SymbolsTable env) {
        if (this.type != 3)
            return new CompileError("Semantico", "Tipo de operando incorrecto: no se puede operar valores no booleanos con el operador '!'", 0, 0);
        
        ArrayList<Atomic> store = new ArrayList<Atomic>();
        for (Atomic ob : this.content) {
            boolean val = !((Boolean)ob.getValue()).booleanValue();
            store.add(new Atomic(Atomic.Type.BOOLEAN, val));
        }
        
        return new Vector(store, 3);
    }
    
    @Override
    public Object aritmeticNegation(SymbolsTable env) {
        if (this.type != 1 && this.type != 2)
            return new CompileError("Semantico", "Tipo de operando incorrecto: solo pueden operarse valores numericos y enteros con el operador '-'", 0, 0);
        
        ArrayList<Atomic> store = new ArrayList<Atomic>();
        for (Atomic ob : this.content) {
            
            if (ob.getType() == Atomic.Type.INTEGER)
                store.add(new Atomic(Atomic.Type.INTEGER, Integer.valueOf(-((Integer)ob.getValue()))));
            else 
                store.add(new Atomic(Atomic.Type.NUMERIC, Double.valueOf(-((Double)ob.getValue()))));
        }
        
        return this.type == 1 ? new Vector(store, 1) : new Vector(store, 2);
    }
    
    private Object validateBaldor(SymbolsTable env, Value op, String operator, int order) {
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
                        if (operator.equals("^")) {
                            Vector thor = baldorVectors(this.content, (ArrayList<Atomic>)vec.getValue(), 2, operator);
                            if (thor == null)
                                return new CompileError("Semantico", "Las operaciones de division y modulo sobre 0 no estan definidas", 0, 0);
                            
                            return thor;
                        }
                              
                        Vector thor = baldorVectors(this.content, (ArrayList<Atomic>)vec.getValue(), 1, operator);
                        if (thor == null)
                                return new CompileError("Semantico", "Las operaciones de division y modulo sobre 0 no estan definidas", 0, 0);
                            
                        return thor;
                    }
                        
                    
                    if (vec.type == 2) {
                        Vector thor = baldorVectors(this.content, (ArrayList<Atomic>)vec.getValue(), 2, operator);
                        if (thor == null)
                                return new CompileError("Semantico", "Las operaciones de division y modulo sobre 0 no estan definidas", 0, 0);
                            
                        return thor;
                    }
                    
                    if (!operator.equals("+"))
                        return new CompileError("Semantico", "Tipo de operando invalido, no se puede aplicar al operador '" + operator + "'", 0, 0);
                }
                
                if (this.type == 2) {
                    if (vec.type == 1 || vec.type == 2) {
                        Vector thor = baldorVectors(this.content, (ArrayList<Atomic>)vec.getValue(), 2, operator);
                        if (thor == null)
                            return new CompileError("Semantico", "Las operaciones de division y modulo sobre 0 no estan definidas", 0, 0);
                            
                        return thor;
                    } 
                    
                    if (!operator.equals("+"))
                        return new CompileError("Semantico", "Tipo de operando invalido, no se puede aplicar al operador '" + operator + "'", 0, 0);
                }
                
                if (operator.equals("+")) {
                    if (this.type == 3) {
                        if (vec.type == 4) {
                            Vector thor = baldorVectors(this.content, (ArrayList<Atomic>)vec.getValue(), 4, "+");
                            if (thor == null)
                                return new CompileError("Semantico", "Las operaciones de division y modulo sobre 0 no estan definidas", 0, 0);
                            
                            return thor;
                        }
                        
                        return new CompileError("Semantico", "Tipo de operando invalido, no se puede aplicar al operador '" + operator + "'", 0, 0);
                    }
                
                    if (this.type == 4) {
                        Vector thor = baldorVectors(this.content, (ArrayList<Atomic>)vec.getValue(), 4, "+");
                        if (thor == null)
                            return new CompileError("Semantico", "Operacion invalida, no es posible realizar operaciones con el valor null", 0, 0);
                            
                        return thor;
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
                    Vector thor = baldorVector(this.content, val, operator, order);
                    if (thor == null)
                        return new CompileError("Semantico", "Las operaciones de division y modulo sobre 0 no estan definidas", 0, 0);
                            
                    return thor;
                }
                
                if (((Atomic)op).getType() == Atomic.Type.NUMERIC) {
                    double val = ((Double)(((Atomic)op).getValue())).doubleValue();
                    Vector thor = baldorVector(this.content, val, operator, order);
                    if (thor == null)
                        return new CompileError("Semantico", "Las operaciones de division y modulo sobre 0 no estan definidas", 0, 0);
                            
                    return thor;
                }
                
                if (!operator.equals("+"))
                    return new CompileError("Semantico", "Tipo de operando invalido, incompatible con el operador '" + operator + "'", 0, 0);
            }
            
            if (this.type == 1) {
                if (((Atomic)op).getType() == Atomic.Type.INTEGER) {
                    int val = ((Integer)(((Atomic)op).getValue())).intValue();
                    Vector thor = baldorVector(this.content, val, operator, order);
                    if (thor == null)
                        return new CompileError("Semantico", "Las operaciones de division y modulo sobre 0 no estan definidas", 0, 0);
                            
                    return thor;
                }
                
                if (((Atomic)op).getType() == Atomic.Type.NUMERIC) {
                    double val = ((Double)(((Atomic)op).getValue())).doubleValue();
                    Vector thor = baldorVector(this.content, val, operator, order);
                    if (thor == null)
                        return new CompileError("Semantico", "Las operaciones de division y modulo sobre 0 no estan definidas", 0, 0);
                            
                    return thor;
                }
                
                if (!operator.equals("+"))
                    return new CompileError("Semantico", "Tipo de operando invalido, incompatible con el operador '" + operator + "'", 0, 0);
            }
            
            if (operator.equals("+")) {
                if (this.type == 3) {
                    if (((Atomic)op).getType() == Atomic.Type.STRING) {
                        Vector thor = stringAdding(this.content, String.valueOf(((Atomic)op).getValue()), order);
                        if (thor == null)
                            return new CompileError("Semantico", "Operacion Invalida", 0, 0);
                            
                        return thor;
                    }
                    
                    return new CompileError("Semantico", "Tipo de operando invalido, incompatible con el operador '" + operator + "'", 0, 0);
                }
                
                if (this.type == 4) {
                    Vector thor = stringAdding(this.content, String.valueOf(((Atomic)op).getValue()), order);
                    if (thor == null)
                        return new CompileError("Semantico", "No es posible operar valores nulos", 0, 0);
                            
                    return thor;
                }
            }
                
            return new CompileError("Semantico", "Tipo de operando invalido, no se puede aplicar al operador '" + operator + "'", 0, 0);
        }
        
        //THE OPERAND IS A LIST, MATRIX OR ARRAY
        return new CompileError("Semantico", "Tipo de operando invalido, operacion imposible con valor de tipo vector", 0, 0);
    }
    
    private Vector baldorVectors(ArrayList<Atomic> v1, ArrayList<Atomic> v2, int type, String operator) { 
        ArrayList<Atomic> res = new ArrayList<Atomic>();
        
        for (int i = 0; i < v1.size(); i++) {
            if (type == 1) {
                int r = 0;
                
                if (operator.equals("-"))
                    r = ((Integer)v1.get(i).getValue()).intValue() - ((Integer)v2.get(i).getValue()).intValue();
                else if (operator.equals("*"))
                    r = ((Integer)v1.get(i).getValue()).intValue() * ((Integer)v2.get(i).getValue()).intValue();
                else if (operator.equals("/")) {
                    if (((Integer)v2.get(i).getValue()).intValue()  == 0)
                        return null;
                    
                    r = ((Integer)v1.get(i).getValue()).intValue() / ((Integer)v2.get(i).getValue()).intValue();
                }
                else if (operator.equals("%")) {
                    if (((Integer)v2.get(i).getValue()).intValue()  == 0)
                        return null;
                    
                    r = ((Integer)v1.get(i).getValue()).intValue() % ((Integer)v2.get(i).getValue()).intValue();
                }
                else 
                    r = ((Integer)v1.get(i).getValue()).intValue() + ((Integer)v2.get(i).getValue()).intValue();
                
                res.add(new Atomic(Atomic.Type.INTEGER, Integer.valueOf(r)));
            }
            else if (type == 2){
                String one = String.valueOf(v1.get(i).getValue());
                String two = String.valueOf(v2.get(i).getValue());
                double r = 0;
                
                if (operator.equals("-"))
                    r = Double.valueOf(one) - Double.valueOf(two);
                else if (operator.equals("+"))
                    r = Double.valueOf(one) + Double.valueOf(two);
                else if (operator.equals("*"))
                    r = Double.valueOf(one) * Double.valueOf(two);
                else if (operator.equals("/")) {
                    if (Double.valueOf(two) == 0.0 || Double.valueOf(two) == 0)
                        return null;
                    
                    r = Double.valueOf(one) / Double.valueOf(two);
                }
                else if (operator.equals("%")) {
                    if (Double.valueOf(two) == 0.0 || Double.valueOf(two) == 0)
                        return null;
                    
                    r = Double.valueOf(one) % Double.valueOf(two);
                }
                else 
                    r = Math.pow(Double.valueOf(one), Double.valueOf(two));
                    
                res.add(new Atomic(Atomic.Type.NUMERIC, Double.valueOf(r)));
            }
            else {
                if (v1.get(i).getValue() == null)
                    return null;
                String one = String.valueOf(v1.get(i).getValue());
                if (v2.get(i).getValue() == null)
                    return null;
                String two = String.valueOf(v2.get(i).getValue());
                
                res.add(new Atomic(Atomic.Type.STRING, one + two));
            }
        }
        
        return new Vector(res, type);
    }
    
    private Vector baldorVector(ArrayList<Atomic> v1, double val, String operator, int order) { 
        ArrayList<Atomic> res = new ArrayList<Atomic>();
        
        for (Atomic ob : v1) {
            String cur = String.valueOf(ob.getValue());
            double doub = Double.valueOf(cur);
            double r = 0;
            
            if (operator.equals("-"))
                r = order == 1 ? doub - val : val - doub;
            else if (operator.equals("+"))
                r = order == 1 ? doub + val : val + doub;
            else if (operator.equals("*"))
                r = order == 1 ? doub * val : val * doub;
            else if (operator.equals("/")) {
                if (order == 1 && val == 0) 
                    return null;
                else if (order == 2 && doub == 0) 
                    return null;
                
                r = order == 1 ? doub / val : val / doub;
            }
            else if (operator.equals("%")) {
                if (order == 1 && val == 0) 
                    return null;
                else if (order == 2 && doub == 0) 
                    return null;
                
                r = order == 1 ? doub % val : val % doub;
            }
            else 
                r = Math.pow(doub, val);
                    
            res.add(new Atomic(Atomic.Type.NUMERIC, Double.valueOf(r)));
        }
        
        return new Vector(res, 2);
    }
    
    private Vector baldorVector(ArrayList<Atomic> v1, int val, String operator, int order) { 
        ArrayList<Atomic> res = new ArrayList<Atomic>();
        
        for (Atomic ob: v1) {
            int ent = ((Integer)ob.getValue()).intValue();
            int r = 0;
            
            if (operator.equals("-"))
                r = order == 1 ? ent - val : val - ent;
            else if (operator.equals("+"))
                r = order == 1 ? ent + val : val + ent;
            else if (operator.equals("*"))
                r = order == 1 ? ent * val : val * ent;
            else if (operator.equals("/")) {
                if (order == 1 && val == 0)
                    return null;
                else if (order == 2 && ent == 0)
                    return null;
                
                r = order == 1 ? ent / val : val / ent;
            }              
            else if (operator.equals("%")) {
                if (order == 1 && val == 0)
                    return null;
                else if (order == 2 && ent == 0)
                    return null;
                
                r = order == 1 ? ent % val : val % ent;
            } 
                    
            res.add(new Atomic(Atomic.Type.INTEGER, Integer.valueOf(r)));
        }
        
        return new Vector(res, 1);
    }
    
    private Vector stringAdding(ArrayList<Atomic> v1, String val, int order) {
        ArrayList<Atomic> res = new ArrayList<Atomic>();
        if (val == null)
            return null;
        
        for (Atomic ob: v1) {
            if (ob.getValue() == null)
                return null;
            String one = String.valueOf(ob.getValue());
            
            
            if (order == 1)
                res.add(new Atomic(Atomic.Type.STRING, one + val));
            else 
                res.add(new Atomic(Atomic.Type.STRING, val + one));
        }
        
        return new Vector(res, 4);
    }
    
    @Override
    public Object minus(SymbolsTable env, Value op, int order) {
        return validateBaldor(env, op, "-", order);
    }
    
    @Override
    public Object plus(SymbolsTable env, Value op, int order) {
        return validateBaldor(env, op, "+", order);
    } 
    
    @Override
    public Object times(SymbolsTable env, Value op, int order) {
        return validateBaldor(env, op, "*", order);
    }
    
    @Override
    public Object div(SymbolsTable env, Value op, int order) {
        return validateBaldor(env, op, "/", order);
    }
    
    @Override
    public Object mod(SymbolsTable env, Value op, int order) {
        return validateBaldor(env, op, "%", order);
    } 
    
    @Override
    public Object power(SymbolsTable env, Value op, int order) {
        return validateBaldor(env, op, "^", order);
    } 
    
    private Object validateRelational(SymbolsTable env, Value op, String operator, int order) {
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
        
        if (op instanceof Vector) {
            Vector vec = (Vector)op;
            if (this.type == 1 || this.type == 2) {
                if (vec.type == 1 || vec.type == 2){
                    return relationalVectors(this.content, (ArrayList<Atomic>)vec.getValue(), 2, operator);
                }
                
                return new CompileError("Semantico", "Tipo de operando invalido para el operador '" + operator + "'", 0, 0);
            }
            
            if (this.type == 3) {
                if (!operator.equals("==") && !operator.equals("!="))
                   return new CompileError("Semantico", "Tipo de operando invalido para el operador '" + operator + "'", 0, 0);
                
                if (vec.type == 3) {
                    return relationalVectors(this.content, (ArrayList<Atomic>)vec.getValue(), 3, operator);
                }
                
                return new CompileError("Semantico", "Tipo de operando invalido para el operador '" + operator + "'", 0, 0);
            }
            
            if (this.type == 4) {
                if (vec.type == 4) {
                    Vector thor = relationalVectors(this.content, (ArrayList<Atomic>)vec.getValue(), 4, operator);
                    if (thor == null)
                        return new CompileError("Semantico", "No es posible operar valores nulos", 0, 0);
                    
                    return thor;
                }
                
                return new CompileError("Semantico", "Tipo de operando invalido para el operador '" + operator + "'", 0, 0);
            }
        }
        
        if (op instanceof Atomic) {
            if (this.type == 1 || this.type == 2) {
                if (((Atomic)op).getType() == Atomic.Type.INTEGER || ((Atomic)op).getType() == Atomic.Type.NUMERIC) {
                    String str = String.valueOf(((Atomic)op).getValue());
                    double arg = Double.valueOf(str);
                    return relationalVector(this.content, arg, order, operator);
                }
                
                return new CompileError("Semantico", "Tipo de operando invalido para el operador '" + operator + "'", 0, 0);
            }
            
            if (this.type == 3) {
                if (!operator.equals("==") && !operator.equals("!="))
                    return new CompileError("Semantico", "Tipo de operando invalido para el operador '" + operator + "'", 0, 0);
                
                if (((Atomic)op).getType() == Atomic.Type.BOOLEAN) {
                    boolean arg = ((Boolean)(((Atomic)op).getValue())).booleanValue();
                    return relationalVector(this.content, arg, operator);
                }
                
                return new CompileError("Semantico", "Tipo de operando invalido para el operador '" + operator + "'", 0, 0);
            }
            
            if (this.type == 4) {
                if (((Atomic)op).getType() == Atomic.Type.STRING) {
                    if (((Atomic)op).getValue() == null)
                        return new CompileError("Semantico", "No es posible operar valores nulos", 0, 0);
                    
                    String str = String.valueOf(((Atomic)op).getValue());
                    Vector thor = relationalVector(this.content, str, order, operator);
                    if (thor == null)
                        return new CompileError("Semantico", "No es posible operar valores nulos", 0, 0);
                    
                    return thor;
                }
            }
        }
        
        //operand is an array, list or matrix
        return new CompileError("Semantico", "Tipo de operando invalido para el operador '" + operator + "'", 0, 0);
    }
    
    private Vector relationalVectors(ArrayList<Atomic> v1, ArrayList<Atomic> v2, int type, String operator) {
        ArrayList<Atomic> res = new ArrayList<Atomic>();
        
        for (int i = 0; i < v1.size(); i++) {
            if (type == 2) {
                String first = String.valueOf(v1.get(i).getValue());
                String second = String.valueOf(v2.get(i).getValue());
                double one = Double.valueOf(first);
                double two = Double.valueOf(second);
                
                boolean flag = false;
                switch (operator) {
                    case "<":
                        flag = one < two;
                        break;
                    case ">":
                        flag = one > two;
                        break;
                    case "<=":
                        flag = one <= two;
                        break;
                    case ">=":
                        flag = one >= two;
                        break;
                    case "==":
                        flag = one == two;
                        break;
                    case "!=":
                        flag = one != two;
                        break;
                }
                
                res.add(new Atomic(Atomic.Type.BOOLEAN, Boolean.valueOf(flag)));
            }
            else if (type == 3) {
                boolean one = ((Boolean)v1.get(i).getValue()).booleanValue();
                boolean two = ((Boolean)v2.get(i).getValue()).booleanValue();
                
                boolean flag = false;
                if (operator.equals("=="))
                    flag = one == two;
                else
                    flag = one != two;
                
                res.add(new Atomic(Atomic.Type.BOOLEAN, Boolean.valueOf(flag)));
            }
            else {
                if (v1.get(i).getValue() == null || v2.get(i).getValue() == null)
                    return null;
                String one = String.valueOf(v1.get(i).getValue());
                String two = String.valueOf(v2.get(i).getValue());
                
                boolean flag = false;
                switch (operator) {
                    case "<":
                        flag = one.compareTo(two) < 0;
                        break;
                    case ">":
                        flag = one.compareTo(two) > 0;
                        break;
                    case "<=":
                        flag = one.compareTo(two) <= 0;
                        break;
                    case ">=":
                        flag = one.compareTo(two) >= 0;
                        break;
                    case "==":
                        flag = one.compareTo(two) == 0;
                        break;
                    case "!=":
                        flag = one.compareTo(two) != 0;
                        break;
                }
                
                res.add(new Atomic(Atomic.Type.BOOLEAN, flag));
            }
        }
        return new Vector(res, 3);
    }
    
    private Vector relationalVector(ArrayList<Atomic> v1, double doub, int order, String operator) {
        ArrayList<Atomic> res = new ArrayList<Atomic>();
        
        for (Atomic ob: v1) {
            String one = String.valueOf(ob.getValue());
            double dib = Double.valueOf(one);
            
            boolean flag  = false;
            switch (operator) {
                case "<":
                    flag = order == 1 ? dib < doub : doub < dib;
                    break;
                case ">":
                    flag = order == 1 ? dib > doub : doub > dib;
                    break;
                case "<=":
                    flag = order == 1 ? dib <= doub : doub <= dib;
                    break;
                case ">=":
                    flag = order == 1 ? dib >= doub : doub >= dib;
                    break;
                case "==":
                    flag = order == 1 ? dib == doub : doub == dib;
                    break;
                case "!=":
                    flag = order == 1 ? dib != doub : doub != dib;
                    break;
            }
            
            res.add(new Atomic(Atomic.Type.BOOLEAN, Boolean.valueOf(flag)));
        }
        return new Vector(res, 3);
    }
    
    private Vector relationalVector(ArrayList<Atomic> v1, boolean bool, String operator) {
        ArrayList<Atomic> res = new ArrayList<Atomic>();
        
        for (Atomic ob: v1) {
            boolean dib = ((Boolean)ob.getValue()).booleanValue();
            
            boolean flag  = false;
            switch (operator) {
                case "==":
                    flag = dib == bool;
                    break;
                case "!=":
                    flag = dib != bool;
                    break;
            }
            
            res.add(new Atomic(Atomic.Type.BOOLEAN, Boolean.valueOf(flag)));
        }
        return new Vector(res, 3);
    }
    
    private Vector relationalVector(ArrayList<Atomic> v1, String str, int order, String operator) {
        ArrayList<Atomic> res = new ArrayList<Atomic>();
        
        for (Atomic ob: v1) {
            if (ob.getValue() == null)
                return null;
            String one = String.valueOf(ob.getValue());
            
            boolean flag  = false;
            switch (operator) {
                case "<":
                    flag = order == 1 ? one.compareTo(str) < 0 : str.compareTo(one) < 0;
                    break;
                case ">":
                    flag = order == 1 ? one.compareTo(str) > 0 : str.compareTo(one) > 0;
                    break;
                case "<=":
                    flag = order == 1 ? one.compareTo(str) <= 0 : str.compareTo(one) <= 0;
                    break;
                case ">=":
                    flag = order == 1 ? one.compareTo(str) >= 0 : str.compareTo(one) >= 0;
                    break;
                case "==":
                    flag = order == 1 ? one.compareTo(str) == 0 : str.compareTo(one) == 0;
                    break;
                case "!=":
                    flag = order == 1 ? one.compareTo(str) != 0 : str.compareTo(one) != 0;
                    break;
            }
            
            res.add(new Atomic(Atomic.Type.BOOLEAN, Boolean.valueOf(flag)));
        }
        return new Vector(res, 3);
    }
    
    @Override
    public Object lesser(SymbolsTable env, Value op, int order) {
        return validateRelational(env, op, "<", order);
    }
    
    @Override
    public Object greater(SymbolsTable env, Value op, int order) {
        return validateRelational(env, op, ">", order);
    }
    
    @Override
    public Object lesserEquals(SymbolsTable env, Value op, int order) {
        return validateRelational(env, op, "<=", order);
    }
    
    @Override
    public Object greaterEquals(SymbolsTable env, Value op, int order) {
        return validateRelational(env, op, ">=", order);
    }
    
    @Override
    public Object equals(SymbolsTable env, Value op, int order) {
        return validateRelational(env, op, "==", order);
    }
    
    @Override
    public Object notEquals(SymbolsTable env, Value op, int order) {
        return validateRelational(env, op, "!=", order);
    }
    
    /* operaciones booleanas */ 
    private Object validateBoolean(SymbolsTable env, Value op, String operator) {
        if (this.type != 3)
            return new CompileError("Semantico", "Tipo de operando invalido para el operador '" + operator + "'", 0, 0);
        
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
        
        if (op instanceof Vector) {
            if (((Vector)op).getSize() == this.getSize()) {
                if (((Vector)op).type() == 3) {
                    Vector vec = (Vector)op;
                    return booleanVectors(this.content, (ArrayList<Atomic>)vec.getValue(), operator);
                }
                    
                return new CompileError("Semantico", "Tipo de operando invalido para el operador '" + operator + "'", 0, 0);
            }
            
            return new CompileError("Semantico", "Solo pueden realizarse operaciones sobre vectores del mismo tamaño", 0, 0);
        }
        
        if (op instanceof Atomic) {
            if (((Atomic)op).getType() == Atomic.Type.BOOLEAN) {
                Atomic val = (Atomic)op;
                boolean bool = ((Boolean)val.getValue()).booleanValue();
                return booleanVector(this.content, bool, operator);
            }
            
            return new CompileError("Semantico", "Tipo de operando invalido para el operador '" + operator + "'", 0, 0);
        }
        
        // op is matrix, array or list
        return new CompileError("Semantico", "Tipo de operando invalido para el operador '" + operator + "'", 0, 0);
    }
    
    private Vector booleanVectors(ArrayList<Atomic> v1, ArrayList<Atomic> v2, String operator) {
        ArrayList<Atomic> res = new ArrayList<Atomic>();
        
        for (int i = 0; i < v1.size(); i++) {
            boolean one = ((Boolean)v1.get(i).getValue()).booleanValue();
            boolean two = ((Boolean)v2.get(i).getValue()).booleanValue();
            
            boolean flag = false;
            if (operator.equals("&"))
                flag = one && two;
            else 
                flag = one || two;
            
            res.add(new Atomic(Atomic.Type.BOOLEAN, Boolean.valueOf(flag)));
        }
        
        return new Vector(res, 3);
    }
    
    private Vector booleanVector(ArrayList<Atomic> v1, boolean bool, String operator) {
        ArrayList<Atomic> res = new ArrayList<Atomic>();
        
        for (Atomic ob : v1) {
            boolean one = ((Boolean)ob.getValue()).booleanValue();
            
            boolean flag = false;
            if (operator.equals("&"))
                flag = one && bool;
            else 
                flag = one || bool;
            
            res.add(new Atomic(Atomic.Type.BOOLEAN, Boolean.valueOf(flag)));
        }
        
        return new Vector(res, 3);
    }
    
    @Override
    public Object and(SymbolsTable env, Value op) {
        return validateBoolean(env, op, "&");
    }
    
    @Override
    public Object or(SymbolsTable env, Value op) {
        return validateBoolean(env, op, "|");
    }
    
    @Override
    public Object getValue(int i) {
        i--;
        if (i > this.content.size() - 1 || i < 0)
            return new CompileError("Semantico", "Indice de acceso fuera de limites", 0, 0);
        
        Atomic atom = this.content.get(i);
        return new Vector(atom);
    }
    
    @Override
    public Object getValue2B(int i) {
        return new CompileError("Semantico", "El acceso con corchetes dobles [[]] no esta definido para Vectores", 0, 0);
    }
}

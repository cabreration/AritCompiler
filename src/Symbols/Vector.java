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
    
    private Object validateBaldor(Enviroment env, Value op, String operator, int order) {
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
                            Vector thor = baldorVectors(this.content, (ArrayList<Object>)vec.getValue(), 2, operator);
                            if (thor == null)
                                return new CompileError("Semantico", "Las operaciones de division y modulo sobre 0 no estan definidas", 0, 0);
                            
                            return thor;
                        }
                              
                        Vector thor = baldorVectors(this.content, (ArrayList<Object>)vec.getValue(), 1, operator);
                        if (thor == null)
                                return new CompileError("Semantico", "Las operaciones de division y modulo sobre 0 no estan definidas", 0, 0);
                            
                        return thor;
                    }
                        
                    
                    if (vec.type == 2) {
                        Vector thor = baldorVectors(this.content, (ArrayList<Object>)vec.getValue(), 2, operator);
                        if (thor == null)
                                return new CompileError("Semantico", "Las operaciones de division y modulo sobre 0 no estan definidas", 0, 0);
                            
                        return thor;
                    }
                    
                    if (!operator.equals("+"))
                        return new CompileError("Semantico", "Tipo de operando invalido, no se puede aplicar al operador '" + operator + "'", 0, 0);
                }
                
                if (this.type == 2) {
                    if (vec.type == 1 || vec.type == 2) {
                        Vector thor = baldorVectors(this.content, (ArrayList<Object>)vec.getValue(), 2, operator);
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
                            Vector thor = baldorVectors(this.content, (ArrayList<Object>)vec.getValue(), 4, "+");
                            if (thor == null)
                                return new CompileError("Semantico", "Las operaciones de division y modulo sobre 0 no estan definidas", 0, 0);
                            
                            return thor;
                        }
                        
                        return new CompileError("Semantico", "Tipo de operando invalido, no se puede aplicar al operador '" + operator + "'", 0, 0);
                    }
                
                    if (this.type == 4) {
                        Vector thor = baldorVectors(this.content, (ArrayList<Object>)vec.getValue(), 4, "+");
                        if (thor == null)
                            return new CompileError("Semantico", "Las operaciones de division y modulo sobre 0 no estan definidas", 0, 0);
                            
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
                        return new CompileError("Semantico", "Operacion Invalida", 0, 0);
                            
                    return thor;
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
                else if (operator.equals("/")) {
                    if (((Integer)v2.get(i)).intValue()  == 0)
                        return null;
                    
                    r = ((Integer)v1.get(i)).intValue() / ((Integer)v2.get(i)).intValue();
                }
                else if (operator.equals("%")) {
                    if (((Integer)v2.get(i)).intValue()  == 0)
                        return null;
                    
                    r = ((Integer)v1.get(i)).intValue() % ((Integer)v2.get(i)).intValue();
                }
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
    
    private Vector baldorVector(ArrayList<Object> v1, double val, String operator, int order) { 
        ArrayList<Object> res = new ArrayList<Object>();
        
        for (Object ob : v1) {
            String cur = String.valueOf(ob);
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
                    
            res.add(Double.valueOf(r));
        }
        
        return new Vector(res, 2);
    }
    
    private Vector baldorVector(ArrayList<Object> v1, int val, String operator, int order) { 
        ArrayList<Object> res = new ArrayList<Object>();
        
        for (Object ob: v1) {
            int ent = ((Integer)ob).intValue();
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
                    
            res.add(Integer.valueOf(r));
        }
        
        return new Vector(res, 1);
    }
    
    private Vector stringAdding(ArrayList<Object> v1, String val, int order) {
        ArrayList<Object> res = new ArrayList<Object>();
        if (val == null)
            val = "";
        
        for (Object ob: v1) {
            String one = String.valueOf(ob);
            if (one == null)
                one = "";
            
            if (order == 1)
                res.add(one + val);
            else 
                res.add(val + one);
        }
        
        return new Vector(res, 4);
    }
    
    @Override
    public Object minus(Enviroment env, Value op, int order) {
        return validateBaldor(env, op, "-", order);
    }
    
    @Override
    public Object plus(Enviroment env, Value op, int order) {
        return validateBaldor(env, op, "+", order);
    } 
    
    @Override
    public Object times(Enviroment env, Value op, int order) {
        return validateBaldor(env, op, "*", order);
    }
    
    @Override
    public Object div(Enviroment env, Value op, int order) {
        return validateBaldor(env, op, "/", order);
    }
    
    @Override
    public Object mod(Enviroment env, Value op, int order) {
        return validateBaldor(env, op, "%", order);
    } 
    
    @Override
    public Object power(Enviroment env, Value op, int order) {
        return validateBaldor(env, op, "^", order);
    } 
    
    private Object validateRelational(Enviroment env, Value op, String operator, int order) {
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
                    return relationalVectors(this.content, (ArrayList<Object>)vec.getValue(), 2, operator);
                }
                
                return new CompileError("Semantico", "Tipo de operando invalido para el operador '" + operator + "'", 0, 0);
            }
            
            if (this.type == 3) {
                if (!operator.equals("==") && !operator.equals("!="))
                   return new CompileError("Semantico", "Tipo de operando invalido para el operador '" + operator + "'", 0, 0);
                
                if (vec.type == 3) {
                    return relationalVectors(this.content, (ArrayList<Object>)vec.getValue(), 3, operator);
                }
                
                return new CompileError("Semantico", "Tipo de operando invalido para el operador '" + operator + "'", 0, 0);
            }
            
            if (this.type == 4) {
                if (vec.type == 4) {
                    return relationalVectors(this.content, (ArrayList<Object>)vec.getValue(), 4, operator);
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
                    String str = String.valueOf(((Atomic)op).getValue());
                    boolean arg = Boolean.valueOf(str);
                    return relationalVector(this.content, arg, operator);
                }
                
                return new CompileError("Semantico", "Tipo de operando invalido para el operador '" + operator + "'", 0, 0);
            }
            
            if (this.type == 4) {
                if (((Atomic)op).getType() == Atomic.Type.STRING) {
                    String str = String.valueOf(((Atomic)op).getValue());
                    return relationalVector(this.content, str, order, operator);
                }
            }
        }
        
        //operand is an array, list or matrix
        return new CompileError("Semantico", "Tipo de operando invalido para el operador '" + operator + "'", 0, 0);
    }
    
    private Vector relationalVectors(ArrayList<Object> v1, ArrayList<Object> v2, int type, String operator) {
        ArrayList<Object> res = new ArrayList<Object>();
        
        for (int i = 0; i < v1.size(); i++) {
            if (type == 2) {
                String first = String.valueOf(v1.get(i));
                String second = String.valueOf(v2.get(i));
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
                
                res.add(Boolean.valueOf(flag));
            }
            else if (type == 3) {
                String first = String.valueOf(v1.get(i));
                String second = String.valueOf(v2.get(i));
                boolean one = Boolean.valueOf(first);
                boolean two = Boolean.valueOf(second);
                
                boolean flag = false;
                if (operator.equals("=="))
                    flag = one == two;
                else
                    flag = one != two;
                
                res.add(Boolean.valueOf(flag));
            }
            else {
                String one = String.valueOf(v1.get(i));
                String two = String.valueOf(v2.get(i));
                
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
            }
        }
        return new Vector(res, 3);
    }
    
    private Vector relationalVector(ArrayList<Object> v1, double doub, int order, String operator) {
        ArrayList<Object> res = new ArrayList<Object>();
        
        for (Object ob: v1) {
            String one = String.valueOf(ob);
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
            
            res.add(Boolean.valueOf(flag));
        }
        return new Vector(res, 3);
    }
    
    private Vector relationalVector(ArrayList<Object> v1, boolean bool, String operator) {
        ArrayList<Object> res = new ArrayList<Object>();
        
        for (Object ob: v1) {
            String one = String.valueOf(ob);
            boolean dib = Boolean.valueOf(one);
            
            boolean flag  = false;
            switch (operator) {
                case "==":
                    flag = dib == bool;
                    break;
                case "!=":
                    flag = dib != bool;
                    break;
            }
            
            res.add(Boolean.valueOf(flag));
        }
        return new Vector(res, 3);
    }
    
    private Vector relationalVector(ArrayList<Object> v1, String str, int order, String operator) {
        ArrayList<Object> res = new ArrayList<Object>();
        
        for (Object ob: v1) {
            String one = String.valueOf(ob);
            
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
            
            res.add(Boolean.valueOf(flag));
        }
        return new Vector(res, 3);
    }
    
    @Override
    public Object lesser(Enviroment env, Value op, int order) {
        return validateRelational(env, op, "<", order);
    }
    
    @Override
    public Object greater(Enviroment env, Value op, int order) {
        return validateRelational(env, op, ">", order);
    }
    
    @Override
    public Object lesserEquals(Enviroment env, Value op, int order) {
        return validateRelational(env, op, "<=", order);
    }
    
    @Override
    public Object greaterEquals(Enviroment env, Value op, int order) {
        return validateRelational(env, op, ">=", order);
    }
    
    @Override
    public Object equals(Enviroment env, Value op, int order) {
        return validateRelational(env, op, "==", order);
    }
    
    @Override
    public Object notEquals(Enviroment env, Value op, int order) {
        return validateRelational(env, op, "!=", order);
    }
}

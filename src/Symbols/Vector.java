/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Symbols;

import APIServices.CompileError;
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
        return content;
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
    
}

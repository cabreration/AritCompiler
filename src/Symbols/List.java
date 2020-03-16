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
public class List implements Value, Symbol {

    private ArrayList elements;

    public List(ArrayList elements) {
        this.elements = elements;
    }
    
    public List(Object element) {
        this.elements = new ArrayList();
        this.elements.add(element);
    }
    
    public List clonation() {
        ArrayList nu = new ArrayList();
        for (Object obj : this.elements) {
            Object ref = null;
            if (obj instanceof Vector)
                ref = ((Vector)obj).clonation();
            else 
                ref = ((List)obj).clonation();
            nu.add(ref);
        }
        return new List(nu);
    }
    
    @Override
    public Object booleanNegation(SymbolsTable env) {
        return new CompileError("Semantico", "No es posible realizar operaciones con listas", 0, 0);
    }

    @Override
    public Object aritmeticNegation(SymbolsTable env) {
        return new CompileError("Semantico", "No es posible realizar operaciones con listas", 0, 0);
    }

    @Override
    public Object minus(SymbolsTable env, Value op, int order) {
        return new CompileError("Semantico", "No es posible realizar operaciones con listas", 0, 0);
    }

    @Override
    public Object plus(SymbolsTable env, Value op, int order) {
        return new CompileError("Semantico", "No es posible realizar operaciones con listas", 0, 0);
    }

    @Override
    public Object times(SymbolsTable env, Value op, int order) {
        return new CompileError("Semantico", "No es posible realizar operaciones con listas", 0, 0);
    }

    @Override
    public Object div(SymbolsTable env, Value op, int order) {
        return new CompileError("Semantico", "No es posible realizar operaciones con listas", 0, 0);
    }

    @Override
    public Object power(SymbolsTable env, Value op, int order) {
        return new CompileError("Semantico", "No es posible realizar operaciones con listas", 0, 0);
    }

    @Override
    public Object mod(SymbolsTable env, Value op, int order) {
        return new CompileError("Semantico", "No es posible realizar operaciones con listas", 0, 0);
    }

    @Override
    public Object lesser(SymbolsTable env, Value op, int order) {
        return new CompileError("Semantico", "No es posible realizar operaciones con listas", 0, 0);
    }

    @Override
    public Object greater(SymbolsTable env, Value op, int order) {
        return new CompileError("Semantico", "No es posible realizar operaciones con listas", 0, 0);
    }

    @Override
    public Object lesserEquals(SymbolsTable env, Value op, int order) {
        return new CompileError("Semantico", "No es posible realizar operaciones con listas", 0, 0);
    }

    @Override
    public Object greaterEquals(SymbolsTable env, Value op, int order) {
        return new CompileError("Semantico", "No es posible realizar operaciones con listas", 0, 0);
    }

    @Override
    public Object equals(SymbolsTable env, Value op, int order) {
        return new CompileError("Semantico", "No es posible realizar operaciones con listas", 0, 0);
    }

    @Override
    public Object notEquals(SymbolsTable env, Value op, int order) {
        return new CompileError("Semantico", "No es posible realizar operaciones con listas", 0, 0);
    }

    @Override
    public Object and(SymbolsTable env, Value op) {
        return new CompileError("Semantico", "No es posible realizar operaciones con listas", 0, 0);
    }

    @Override
    public Object or(SymbolsTable env, Value op) {
        return new CompileError("Semantico", "No es posible realizar operaciones con listas", 0, 0);
    }

    // Symbol Methods
    @Override
    public Object getValue() {
        return this.elements;
    }

    @Override
    public int getSize() {
        return this.elements.size();
    }

    @Override
    public Object getValue(int i) {
        //Devuelve una lista
        i--;
        if (i > this.elements.size() - 1 || i < 0)
            return new CompileError("Semantico", "Indice de acceso fuera de limites", 0, 0);
        
        Object atom = this.elements.get(i);
        if (atom instanceof Vector)
            atom = ((Vector)atom).clonation();
        else 
            atom = ((List)atom).clonation();
        
        return new List(atom);
    }

    @Override
    public Object getValue2B(int i) {
        //Devuelve un vector
        i--;
        if (i > this.elements.size() - 1 || i < 0)
            return new CompileError("Semantico", "Indice de acceso fuera de limites", 0, 0);
        
        Object atom = this.elements.get(i);
        if (atom instanceof Vector) {
            return atom;
        }
        else {
            // atom is a list
            return new CompileError("Semantico", "El elemento en la posicion " + i + " es una lista", 0, 0);
        }
    }

    @Override
    public void expand(int i) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public void insertValue2B(Object obj, int i) {
    
    }

    @Override
    public void insertValue(Object obj, int i) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}

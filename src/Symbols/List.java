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
public class List implements Value, Symbol {

    private ArrayList<Object> elements;

    public List(ArrayList elements) {
        this.elements = elements;
    }
    
    public List(Object element) {
        this.elements = new ArrayList();
        this.elements.add(element);
    }
    
    public List() {
        this.elements = new ArrayList();
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
            return new CompileError("Semantico", "El elemento en la posicion " + i + " es una lista, no es posible acceder a ella con [[]]", 0, 0);
        }
    }

    @Override
    public void expand(int i) {
        if (i < this.elements.size())
            return;
        
        for (int j = this.elements.size(); j <= i; j++) {
            this.elements.add(new Vector(new Atomic(Atomic.Type.STRING, null)));
        }
    }
    
    @Override
    public void insertValue2B(Object obj, int i) {
        if (obj instanceof Atomic) {
            if (((Atomic)obj).getType() == Atomic.Type.IDENTIFIER) {
                String id = String.valueOf(((Atomic)obj).getValue());
                int line = ((Atomic)obj).getLine();
                int column = ((Atomic)obj).getColumn();
            
                if (obj == null) {
                    Singleton.insertError(new CompileError("Semantico", "La variable '" + id + "' no existe en el contexto actual", line, column));
                    return;
                }
            }
        }
        
        if (obj instanceof List || obj instanceof Vector) {
            this.elements.remove(i);
            this.elements.add(i, obj);
        }
        else {
            this.elements.remove(i);
            this.elements.add(i, new Vector((Atomic)obj));
        }
    }

    @Override
    public void insertValue(Object obj, int i) {
        if (obj instanceof Atomic) {
            if (((Atomic)obj).getType() == Atomic.Type.IDENTIFIER) {
                String id = String.valueOf(((Atomic)obj).getValue());
                int line = ((Atomic)obj).getLine();
                int column = ((Atomic)obj).getColumn();
            
                if (obj == null) {
                    Singleton.insertError(new CompileError("Semantico", "La variable '" + id + "' no existe en el contexto actual", line, column));
                    return;
                }
            }
        }
        
        if (obj instanceof List) {
            if (((List)obj).getSize() > 1) {
                Singleton.insertError(new CompileError("Semantico", "El tipo de acceso [] en listas solo permite asigaciones de tamanio 1", 0, 0));
                return;
            }
            this.elements.remove(i);
            this.elements.add(i, obj);
        }
        else if (obj instanceof Vector) {
            if (((Vector)obj).getSize() > 1) {
                Singleton.insertError(new CompileError("Semantico", "El tipo de acceso [] en listas solo permite asigaciones de tamanio 1", 0, 0));
                return;
            }
            this.elements.remove(i);
            this.elements.add(i, new List(obj));
        }
        else {
            this.elements.remove(i);
            this.elements.add(i, new Vector((Atomic)obj));
        }
    }
    
}

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
public class Matrix implements Symbol, Value{

    private Atomic[][] elements;
    private int nRows;
    private int nCols;
    
    public Matrix(int nRows, int nCols, Vector vector) {
        this.nRows = nRows;
        this.nCols = nCols;
        
        this.elements = new Atomic[this.nRows][this.nCols];
        // now we fill it up
        int size = this.nRows*this.nCols;
        int line = 0;
        int col = 0;
        int pos = 0;
        for (int i = 0; i < size; i++) {
            Atomic atom = ((ArrayList<Atomic>)vector.getValue()).get(pos);
            this.elements[line][col] = atom;
            line++;
            pos++;
            if (pos == vector.getSize())
                pos = 0;
            if (line == this.nRows) {
                line = 0;
                col++;
            }
        }
    }
    
    public int getRows() {
        return this.nRows;
    }
    
    public int getColumns() {
        return this.nCols;
    }
    
    @Override
    public Object getValue() {
        return this.elements;
    }

    @Override
    public int getSize() {
        return this.nRows * this.nCols;
    }

    @Override
    public Object getValue(int i) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object getValue2B(int i) {
        return new CompileError("Semantico", "Las operaciones con [[]] no estan definidas para matrices", 0, 0);
    }

    @Override
    public void expand(int i) {
        // Im not going to use this
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void insertValue(Object obj, int i) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void insertValue2B(Object obj, int i) {
        Singleton.insertError(new CompileError("Semantico", "Las operaciones con [[]] no estan definidas para matrices", 0, 0));
    }
    
    @Override
    public Object accessBoth(int i, int j) {
        return null;
    }
    
    @Override
    public Object accessLeft(int i) {
        return null;
    }
    
    @Override
    public Object accessRight(int j) {
        return null;
    }

    // Value methods
    
    @Override
    public Object booleanNegation(SymbolsTable env) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object aritmeticNegation(SymbolsTable env) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object minus(SymbolsTable env, Value op, int order) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object plus(SymbolsTable env, Value op, int order) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object times(SymbolsTable env, Value op, int order) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object div(SymbolsTable env, Value op, int order) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object power(SymbolsTable env, Value op, int order) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object mod(SymbolsTable env, Value op, int order) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object lesser(SymbolsTable env, Value op, int order) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object greater(SymbolsTable env, Value op, int order) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object lesserEquals(SymbolsTable env, Value op, int order) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object greaterEquals(SymbolsTable env, Value op, int order) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object equals(SymbolsTable env, Value op, int order) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object notEquals(SymbolsTable env, Value op, int order) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object and(SymbolsTable env, Value op) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object or(SymbolsTable env, Value op) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}

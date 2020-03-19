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
    private int type;
    
    public Matrix(int nRows, int nCols, Vector vector, int type) {
        this.nRows = nRows;
        this.nCols = nCols;
        this.type = type;
        
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
    
    public Matrix clonation() {
        // Implementar un clon
        return null;
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
        i--;
        if (i >= this.nRows * this.nCols)
            return new CompileError("Semantico", "El indice de acceso por lista a matriz se encuentra fuera de rango", 0, 0);
        
        int n = 0;
        Atomic atom = null;
        for (int j = 0; j < this.nRows; j++) {
            for (int k = 0; k < this.nCols; k++) {
                if (n == i) {
                    atom = this.elements[i][j];
                    break;
                }
                n++;
            }
        }
        
        return new Vector(atom);
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
        i--;
        if (i >= this.nRows * this.nCols) {
            Singleton.insertError(new CompileError("Semantico", "El indice de acceso por lista a matriz se encuentra fuera de rango", 0, 0));
            return;
        }
        
        if (this.type == 4) {
            obj = new Atomic(Atomic.Type.STRING, String.valueOf(((Atomic)obj).getValue()));
        }
        else if (this.type == 2) {
            if (((Atomic)obj).getType() == Atomic.Type.STRING) 
                castToString();
            else if (((Atomic)obj).getType() == Atomic.Type.INTEGER) {
                double doub = ((Integer)((Atomic)obj).getValue()).doubleValue();
                obj = new Atomic(Atomic.Type.NUMERIC, Double.valueOf(doub));
            }
            else if (((Atomic)obj).getType() == Atomic.Type.BOOLEAN) {
                double doub = ((Boolean)((Atomic)obj).getValue()).booleanValue() ? 1.0 : 0.0;
                obj = new Atomic(Atomic.Type.NUMERIC, Double.valueOf(doub));
            }
        }
        else if (this.type == 1) {
            if (((Atomic)obj).getType() == Atomic.Type.STRING) 
                castToString();
            else if (((Atomic)obj).getType() == Atomic.Type.NUMERIC) 
                castToNumeric();       
            else if (((Atomic)obj).getType() == Atomic.Type.BOOLEAN) {
                int doub = ((Boolean)((Atomic)obj).getValue()).booleanValue() ? 1 : 0;
                obj = new Atomic(Atomic.Type.NUMERIC, Integer.valueOf(doub));
            }
        }
        else {
            if (((Atomic)obj).getType() == Atomic.Type.STRING) 
                castToString();
            else if (((Atomic)obj).getType() == Atomic.Type.NUMERIC) 
                castToNumeric();
            else if (((Atomic)obj).getType() == Atomic.Type.INTEGER) 
                castToInteger();
            
        }
        
        int n = 0;
        for (int j = 0; j < this.nRows; j++) {
            for (int k = 0; k < this.nCols; k++) {
                if (n == i) {
                    this.elements[i][j] = (Atomic)obj;
                    break;
                }
                n++;
            }
        }
    }
    
    private void castToString() {}
    
    private void castToNumeric() {}
    
    private void castToInteger() {}
    
    @Override 
    public void insertValueBoth(Object obj, int i, int j) {}
    
    @Override 
    public void insertValueLeft(Object obj, int i) {}
    
    @Override 
    public void insertValueRight(Object obj, int j) {}

    @Override
    public void insertValue2B(Object obj, int i) {
        Singleton.insertError(new CompileError("Semantico", "Las operaciones con [[]] no estan definidas para matrices", 0, 0));
    }
    
    @Override
    public Object accessBoth(int i, int j) {
        i--;
        j--;
        
        if (i >= this.elements.length)
            return new CompileError("Semantico", "El indice de las filas se encuentra fuera de rango", 0, 0);
        
        if (j >= this.elements[i].length)
            return new CompileError("Semantico", "El indice de las columnas se encuentra fuera de rango", 0, 0);
        
        Atomic atom = this.elements[i][j];
        return new Vector(atom);
    }
    
    @Override
    public Object accessLeft(int i) {
        i--;
        if (i >= this.nRows)
            return new CompileError("Semantico", "El indice de acceso por la izquierda se encuentra fuera de rango", 0, 0);
        
        ArrayList<Atomic> atoms = new ArrayList<Atomic>();
        for (int j = 0; j < this.nCols; j++) {
            atoms.add(this.elements[i][j]);
        }
        
        return new Vector(atoms, this.type);
    }
    
    @Override
    public Object accessRight(int j) {
        j--;
        
        if (j >= this.nCols)
            return new CompileError("Semantico", "El indice de acceso por la derecha se encuentra fuera de rango", 0, 0);
        
        ArrayList<Atomic> atoms = new ArrayList<Atomic>();
        for (int i = 0; i < this.nRows; i++)
            atoms.add(this.elements[i][j]);
        
        return new Vector(atoms, this.type);
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

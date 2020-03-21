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
    
    public int getType() {
        return this.type;
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
        if (i > this.nRows * this.nCols)
            return new CompileError("Semantico", "El indice de acceso por lista a matriz se encuentra fuera de rango", 0, 0);
        
        Atomic atom = null;
        int line = 0;
        int col = 0;
        for (int j = 0; j < i; j++) {
            if (j == i - 1) {
                atom = this.elements[line][col];
                break;
            }
            line++;
            if (line == this.nRows) {
                line = 0;
                col++;
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
        if (i > this.nRows * this.nCols) {
            Singleton.insertError(new CompileError("Semantico", "El indice de acceso por lista a matriz se encuentra fuera de rango", 0, 0));
            return;
        }
        
        if (obj instanceof Vector) {
            if (((Vector)obj).getSize() > 1) {
                Singleton.insertError(new CompileError("Semantico", "No puede asignar mas de un elemento a la posicion", 0, 0));
                return;
            }
            
            obj = ((ArrayList<Atomic>)((Vector)obj).getValue()).get(0);
        }
        
        obj = cast(obj);
        
        int line = 0;
        int col = 0;
        for (int j = 0; j < i; j++) {
            if (j == i - 1) {
                this.elements[line][col] = (Atomic)obj;
                break;
            }
            line++;
            if (line == this.nRows) {
                line = 0;
                col++;
            }
        }
    }
    
    private Atomic cast(Object obj) {
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
                obj = new Atomic(Atomic.Type.INTEGER, Integer.valueOf(doub));
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
        return ((Atomic)obj);
    }
    
    private void castToString() {
        this.type = 4;
        for (int i = 0; i < this.nRows; i++) {
            for (int j = 0; j < this.nCols; j++) {
                this.elements[i][j].setType(Atomic.Type.STRING);
                this.elements[i][j].setValue(String.valueOf(this.elements[i][j].getValue()));
            }
        }
    }
    
    private void castToNumeric() {
        this.type = 2;
        for (int i = 0; i < this.nRows; i++) {
            for (int j = 0; j < this.nCols; j++) {
                if (this.elements[i][j].getType() == Atomic.Type.INTEGER) {
                    double doub = ((Integer)this.elements[i][j].getValue()).doubleValue();
                    this.elements[i][j].setValue(Double.valueOf(doub));
                }
                else if (this.elements[i][j].getType() == Atomic.Type.BOOLEAN) {
                    double doub = ((Boolean)this.elements[i][j].getValue()).booleanValue() ? 1.0 : 0.0;
                    this.elements[i][j].setValue(Double.valueOf(doub));
                }
                this.elements[i][j].setType(Atomic.Type.NUMERIC);
            }
        }
    }
    
    private void castToInteger() {
        this.type = 1;
        for (int i = 0; i < this.nRows; i++) {
            for (int j = 0; j < this.nCols; j++) {
                this.elements[i][j].setType(Atomic.Type.INTEGER);
                int doub = ((Boolean)this.elements[i][j].getValue()).booleanValue() ? 1 : 0;
                this.elements[i][j].setValue(Integer.valueOf(doub));
            }
        }
    }
    
    @Override 
    public void insertValueBoth(Object obj, int i, int j) {
        i--;
        j--;
        
        if (i >= this.elements.length) {
            Singleton.insertError(new CompileError("Semantico", "El indice de las filas se encuentra fuera de rango", 0, 0));
            return;
        }
        
        if (j >= this.elements[i].length) {
            Singleton.insertError(new CompileError("Semantico", "El indice de las columnas se encuentra fuera de rango", 0, 0));
            return;
        }
        
        if (obj instanceof Vector) {
            if (((Vector)obj).getSize() > 1) {
                Singleton.insertError(new CompileError("Semantico", "No puede asignar mas que un elemento a una posicion", 0, 0));
                return;
            }
            
            obj = ((ArrayList<Atomic>)((Vector)obj).getValue()).get(0);
        }
        
        obj = cast(obj);
        this.elements[i][j] = (Atomic)obj;
    }
    
    public void insertValueBothVector(Object obj, int i, int j, int[] next) {
        i--;
        j--;
        
        if (i >= this.elements.length) {
            Singleton.insertError(new CompileError("Semantico", "El indice de las filas se encuentra fuera de rango", 0, 0));
            return;
        }
        
        if (j >= this.elements[i].length) {
            Singleton.insertError(new CompileError("Semantico", "El indice de las columnas se encuentra fuera de rango", 0, 0));
            return;
        }
        
        if (obj instanceof Vector) {
            if (((Vector)obj).getSize() > 1) {
                Singleton.insertError(new CompileError("Semantico", "No puede asignar mas que un elemento a una posicion", 0, 0));
                return;
            }
            
            obj = ((ArrayList<Atomic>)((Vector)obj).getValue()).get(0);
        }

        for (int k = 0; k < next.length; k++) {
            if (next[k] != 1) {
                Singleton.insertError(new CompileError("Semantico", "El indice se encuentra fuera de rango", 0, 0));
                return;
            }
        }
        obj = cast(obj);
        this.elements[i][j] = (Atomic)obj;
    }
    
    @Override 
    public void insertValueLeft(Object obj, int i) {
        i--;
        if (i >= this.nRows) {
            Singleton.insertError(new CompileError("Semantico", "El indice de acceso por la izquierda se encuentra fuera de rango", 0, 0));
            return;
        }
        
        if (obj instanceof Vector) {
            if (((Vector)obj).getSize() == this.nCols) {
                for (int j = 0; j < this.nCols; j++) {
                    Atomic atom = ((ArrayList<Atomic>)(((Vector)obj).getValue())).get(j);
                    atom = cast(atom);
                    this.elements[i][j] = atom;
                }
            }
            else if (((Vector)obj).getSize() == 1) {
                Atomic atom = ((ArrayList<Atomic>)(((Vector)obj).getValue())).get(0);
                atom = cast(atom);
                for (int j = 0; j < this.nCols; j++) {
                    this.elements[i][j] = atom;
                }
            }
            else {
                Singleton.insertError(new CompileError("Semantico", "Cantidad de parametros incorrecta", 0, 0));
                return;
            }
        }
        else {
            obj = cast(obj);
            for (int j = 0; j < this.nCols; j++) {
                this.elements[i][j] = (Atomic)obj;
            }
        }
    }
    
    public void insertValueLeftVectors(Object obj, int i, int[] next) {
        i--;
        if (i >= this.nRows) {
            Singleton.insertError(new CompileError("Semantico", "El indice de acceso por la izquierda se encuentra fuera de rango", 0, 0));
            return;
        }
        
        if (obj instanceof Vector) {
            if (((Vector)obj).getSize() > 1) {
                Singleton.insertError(new CompileError("Semantico", "No puede asignar mas que un elemento a una posicion", 0, 0));
                return;
            }
            
            obj = ((ArrayList<Atomic>)((Vector)obj).getValue()).get(0);
        }
        
        int j = next[0];
        j--;
        
        if (j >= this.nCols) {
            Singleton.insertError(new CompileError("Semantico", "El indice de acceso al vector de la matriz se encuentra fuera de rango", 0, 0));
            return;
        }
        
        if (next.length > 1) {
            for (int k = 1; k < next.length; k++) {
                if (next[k] != 1) {
                    Singleton.insertError(new CompileError("Semantico", "El indice se encuentra fuera de rango para el vector en esa posicion de la matrix", 0, 0));
                    return;
                }
            }
        }
        
        obj = cast(obj);
        this.elements[i][j] = (Atomic)obj;
    }
    
    @Override 
    public void insertValueRight(Object obj, int j) {
        j--;
        
        if (j >= this.nCols) {
            Singleton.insertError(new CompileError("Semantico", "El indice de acceso por la derecha se encuentra fuera de rango", 0, 0));
            return;
        }
        
        if (obj instanceof Vector) {
            if (((Vector)obj).getSize() == this.nRows) {
                for (int i = 0; i < this.nRows; i++) {
                    Atomic atom = ((ArrayList<Atomic>)(((Vector)obj).getValue())).get(i);
                    atom = cast(atom);
                    this.elements[i][j] = atom;
                }
            }
            else if (((Vector)obj).getSize() == 1) {
                Atomic atom = ((ArrayList<Atomic>)(((Vector)obj).getValue())).get(0);
                atom = cast(atom);
                for (int i = 0; i < this.nRows; i++) {
                    this.elements[i][j] = atom;
                }
            }
            else {
                Singleton.insertError(new CompileError("Semantico", "Cantidad de parametros incorrecta", 0, 0));
                return;
            }
        }
        else {
            obj = cast(obj);
            for (int i = 0; i < this.nCols; i++) {
                this.elements[i][j] = (Atomic)obj;
            }
        }
    }
    
    public void insertValueRightVectors(Object obj, int j, int[] next) {
        j--;
        if (j >= this.nCols) {
            Singleton.insertError(new CompileError("Semantico", "El indice de acceso por la izquierda se encuentra fuera de rango", 0, 0));
            return;
        }
        
        if (obj instanceof Vector) {
            if (((Vector)obj).getSize() > 1) {
                Singleton.insertError(new CompileError("Semantico", "No puede asignar mas que un elemento a una posicion", 0, 0));
                return;
            }
            
            obj = ((ArrayList<Atomic>)((Vector)obj).getValue()).get(0);
        }

        int i = next[0];
        i--;
        
        if (i >= this.nRows) {
            Singleton.insertError(new CompileError("Semantico", "El indice de acceso al vector de la matriz se encuentra fuera de rango", 0, 0));
            return;
        }
        
        if (next.length > 1) {
            for (int k = 1; k < next.length; k++) {
                if (next[k] != 1) {
                    Singleton.insertError(new CompileError("Semantico", "El indice se encuentra fuera de rango para el vector en esa posicion de la matrix", 0, 0));
                    return;
                }
            }
        }
        
        obj = cast(obj);
        this.elements[i][j] = (Atomic)obj;
    }

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
    public Atomic typeof(SymbolsTable env) {
        String type = "matrix";
        if (this.type == 1) 
            type += "-integer";
        else if (this.type == 2) 
            type += "-numeric";
        else if (this.type == 3) 
            type += "-boolean";
        else 
            type += "-string";
        
        return new Atomic(Atomic.Type.STRING, type);
    }
    
    @Override
    public Atomic length(SymbolsTable env) {
        int length = this.nRows * this.nCols;
        return new Atomic(Atomic.Type.INTEGER, Integer.valueOf(length));
    }
    
    @Override
    public Atomic nRow(SymbolsTable env) {
        return new Atomic(Atomic.Type.INTEGER, Integer.valueOf(this.nRows));
    }
    
    @Override
    public Atomic nCol(SymbolsTable env) {
        return new Atomic(Atomic.Type.INTEGER, Integer.valueOf(this.nCols));
    }
    
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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Expressions;

import APIServices.CompileError;
import Symbols.List;
import Symbols.Matrix;
import Symbols.Symbol;
import Symbols.SymbolsTable;
import Symbols.Vector;
import java.util.ArrayList;

/**
 *
 * @author jacab
 */
public class MatrixAccess implements Expression {
    private String identifier;
    private int line;
    private int column;
    private int type;
    private Expression left;
    private Expression right;
    private Expression[] vectors;

    public MatrixAccess(String identifier, int line, int column, int type, Expression dister) {
        this.identifier = identifier;
        this.line = line;
        this.column = column;
        this.type = type;
        if (this.type == 1) {
            this.left = dister;
            this.right = null;
        }
        else {
            this.right = dister;
            this.left = null;
        }
        vectors = null;
    }

    public MatrixAccess(String identifier, int line, int column, int type, Expression dister, Expression[] vectors) {
        this.identifier = identifier;
        this.line = line;
        this.column = column;
        this.type = type;
        if (this.type == 1) {
            this.left = dister;
            this.right = null;
        }
        else {
            this.right = dister;
            this.left = null;
        }
        this.vectors = vectors;
    }

    public MatrixAccess(String identifier, int line, int column, Expression left, Expression right) {
        this.identifier = identifier;
        this.line = line;
        this.column = column;
        this.left = left;
        this.right = right;
        this.type = 0;
        this.vectors = null;
    }

    public MatrixAccess(String identifier, int line, int column, Expression left, Expression right, Expression[] vectors) {
        this.identifier = identifier;
        this.line = line;
        this.column = column;
        this.left = left;
        this.right = right;
        this.vectors = vectors;
        this.type = 0;
    }

    public String getIdentifier() {
        return identifier;
    }

    public int getType() {
        return type;
    }

    public Expression getLeft() {
        return left;
    }

    public Expression getRight() {
        return right;
    }

    public Expression[] getVectors() {
        return vectors;
    }

    @Override
    public Object process(SymbolsTable env) {
        Symbol sym = env.getSymbol(this.identifier);
        
        if (sym == null)
            return new CompileError("Semantico", "La variable '" + this.identifier + "' no existe en el contexto actual", this.line, this.column);
        
        if (this.type == 0) {
            Object first = obtainIndex(this.left, env);
            Object second = obtainIndex(this.right, env);
            
            if (first instanceof CompileError)
                return first;
            if (second instanceof CompileError)
                return second;
            
            int i = ((Integer)first).intValue();
            int j = ((Integer)second).intValue();
            
            sym = (Symbol)sym.accessBoth(i, j);
        }
        else if (this.type == 1) {
            Object first = obtainIndex(this.left, env);
            
            if (first instanceof CompileError)
                return first;
            
            int i = ((Integer)first).intValue();
            sym = (Symbol)sym.accessLeft(i);
        }
        else {
            Object second = obtainIndex(this.right, env);
            
            if (second instanceof CompileError)
                return second;
            
            int j = ((Integer)second).intValue();
            sym = (Symbol)sym.accessRight(j);
        }
        
        if (sym instanceof CompileError) {
            if (((CompileError)sym).getRow() == 0 && ((CompileError)sym).getColumn() == 0) {
                ((CompileError)sym).setRow(this.line);
                ((CompileError)sym).setColumn(this.column);
            }
            return sym;
        }
        
        if (this.vectors == null)
            return sym;
        
        //Evaluate the other addresses
        for (Expression exp : this.vectors) {
            Object one = obtainIndex(exp, env);
            
            if (one instanceof CompileError)
                return one;
            
            int i = ((Integer)one).intValue();
            sym = (Symbol)sym.getValue(i);
            
            if (sym instanceof CompileError) {
                if (((CompileError)sym).getRow() == 0 && ((CompileError)sym).getColumn() == 0) {
                    ((CompileError)sym).setRow(this.line);
                    ((CompileError)sym).setColumn(this.column);
                }
                return sym;
            }
        }
        
        return sym;
    }
    
    private Object obtainIndex(Expression i, SymbolsTable env) {
        Object index = i.process(env);
        
        if (index == null)
            return null;
        
        if (index instanceof CompileError) {
            if (((CompileError)index).getRow() == 0 && ((CompileError)index).getColumn() == 0) {
                ((CompileError)index).setRow(this.line);
                ((CompileError)index).setColumn(this.column);
            }
            return index;
        }
        
        if (index instanceof Atomic) {
            if (((Atomic)index).getType() == Atomic.Type.IDENTIFIER) {
                String id = String.valueOf(((Atomic)index).getValue());
                int line = ((Atomic)index).getLine();
                int col = ((Atomic)index).getColumn();
                
                index = env.getSymbol(id);
                if (index == null)
                    return new CompileError("Semantico", "La variable '" + id + "' no existe en el contexto actual", line, col);
            }
        }
        
        if (index instanceof Matrix) {
            index = ((Atomic[][])((Matrix)index).getValue())[0][0];
        }
        
        while (index instanceof List) {
            index = ((ArrayList)((List)index).getValue()).get(0);
        }
            
        if (index instanceof Vector) {
            index = ((ArrayList<Atomic>)(((Vector)index).getValue())).get(0);
        }
        
        if (index instanceof Atomic) {
            if (((Atomic)index).getType() == Atomic.Type.INTEGER) {
                return (Integer)((Atomic)index).getValue();
            }
            else if (((Atomic)index).getType() == Atomic.Type.NUMERIC) {
                double doub = ((Double)((Atomic)index).getValue()).doubleValue();
                doub = doub % 1;
                if (doub != 0)
                    return new CompileError("Semantico", "Unicamente pueden usarse valores enteros como indices", this.line, this.column);
                    
                return Integer.valueOf((int)doub);
            }
            else 
                return new CompileError("Semantico", "Unicamente pueden usarse valores enteros como indices", this.line, this.line);
        }
        return null;
    }
    
}

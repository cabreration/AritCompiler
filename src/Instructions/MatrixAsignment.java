/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Instructions;

import APIServices.CompileError;
import Expressions.Atomic;
import Expressions.Expression;
import Expressions.MatrixAccess;
import Symbols.List;
import Symbols.Matrix;
import Symbols.Symbol;
import Symbols.SymbolsTable;
import Symbols.Vector;
import aritcompiler.Singleton;
import java.util.ArrayList;

/**
 *
 * @author jacab
 */
public class MatrixAsignment implements Instruction {

    private MatrixAccess access;
    private int line;
    private int column;
    private Expression exp;

    public MatrixAsignment(MatrixAccess access, Expression exp, int line, int column) {
        this.access = access;
        this.exp = exp;
        this.line = line;
        this.column = column;
    }
    
    @Override
    public Object process(SymbolsTable env) {
        Symbol sym = env.getSymbol(this.access.getIdentifier());
        
        if (sym == null)
            return new CompileError("Semantico", "La variable '" + this.access.getIdentifier() + "' no existe en el contexto actual", this.line, this.column);
        
        Object res = obtainIndex(exp, env, 2);
        if (res instanceof CompileError) {
            Singleton.insertError((CompileError)res);
            return null;
        }
        
        if (this.access.getType() == 0) {
            Object first = obtainIndex(this.access.getLeft(), env, 1);
            Object second = obtainIndex(this.access.getRight(), env, 1);
            
            if (first instanceof CompileError)
                return first;
            if (second instanceof CompileError)
                return second;
            
            int i = ((Integer)first).intValue();
            int j = ((Integer)second).intValue();
            
            if (this.access.getVectors() == null) {
                sym.insertValueBoth(res, i, j);
                return null;
            }
        }
        else if (this.access.getType() == 1) {
            Object first = obtainIndex(this.access.getLeft(), env, 1);
            
            if (first instanceof CompileError)
                return first;
            
            int i = ((Integer)first).intValue();
            
            if (this.access.getVectors() == null) {
                sym.insertValueLeft(res, i);
                return null;
            }
        }
        else {
            Object second = obtainIndex(this.access.getRight(), env, 1);
            
            if (second instanceof CompileError)
                return second;
            
            int j = ((Integer)second).intValue();
            
            if (this.access.getVectors() == null) {
                sym.insertValueRight(res, j);
                return null;
            }
        }
       
        if (!(sym instanceof Matrix)) {
            Singleton.insertError(new CompileError("Semantico", "El acceso que desea utilizar para la asignacion unicamente esta definido para Matrices", this.line, this.column));
            return null;
        }
        
        int n = this.access.getVectors().length;
        int[] next = new int[n];
        for (int i = 0; i < n; i++) {
            Object index = obtainIndex(this.access.getVectors()[i], env, 1);
            if (index instanceof CompileError) {
                Singleton.insertError((CompileError)index);
                return null;
            }
            next[i] = ((Integer)index).intValue();
        }
        
        if (this.access.getType() == 0) {
            Object first = obtainIndex(this.access.getLeft(), env, 1);
            Object second = obtainIndex(this.access.getRight(), env, 1);
            
            if (first instanceof CompileError)
                return first;
            if (second instanceof CompileError)
                return second;
            
            int i = ((Integer)first).intValue();
            int j = ((Integer)second).intValue();
            ((Matrix)sym).insertValueBothVector(res, i, j, next);
        }
        else if (this.access.getType() == 1) {
            Object first = obtainIndex(this.access.getLeft(), env, 1);
            
            if (first instanceof CompileError)
                return first;
            
            int i = ((Integer)first).intValue();
            ((Matrix)sym).insertValueLeftVectors(res, i, next);
        }
        else {
            Object second = obtainIndex(this.access.getRight(), env, 1);
            
            if (second instanceof CompileError)
                return second;
            
            int j = ((Integer)second).intValue();
            ((Matrix)sym).insertValueRightVectors(res, j, next);
        }
        return null;
    }
    
    private Object obtainIndex(Expression i, SymbolsTable env, int exp) {
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
            if (exp == 2)
                return index;
            
            index = ((ArrayList<Atomic>)(((Vector)index).getValue())).get(0);
        }
        
        if (index instanceof Atomic) {
            if (exp == 2)
                return index;
            
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

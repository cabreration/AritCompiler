/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Instructions;

import APIServices.CompileError;
import Expressions.Atomic;
import Expressions.Expression;
import Symbols.List;
import Symbols.Matrix;
import Symbols.SymbolsTable;
import Symbols.Vector;
import aritcompiler.Singleton;
import java.util.ArrayList;

/**
 *
 * @author jacab
 */
public class Matrix_Function implements Instruction{

    private int line;
    private int column;
    private Expression elements;
    private Expression rowsNumber;
    private Expression columnsNumber;

    public Matrix_Function(int line, int column, Expression elements, Expression rowsNumber, Expression columnsNumber) {
        this.line = line;
        this.column = column;
        this.elements = elements;
        this.rowsNumber = rowsNumber;
        this.columnsNumber = columnsNumber;
    }
    
    @Override
    public Object process(SymbolsTable env) {      
        Object rows = obtainValue(rowsNumber, env);
        Object cols = obtainValue(columnsNumber, env);
        Object vec = obtainValue(elements, env);
        
        if (rows == null)
            return null;
        
        int filas = getDimension(rows);
        if (filas == -1)
            return null;
        
        if (cols == null)
            return null;
        int columnas = getDimension(cols);
        if (columnas == -1)
            return null;
        
        if (vec instanceof Atomic)
            vec = new Vector((Atomic)vec);
        
        int vecSize = ((Vector)vec).getSize();
        int matSize = filas*columnas;
        
        if (matSize >= vecSize) {
            if (matSize % vecSize != 0) {
                Singleton.insertError(new CompileError("Semantico", "La cantidad de elementos en el vector no es multiplo del tamanio del vector", this.line, this.column));
                return null;
            }
        }
        else {
            if (vecSize % matSize != 0) {
                Singleton.insertError(new CompileError("Semantico", "La cantidad de elementos en el vector no es multiplo del tamanio del vector", this.line, this.column));
                return null;
            }
        }
        
        Matrix matrix = new Matrix(filas, columnas, (Vector)vec, ((Vector)vec).type());
        return matrix;
    }
    
    private Object obtainValue(Expression exp, SymbolsTable env) {
        Object obj = exp.process(env);
        
        if (obj instanceof CompileError) {
            Singleton.insertError((CompileError)obj);
            return null;
        }
        
        if (obj instanceof Atomic) {
            if (((Atomic)obj).getType() == Atomic.Type.IDENTIFIER) {
                String id = String.valueOf(((Atomic)obj).getValue());
                int line = ((Atomic)obj).getLine();
                int col = ((Atomic)obj).getColumn();
                
                obj = env.getSymbol(id, line);
                if (obj == null) {
                    Singleton.insertError(new CompileError("Semantico", "La variable '" + id + "' no existe en el contexto actual", line, col));
                    return null;
                }
            }
        }
        
        if (obj instanceof Matrix)
            obj = ((Atomic[][])((Matrix)obj).getValue())[0][0];
        
        while (obj instanceof List) {
            obj = ((ArrayList<Object>)((List)obj).getValue()).get(0);
        }
        
        return obj;
    }
    
    private int getDimension(Object obj) {
        if (obj instanceof Vector) {
            if (((Vector)obj).type() == 1 || ((Vector)obj).type() == 2) {
                obj = ((ArrayList<Atomic>)((Vector)obj).getValue()).get(0);
            }
            else {
                Singleton.insertError(new CompileError("Semantico", "Unicamente tipos de datos numericos pueden usarse como dimensiones", this.line, this.column));
                return -1;
            }
        }
        
        if (obj instanceof Atomic) {
            if (((Atomic)obj).getType() == Atomic.Type.INTEGER)
                return ((Integer)((Atomic)obj).getValue()).intValue();
            else {
                // NUMERIC
                double doub = ((Double)((Atomic)obj).getValue()).doubleValue();
                if (doub % 1 == 0) {
                    return (int)doub;
                }
                else {
                    Singleton.insertError(new CompileError("Semantico", "Unicamente valores de tipo entero pueden ser usados como dimensiones", this.line, this.column));
                    return -1;
                }
            }
        }
        return -1;
    }
}

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
import java.util.ArrayList;

/**
 *
 * @author jacab
 */
public class Mean implements Instruction {

    private Expression vector;
    private Expression trim;
    private int line;
    private int column;

    public Mean(Expression vector, Expression trim, int line, int column) {
        this.vector = vector;
        this.trim = trim;
        this.line = line;
        this.column = column;
    }

    public Mean(Expression vector, int line, int column) {
        this.vector = vector;
        this.line = line;
        this.column = column;
        this.trim = null;
    }
    
    @Override
    public Object process(SymbolsTable env) {
        Object vec = validate(env, this.vector, 1);
        
        if (vec == null)
            return null;
        
        if (vec instanceof CompileError)
            return vec;
        
        if (((Vector)vec).type() > 2) 
            return new CompileError("Semantico", "Solo calcularse la media sobre vectores de tipo numerico", this.line, this.column);
        
        if (this.trim == null) {
            return calculateMean((Vector)vec, 0, false);
        }
        else {
            Object trim = validate(env, this.trim, 2);
            if (trim == null)
                return null;
            if (trim instanceof CompileError)
                return trim;
            
            if (((Atomic)trim).getType() == Atomic.Type.STRING || ((Atomic)trim).getType() == Atomic.Type.BOOLEAN)
                return new CompileError("Semantico", "El valor del parametro trim solo puede ser numerico", this.line, this.column);
            
            double trimmer = 0.0;
            if (((Atomic)trim).getType() == Atomic.Type.NUMERIC) 
                trimmer = ((Double)(((Atomic)trim).getValue())).doubleValue();
            else 
                trimmer = ((Integer)(((Atomic)trim).getValue())).doubleValue();
            
            return calculateMean((Vector)vec, trimmer, true);
        }
    }
    
    private Object validate(SymbolsTable env, Expression exp, int type) {
        Object res = exp.process(env);
        
        if (res == null)
            return null;
        
        if (res instanceof CompileError) {
            if (((CompileError)res).getRow() == 0 && ((CompileError)res).getColumn() == 0) {
                ((CompileError)res).setRow(this.line);
                ((CompileError)res).setColumn(this.column);
            }
            return res;
        }
        
        if (res instanceof Atomic) {
            if (((Atomic)res).getType() == Atomic.Type.IDENTIFIER) {
                String id = String.valueOf(((Atomic)res).getValue());
                int line = ((Atomic)res).getLine();
                int column = ((Atomic)res).getColumn();
                
                res = env.getSymbol(id, line);
                if (res == null) {
                    CompileError error = new CompileError("Semantico", "La variable '" + id + "' no existe en el contexto actual", line, column);
                    return error;
                }
            }
        }
        
        if (res instanceof Matrix) {
            res = ((Atomic[][])(((Matrix)res).getValue()))[0][0];
        }
        
        while (res instanceof List) {
            res = ((ArrayList<Object>)((List)res).getValue()).get(0);
        }
        
        if (res instanceof Vector) {
            if (type == 1)
                return res;
            else
                res = ((ArrayList<Atomic>)((Vector)res).getValue()).get(0);
        }
        
        if (res instanceof Atomic) {
            if (type == 1)
                return new Vector((Atomic)res);
            else 
                return res;
        }
        
        return null;
    }
    
    private Object calculateMean(Vector vector, double trim, boolean shouldI) {
        ArrayList<Atomic> elements = (ArrayList<Atomic>)vector.getValue();
        
        if (shouldI) {
            ArrayList<Atomic> nu = new ArrayList<Atomic>();
            for (Atomic atom : elements) {
                if (((Number)atom.getValue()).doubleValue() >= trim) 
                    nu.add(atom);
            }
            
            if (nu.size() > 0)
                elements = nu;
        }
        
        double pool = 0;
        for (Atomic atom : elements) {
            pool += ((Number)atom.getValue()).doubleValue();
        }
        pool = pool / elements.size();
        return new Atomic(Atomic.Type.NUMERIC, Double.valueOf(pool));
    }
}

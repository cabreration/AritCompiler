/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Instructions;

import APIServices.CompileError;
import Expressions.Atomic;
import Expressions.Expression;
import Symbols.Symbol;
import Symbols.SymbolsTable;
import Symbols.Vector;
import aritcompiler.Singleton;
import java.util.ArrayList;

/**
 *
 * @author jacab
 */
public class Concat implements Instruction {
    private ArrayList<Object> params; 
    private int line;
    private int column;

    public Concat(ArrayList<Object> params, int line, int column) {
        this.params = params;
        this.line = line;
        this.column = column;
    }

    @Override
    public Object process(SymbolsTable env) {
        ArrayList<Object> elements = new ArrayList<Object>();
        for (Object param : this.params) {
            Object val = ((Expression)param).process(env);
            
            if (val == null)
                return null;
            if (val instanceof CompileError) {
                Singleton.insertError((CompileError)val);
                return null;
            }
            
            elements.add(val);
        }
        int type = determineType(elements, env);
        if (type == 0)
            return null;
        
        ArrayList<Object> ret = null;
        if (type == 3)
            ret = castToString(elements);
        else if (type == 4)
            ret = castToNumeric(elements);
        else if (type == 5)
            ret = castToInt(elements);
        else 
            ret = castToBoolean(elements);
        
        return ret;
    }
    
    private int determineType(ArrayList<Object> elements, SymbolsTable env) {
        int type = 6; // 1 - list, 2 - vector, 3 - string, 4 - numeric, 5 - integer, 6 - boolean
        for (Object element : elements) {
            //if (element instanceof List) {}
            if (element instanceof Vector) 
                type = type > 2 ? 2 : type;
            else if (element instanceof Atomic) {
                if (((Atomic)element).getType() == Atomic.Type.IDENTIFIER) {
                    String id = String.valueOf(((Atomic)element).getValue());
                    int lin = ((Atomic)element).getLine();
                    int col = ((Atomic)element).getColumn();
                    Symbol sym = env.getSymbol(id);
                    
                    if (sym == null) {
                        Singleton.insertError(new CompileError("Semantico", "La variable '" + id + "' no ha sido declarada", lin, col));
                        return 0;
                    }
                    
                    if (sym instanceof Vector)
                        type = type > 2 ? 2 : type;
                    /* FALTAN LIST, ARRAY Y MATRIX */
                }
                else {
                    if (((Atomic)element).getType() == Atomic.Type.STRING)
                        type = type > 3 ? 3 : type;
                    else if (((Atomic)element).getType() == Atomic.Type.NUMERIC)
                        type = type > 4 ? 4 : type;
                    else if (((Atomic)element).getType() == Atomic.Type.INTEGER)
                        type = type > 5 ? 5 : type;
                }
            }
        }
        return type;
    }
    
    private ArrayList<Object> castToString(ArrayList<Object> elements) {
        ArrayList<Object> strings = new ArrayList<Object>();
        for (Object element: elements) {
            String val = String.valueOf(element);
            strings.add(val);
        }
        return strings;
    }
    
    private ArrayList<Object> castToNumeric(ArrayList<Object> elements) {
        ArrayList<Object> doubles = new ArrayList<Object>();
        for (Object element: elements) {
            if (((Atomic)element).getType() == Atomic.Type.BOOLEAN) {
                if (((Boolean)(((Atomic)element).getValue())).booleanValue())
                    doubles.add(Double.valueOf(1.0));
                else 
                    doubles.add(Double.valueOf(0.0));
            }
            else {
                Double doub = ((Integer)((Atomic)element).getValue()).doubleValue();
                doubles.add(doub);
            }
        }
        return doubles;
    }
    
    private ArrayList<Object> castToInt(ArrayList<Object> elements) {
        ArrayList<Object> inters = new ArrayList<Object>();
        for (Object obj : elements) {
            if (((Atomic)obj).getType() == Atomic.Type.BOOLEAN) {
                if (((Boolean)(((Atomic)obj).getValue())).booleanValue())
                    inters.add(Integer.valueOf(1));
                else 
                    inters.add(Double.valueOf(0));
            } 
        }
        return inters;
    }
    
    private ArrayList<Object> castToBoolean(ArrayList<Object> elements) {
        ArrayList<Object> booleans = new ArrayList<Object>();
        for (Object obj : elements) {
            booleans.add(((Atomic)obj).getValue());
        }
        return booleans;
    }
}

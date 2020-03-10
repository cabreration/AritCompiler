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
            
            if (val instanceof CompileError) {
                Singleton.insertError((CompileError)val);
                return null;
            }
            
            elements.add(val);
        }
        int type = determineType(elements, env);
        if (type == 0)
            return null;
        
        /* if (type == 1) {} else if (type == 2) {} */
        
        Vector ret = null;
        if (type == 3) {
            ArrayList<Atomic> content = castToString(elements);
            ret = new Vector(content, 4);
        }
        else if (type == 4) {
            ArrayList<Atomic> content = castToString(elements);
            ret = new Vector(content, 2);
        }
        else if (type == 5) {
            ArrayList<Atomic> content = castToString(elements);
            ret = new Vector(content, 1);
        }
        else {
            ArrayList<Atomic> content = castToString(elements);
            ret = new Vector(content, 3);
        }
        
        return ret;
    }
    
    private int determineType(ArrayList<Object> elements, SymbolsTable env) {
        int type = 6; // 1 - list, 2 - vector, 3 - string, 4 - numeric, 5 - integer, 6 - boolean
        for (Object element : elements) {
            if (element == null)
                type = type > 3 ? 3 : type;
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
    
    private ArrayList<Atomic> castToString(ArrayList<Object> elements) {
        ArrayList<Atomic> strings = new ArrayList<Atomic>();
        for (Object element: elements) {
            String val = String.valueOf(((Atomic)element).getValue());
            strings.add(new Atomic(Atomic.Type.STRING, val));
        }
        return strings;
    }
    
    private ArrayList<Atomic> castToNumeric(ArrayList<Object> elements) {
        ArrayList<Atomic> doubles = new ArrayList<Atomic>();
        for (Object element: elements) {
            if (((Atomic)element).getType() == Atomic.Type.BOOLEAN) {
                if (((Boolean)(((Atomic)element).getValue())).booleanValue())
                    doubles.add(new Atomic(Atomic.Type.NUMERIC, Double.valueOf(1.0)));
                else 
                    doubles.add(new Atomic(Atomic.Type.NUMERIC, Double.valueOf(0.0)));
            }
            else {
                Double doub = ((Integer)((Atomic)element).getValue()).doubleValue();
                doubles.add(new Atomic(Atomic.Type.NUMERIC, doub));
            }
        }
        return doubles;
    }
    
    private ArrayList<Atomic> castToInt(ArrayList<Object> elements) {
        ArrayList<Atomic> inters = new ArrayList<Atomic>();
        for (Object obj : elements) {
            if (((Atomic)obj).getType() == Atomic.Type.BOOLEAN) {
                if (((Boolean)(((Atomic)obj).getValue())).booleanValue())
                    inters.add(new Atomic(Atomic.Type.INTEGER, Integer.valueOf(1)));
                else 
                    inters.add(new Atomic(Atomic.Type.INTEGER, Integer.valueOf(0)));
            } 
        }
        return inters;
    }
    
    private ArrayList<Atomic> castToBoolean(ArrayList<Object> elements) {
        ArrayList<Atomic> booleans = new ArrayList<Atomic>();
        for (Object obj : elements) {
            booleans.add((Atomic)obj);
        }
        return booleans;
    }
}
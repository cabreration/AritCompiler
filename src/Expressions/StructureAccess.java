/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Expressions;

import APIServices.CompileError;
import Symbols.Address;
import Symbols.Symbol;
import Symbols.SymbolsTable;
import Symbols.Vector;
import java.util.ArrayList;

/**
 *
 * @author jacab
 */
public class StructureAccess implements Expression {
    private String identifier;
    private int line;
    private int column;
    private Address[] direcciones;

    public StructureAccess(String identifier, int line, int column, Address[] direcciones) {
        this.identifier = identifier;
        this.line = line;
        this.column = column;
        this.direcciones = direcciones;
    }

    public String getIdentifier() {
        return identifier;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    public Address[] getDirecciones() {
        return direcciones;
    }
    
    @Override
    public Object process(SymbolsTable env) {
        Object sym = env.getSymbol(this.identifier);
        if (sym == null) {
            return new CompileError("Semantico", "La variable '" + this.identifier + "' no existe en el contexto actual", this.line, this.column);
        }
        
        for (Address address : this.direcciones) {
            if (sym instanceof CompileError)
                return sym;
            
            if (!(sym instanceof Symbol))
                return new CompileError("Semantico", "Algo aqui salio mal y no deberia haber pasado", this.line, this.column);
            
            int index = 0;
            Object res = address.getAddress().process(env);
            
            if (res instanceof Atomic) {
                if (((Atomic)res).getType() == Atomic.Type.IDENTIFIER) {
                    String id = String.valueOf(((Atomic)res).getValue());
                    int line = ((Atomic)res).getLine();
                    int col = ((Atomic)res).getColumn();
                
                    res = env.getSymbol(id);
                    if (res == null)
                        return new CompileError("Semantico", "La variable '" + id + "' no existe en el contexto actual", line, col);
                }
            }
            
            /* FALTAN MATRIX, LIST, ARRAY */
            
            if (res instanceof Vector) {
                res = ((ArrayList<Atomic>)(((Vector)res).getValue())).get(0);
            }
            
            if (res instanceof Atomic) {
                if (((Atomic) res).getType() == Atomic.Type.INTEGER) {
                    index = ((Integer)((Atomic)res).getValue()).intValue();
                }
                else if (((Atomic)res).getType() == Atomic.Type.NUMERIC) {
                    double doub = ((Double)((Atomic)res).getValue()).doubleValue();
                    doub = doub % 1;
                    if (doub != 0)
                        return new CompileError("Semantico", "Unicamente pueden usarse valores enteros como indices", this.line, this.column);
                    
                    index = (int)doub;
                }
                else 
                    return new CompileError("Semantico", "Unicamente pueden usarse valores enteros como indices", this.line, this.line);
                
                if (address.getType() == 1)
                    sym = ((Symbol)sym).getValue(index);
                else 
                    sym = ((Symbol)sym).getValue2B(index);
                continue;
                
            }
            
            /* MATRIX, ARRAY, LIST */
            
            return new CompileError("Semantico", "Unicamente se aceptan valores enteros como indices", this.line, this.column);
        }
        return sym;
    }
}

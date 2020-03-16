/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Instructions;

import APIServices.CompileError;
import Expressions.Atomic;
import Expressions.Expression;
import Symbols.Address;
import Symbols.List;
import Symbols.Symbol;
import Symbols.SymbolsTable;
import Symbols.Vector;
import aritcompiler.Singleton;
import java.util.ArrayList;

/**
 *
 * @author jacab
 */
public class StructureAsignment implements Instruction {

    private String id;
    private int line;
    private int column;
    private Address[] addresses;
    private Expression expression;

    public StructureAsignment(String id, int line, int column, Address[] addresses, Expression expression) {
        this.id = id;
        this.line = line;
        this.column = column;
        this.addresses = addresses;
        this.expression = expression;
    }
    
    @Override
    public Object process(SymbolsTable env) {
        Object sym = env.getSymbol(this.id);
        if (sym == null) {
            return new CompileError("Semantico", "La variable '" + this.id + "' no existe en el contexto actual", this.line, this.column);
        }
        
        Object val = expression.process(env);
        if (val == null) {
            Singleton.insertError(new CompileError("Semantico", "La funcion no devolvio ningun valor", this.line, this.column));
            return null;
        }
        else if (val instanceof CompileError) {
            if (((CompileError)val).getRow() == 0 && ((CompileError)val).getColumn() == 0) {
                ((CompileError)val).setRow(this.line);
                ((CompileError)val).setColumn(this.column);
            }
            Singleton.insertError((CompileError)val);
            return null;
        }
        
        if (sym instanceof Vector) 
            onVector((Symbol)sym, env);
        else if (sym instanceof List)
            onList((List)sym, env, 0, this.addresses.length - 1);
        
           /*if (sym instanceof Array) {}
           else {} // matrix
        */
        return null;
    }
    
    private void onVector(Symbol sym, SymbolsTable env) {
        if (addresses[0].getType() == 1) {
            Object index = findIndex(addresses[0].getAddress(), env);
            if (index instanceof CompileError) {
                Singleton.insertError((CompileError)index);
                return;
            }
                
            int i = ((Integer)index).intValue();
                
            for (int j = 1; j < addresses.length; j++) {
                if (addresses[j].getType() == 2) {
                    Singleton.insertError(new CompileError("Semantico", "El tipo de acceso [[]] no esta definido para vectores", this.line, this.column));
                    return;
                }
                
                Object dex = findIndex(addresses[j].getAddress(), env);
                if (dex instanceof CompileError) {
                    Singleton.insertError((CompileError)dex);
                    return;
                }
                if (((Integer)dex).intValue() != 1) {
                    Singleton.insertError(new CompileError("Semantico", "La direccion a la que esta intentado acceder no existe", this.line, this.column));
                    return;
                }
                     
            }
            
            Object res = expression.process(env);
            
            if (res instanceof Atomic) {
                if (((Atomic)res).getType() == Atomic.Type.IDENTIFIER) {
                    String ident = String.valueOf(((Atomic)res).getValue());
                    int line = ((Atomic)res).getLine();
                    int col = ((Atomic)res).getColumn();
                    res = env.getSymbol(ident);
                    
                    if (res == null) {
                        Singleton.insertError(new CompileError("Semantico", "La variable '" + ident + "' no existe en el contexto actual", line, col));
                        return;
                    }
                }
            }
            
            if (res instanceof Vector) {
                if (((Vector)res).getSize() > 1) {
                    Singleton.insertError(new CompileError("Semantico", "No se puede asignar mas de un valor a la misma posicion", this.line, this.column));
                    return;
                }
                
                res = ((ArrayList<Atomic>)(((Vector)res).getValue())).get(0);
            }
            /* if (res instanceof Matrix) { Error } */
            /* if (res instanceof Array) { Error } */
            /* if (res instanceof List) { Error } */
            
            i--;
            ((Vector)sym).expand(i);  
            ((Vector)sym).insertValue(res, i);
        }
            
        Singleton.insertError(new CompileError("Semantico", "El tipo de acceso [[]] no esta definido para vectores", this.line, this.column));
    }
    
    private Object findIndex(Expression exp, SymbolsTable env) {
        int index = 0;
        Object res = exp.process(env);
            
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
        
        while (res instanceof List) 
            res = ((ArrayList<Object>)(((List)res).getValue())).get(0);
        
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
        }
                /* MATRIX, ARRAY */
        return Integer.valueOf(index);
    }
    
    private Object onList(Symbol sym, SymbolsTable env, int pos, int last) {
        if (sym instanceof Vector) {
            if (this.addresses[pos].getType() == 2) {
                Singleton.insertError(new CompileError("Semantico", "El acceso [[]] no esta definido para vectores", this.line, this.column));
                return null;
            }
        }
        
        Object index = findIndex(this.addresses[pos].getAddress(), env);
        if (index instanceof CompileError) {
            Singleton.insertError((CompileError)index);
            return null;
        }
                
        int i = ((Integer)index).intValue();
        i--;
        sym.expand(i);
        
        Object res = null;
        if (pos == last) {
            res = expression.process(env);
            
            if (res instanceof Atomic) {
                if (((Atomic)res).getType() == Atomic.Type.IDENTIFIER) {
                    String ident = String.valueOf(((Atomic)res).getValue());
                    int line = ((Atomic)res).getLine();
                    int col = ((Atomic)res).getColumn();
                    res = env.getSymbol(ident);
                    
                    if (res == null) {
                        Singleton.insertError(new CompileError("Semantico", "La variable '" + ident + "' no existe en el contexto actual", line, col));
                        return null;
                    }
                }
            }
            /*Si es matrix o array error*/
        }
        
        if (pos == last) {
            if (this.addresses[pos].getType() == 1)
                sym.insertValue(res, i);
            else 
                sym.insertValue2B(res, i);
                
            return sym;
        }
        else {
            Object nu = null;
            if (this.addresses[pos].getType() == 1) {
                nu = sym.getValue(i+1);
            
                if (nu instanceof List) {
                    nu = ((ArrayList<Object>)(((List)nu).getValue())).get(0);
                    if (nu instanceof Vector)
                        nu = new List();
                }
                
                if (nu instanceof Vector) {
                    if (((Vector)nu).getValue() == null) {
                        nu = new List();
                    }
                }
            }
            else 
                nu = sym.getValue2B(i+1);
            
            if (nu instanceof CompileError) {
                Singleton.insertError((CompileError)nu);
                return null;
            }
                
            Object ject = onList((Symbol)nu, env, pos+1, last);
            if (ject == null) 
                return null;
            else {
                if (this.addresses[pos+1].getType() == 1)
                    sym.insertValue(ject, i);
                else 
                    sym.insertValue2B(ject, i);
                return sym;
            }   
        }
    }
}

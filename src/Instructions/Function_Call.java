/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Instructions;

import APIServices.CompileError;
import Expressions.Atomic;
import Expressions.Expression;
import Symbols.Function;
import Symbols.List;
import Symbols.Parameter;
import Symbols.Symbol;
import Symbols.SymbolsTable;
import Symbols.Vector;
import aritcompiler.Singleton;
import java.util.ArrayList;
import Symbols.Matrix;

/**
 *
 * @author jacab
 */
public class Function_Call implements Instruction, Expression {
    private String name;
    private ArrayList<Object> params;
    private int line;
    private int column;

    public Function_Call(String name, ArrayList<Object> params, int line, int column) {
        this.name = name;
        this.params = params;
        this.line = line;
        this.column = column;
    }

    public Function_Call(String name, int line, int column) {
        this.name = name;
        this.line = line;
        this.column = column;
        this.params = new ArrayList<Object>();
    }
    
    public String getName() {
        return name;
    }

    public ArrayList<Object> getParams() {
        return params;
    }

    @Override
    public Object process(SymbolsTable env) {
        Object retorno = null;
        //Funciones Nativas
        switch (name.toLowerCase()) {
            case "print":
                Print(env);
                break;
            case "c":
                retorno = Concat(env);
                break;
            case "list":
                retorno = List(env);
                break;
            case "matrix":
                retorno = Matrix(env);
                break;
            case "length":
                break;
            case "ncol":
                break;
            case "nrow":
                break;
            case "stringlength":
                break;
            case "remove":
                break;
            case "tolowercase":
                break;
            case "touppercase":
                break;
            case "trunk":
                break;
            case "round":
                break;
            case "mean":
                break;
            case "median":
                break;
            case "mode":
                break;
            case "barplot":
                break;
            case "pie":
                break;
            case "plot":
                break;
            case "hist":
                break;
        }
        
        Function f = Singleton.getFunction(this.name);
        if (f == null)
            Singleton.insertError(new CompileError("Semantico", "La funcion '" + this.name + "' no existe", this.line, this.column));
        else 
            retorno = executeFunction(f, env);
        
        return retorno;
    }
    
    private boolean validateNoDefault() {
        for (Object obj : this.params) {
            if (String.valueOf(obj).equals("default"))
                return false;
        }
        return true;
    }
    
    private Object Print(SymbolsTable env) {
        if (params.size() != 1)
            Singleton.insertError(new CompileError("Semantico", "La funcion print recibe una sola expresion como argumento", this.line, this.column));
        
        if (!(params.get(0) instanceof Expression))
            Singleton.insertError(new CompileError("Semantico", "La funcion print no puede tener default como parametro", this.line, this.column));
        
        Print printer = new Print((Expression)this.params.get(0));
        String result = (String)printer.process(env);
        Singleton.insertPrint(result);
        return null;
    }
    
    private Symbol Concat(SymbolsTable env) {
        if (!validateNoDefault()) {
            Singleton.insertError(new CompileError("Semantico", "La funcion C no acepta 'default' como parametro", this.line, this.column));
            return null;
        }
        
        if (params.size() == 0) {
            Singleton.insertError(new CompileError("Semantico","No puede realizar un funcion de concatenacion sin argumentos", this.line, this.column));
            return null;
        }
        
        Concat concatenator = new Concat(this.params, this.line, this.column);
        Symbol sym = (Symbol)concatenator.process(env);
        return sym;
    }
    
    private Symbol List(SymbolsTable env) {
        if (!validateNoDefault()) {
            Singleton.insertError(new CompileError("Semantico", "La funcion C no acepta 'default' como parametro", this.line, this.column));
            return null;
        }
        if (params.size() == 0) {
            Singleton.insertError(new CompileError("Semantico","No puede realizar un funcion de concatenacion sin argumentos", this.line, this.column));
            return null;
        }
        
        List_Function listator = new List_Function(this.line, this.column, this.params);
        List list = (List)listator.process(env);
        return list;
    }
    
    private Matrix Matrix(SymbolsTable env) {
        if (!validateNoDefault()) {
            Singleton.insertError(new CompileError("Semantico", "La funcion Matrix no acepta 'default' como parametro", this.line, this.column));
            return null;
        }
        if (params.size() != 3) {
            Singleton.insertError(new CompileError("Semantico","No puede realizar un funcion de matrix con ese numero de argumentos", this.line, this.column));
            return null;
        }
        
        Matrix_Function matrixator = new Matrix_Function(this.line, this.column,
                (Expression)this.params.get(0), (Expression)this.params.get(1), (Expression)this.params.get(2));
        Matrix matrix = (Matrix)matrixator.process(env);
        return matrix;
    }

    private Object executeFunction(Function f, SymbolsTable env) {
        if (f.getParameters() == null) {
            // Una funcion sin parametros
            if (this.params != null) {
                return new CompileError("Semantico", "La funcion '" + this.name + "' no acepta parametros", this.line, this.column);
            }
            
            Object ret = null;
            SymbolsTable local = new SymbolsTable("function", env);
            for (Instruction ins : f.getSentences()) {
                ret = ins.process(local);
                if (ret != null && ret instanceof Return_Sentence) {
                    env.update(local);
                    return ((Return_Sentence)ret).getProcessedValue();
                } 
            }
        }
        else {
            // Una funcion con parametros
            if (f.getParameters().size() != this.params.size()) {
                return new CompileError("Semantico", "La funcion '" + f.getName() + "' acepta unicamente " + f.getParameters().size() + " parametros", this.line, this.column);
            }
            
            Object ret = null;
            SymbolsTable local = new SymbolsTable("function", env);
            for (Parameter param : f.getParameters()) {
                local.deleteSymbol(param.getName());
            }
            
            for (int i = 0; i < this.params.size(); i++) {
                Object param = this.params.get(i);
                if (param instanceof String) {
                    // Default value
                    Parameter p = f.getParameters().get(i);
                    if (p.getDefaultValue() == null)
                        return new CompileError("Semantico", "El parametro no tiene un valor por defecto definido", this.line, this.column);
                    
                    Object value = p.getDefaultValue().process(env);
                    if (value instanceof CompileError) {
                        if (((CompileError)value).getRow() == 0 && ((CompileError)value).getColumn() == 0) {
                            ((CompileError)value).setRow(this.line);
                            ((CompileError)value).setColumn(this.column);
                        }
                        return value;
                    }
                    
                    if (value instanceof Atomic) {
                        if (((Atomic)value).getType() == Atomic.Type.IDENTIFIER) {
                            String id = String.valueOf(((Atomic)value).getValue());
                            int line = ((Atomic)value).getLine();
                            int column = ((Atomic)value).getColumn();
                            
                            value = env.getSymbol(id);
                            if (value == null)
                                return new CompileError("Semantico", "La variable '" + id + "' no existe en el contexto actual", line, column);
                        }
                    }
                    
                    while (value instanceof List)
                        value = ((ArrayList<Object>)((List)value).getValue()).get(0);
                    
                    if (value instanceof Vector) {
                        Vector nu = ((Vector)value).clonation();
                        local.updateSymbol(p.getName(), nu);
                    }
                    else if (value instanceof Atomic) {
                        Atomic at = ((Atomic)value).clonation();
                        Vector nu = new Vector(at);
                        local.updateSymbol(p.getName(), nu);
                    }
                    /* MATRIX, ARRAY */
                }
                else {
                    Parameter p = f.getParameters().get(i);
                    Expression exp = (Expression)param;
                    Object val = exp.process(env);
                    if (val instanceof CompileError) {
                        return val;
                    }
                    
                    if (val instanceof Atomic) {
                        if (((Atomic)val).getType() == Atomic.Type.IDENTIFIER) {
                            String id = String.valueOf(((Atomic)val).getValue());
                            int line = ((Atomic)val).getLine();
                            int column = ((Atomic)val).getColumn();
                            
                            val = env.getSymbol(id);
                            if (val == null)
                                return new CompileError("Semantico", "La variable '" + id + "' no existe en el contexto actual", line, column);
                        }
                    }
                    
                    while (val instanceof List)
                        val = ((ArrayList<Object>)((List)val).getValue()).get(0);
                    
                    if (val instanceof Vector) {
                        Vector nu = ((Vector)val).clonation();
                        local.updateSymbol(p.getName(), nu);
                    }
                    else if (val instanceof Atomic) {
                        Atomic at = ((Atomic)val).clonation();
                        Vector nu = new Vector(at);
                        local.updateSymbol(p.getName(), nu);
                    }
                    /* MATRIX, ARRAY, LIST */
                }
            }
            
            for (Instruction ins : f.getSentences()) {
                ret = ins.process(local);
                if (ret != null && ret instanceof Return_Sentence) {
                    for (Parameter param : f.getParameters())
                        local.deleteSymbol(param.getName());
                    
                    env.update(local);
                    Object returnment = ((Return_Sentence)ret).getProcessedValue();
                    if (returnment instanceof String)
                        return null;
                    
                    return returnment;
                } 
            }
            
            for (Parameter param : f.getParameters()) {
                local.deleteSymbol(param.getName());
            }
            env.update(local);
        }
        return null;
    }
}

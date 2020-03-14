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
import Symbols.Parameter;
import Symbols.Symbol;
import Symbols.SymbolsTable;
import Symbols.Vector;
import aritcompiler.Singleton;
import java.util.ArrayList;

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
        this.params = null;
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
        }
        
        Function f = Singleton.getFunction(this.name);
        if (f == null)
            Singleton.insertError(new CompileError("Semantico", "La funcion '" + this.name + "' no existe", this.line, this.column));
        else 
            retorno = executeFunction(f, env);
        
        return retorno;
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
    
    private boolean validateNoDefault() {
        for (Object obj : this.params) {
            if (String.valueOf(obj).equals("default"))
                return false;
        }
        return true;
    }
    
    private Symbol Concat(SymbolsTable env) {
        if (!validateNoDefault()) {
            Singleton.insertError(new CompileError("Semantico", "La funcion C no acepta 'default' como parametro", this.line, this.column));
            return null;
        }
        
        Concat concatenator = new Concat(this.params, this.line, this.column);
        Symbol sym = (Symbol)concatenator.process(env);
        return sym;
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
                    
                }
                else {
                    Expression exp = (Expression)param;
                    Object val = exp.process(env);
                    if (val instanceof CompileError) {
                        return val;
                    }
                    
                    if (val instanceof Atomic) {
                        
                    }
                    
                    if (val instanceof Vector) {
                        Vector nu = (Vector)((Vector)val).clone();
                    }
                    
                }
            }
        }
        return null;
    }
}

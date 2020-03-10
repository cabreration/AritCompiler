/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Instructions;

import APIServices.CompileError;
import Expressions.Expression;
import Symbols.SymbolsTable;
import aritcompiler.Singleton;
import java.util.ArrayList;

/**
 *
 * @author jacab
 */
public class Function_Call implements Instruction {
    private String name;
    private ArrayList<Object> params;
    private int line;
    private int column;

    public Function_Call(String name, ArrayList<Object> params) {
        this.name = name;
        this.params = params;
    }

    public Function_Call(String name) {
        this.name = name;
        this.params = new ArrayList<Object>();
    }

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
    }
    
    public String getName() {
        return name;
    }

    public ArrayList<Object> getParams() {
        return params;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    @Override
    public Object process(SymbolsTable env) {
        
        //Funciones Nativas
        switch (name.toLowerCase()) {
            case "print":
                break;
            case "c":
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
        return null;
    }
    
    public void Print(SymbolsTable env) {
        if (params.size() != 1)
            Singleton.insertError(new CompileError("Semantico", "La funcion print recibe una sola expresion como argumento", this.line, this.column));
        
        if (!(params.get(0) instanceof Expression))
            Singleton.insertError(new CompileError("Semantico", "La funcion print no puede tener default como parametro", this.line, this.column));
        
        Print printer = new Print((Expression)this.params.get(0));
        String result = (String)printer.process(env);
        Singleton.insertPrint(result);
    }
}

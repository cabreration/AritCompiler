/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Instructions;

import Symbols.SymbolsTable;
import java.util.ArrayList;

/**
 *
 * @author jacab
 */
public class Function_Call implements Instruction {
    private String name;
    private ArrayList<Object> params;

    public Function_Call(String name, ArrayList<Object> params) {
        this.name = name;
        this.params = params;
    }

    public Function_Call(String name) {
        this.name = name;
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
    
}

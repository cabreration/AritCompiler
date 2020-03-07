/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Symbols;

import Expressions.Value;
import java.util.Hashtable;

/**
 *
 * @author jacab
 */
public class Enviroment {
    private SymbolsTable symbols;
    private Hashtable<String, Function> functions;
    
    public Enviroment() {
        symbols = new SymbolsTable();
        functions = new Hashtable<String, Function>();
    }
    
    public Symbol getSymbol(String name) {
        return symbols.getSymbol(name);
    }
    
    public Value getValue(String name) {
        return symbols.getValue(name);
    }
    
    public void updateSymbol(String name, Symbol symbol) {
        symbols.updateSymbol(name, symbol);
    }
    
    public Function getFunction(String name) {
        boolean exists = functions.containsKey(name);
        if (!exists)
            return null;
        
        Function func = functions.get(name);
        return func;
    }
    
    public boolean insertFunction(Function function) {
        boolean exists = functions.containsKey(function.getName());
        
        if (exists) {
            // error semantico
            return false;
        }
        
        functions.put(function.getName(), function);
        return true;
    }
}

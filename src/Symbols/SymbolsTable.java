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
public class SymbolsTable {
    
    private Hashtable<String, Symbol> symbols;
    
    public SymbolsTable() {
        symbols = new Hashtable<String, Symbol>();
    }
    
    public void updateSymbol(String name, Symbol symbol) {
        
        boolean exists = symbols.containsKey(name);
        if (exists)
            symbols.remove(name);
        
        symbols.put(name, symbol);
    }
    
    public Symbol getSymbol(String name) {
        boolean exists = symbols.containsKey(name);
        if (!exists)
            return null;
        
        return symbols.get(name);
    }
    
    public Value getValue(String name) {
        boolean exists = symbols.containsKey(name);
        
        if (!exists)
            return null;
        
        return (Value)(symbols.get(name));
    }
}

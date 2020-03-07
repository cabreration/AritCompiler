/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Symbols;

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
    
    public void updateSymbol(Symbol symbol) {
        
        boolean exists = symbols.containsKey(symbol.getName());
        if (exists)
            symbols.remove(symbol.getName());
        
        symbols.put(symbol.getName(), symbol);
    }
    
    public Symbol getSymbol(String name) {
        boolean exists = symbols.containsKey(name);
        if (!exists)
            return null;
        
        return symbols.get(name);
    }
}

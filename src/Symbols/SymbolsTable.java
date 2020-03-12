/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Symbols;

import Expressions.Value;
import java.util.Hashtable;
import java.util.Set;

/**
 *
 * @author jacab
 */
public class SymbolsTable {
    
    private Hashtable<String, Symbol> symbols;
    private String type;
    
    public SymbolsTable(String type) {
        symbols = new Hashtable<String, Symbol>();
        this.type = type;
    }
    
    public SymbolsTable(String type, SymbolsTable vader) {
        this.symbols = new Hashtable<String, Symbol>();
        this.type = type;
        
        Set<String> keys = vader.getSymbols().keySet();
        for (String key : keys) {
            Symbol sym = vader.getSymbol(key);
            this.symbols.put(key, sym);
        }
    }

    public Hashtable<String, Symbol> getSymbols() {
        return symbols;
    }

    public String getType() {
        return type;
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

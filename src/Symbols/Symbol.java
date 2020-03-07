/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Symbols;

/**
 *
 * @author jacab
 */
public abstract class Symbol {
    
    private String name;
    
    public Symbol(String name) {
        this.name = name;
    }
    
    public final String getName() {
        return this.name;
    }
    
    public abstract Object getValue();
}

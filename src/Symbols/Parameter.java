/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Symbols;

import Expressions.Expression;

/**
 *
 * @author jacab
 */
public class Parameter {
    
    private int line;
    private int column;
    private String name;
    private Expression defaultValue;

    public Parameter(int line, int column, String name, Expression defaultValue) {
        this.line = line;
        this.column = column;
        this.name = name;
        this.defaultValue = defaultValue;
    }

    public Parameter(int line, int column, String name) {
        this.line = line;
        this.column = column;
        this.name = name;
        this.defaultValue = null;
    }
}

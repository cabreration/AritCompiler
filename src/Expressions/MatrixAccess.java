/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Expressions;

import Symbols.SymbolsTable;

/**
 *
 * @author jacab
 */
public class MatrixAccess implements Expression {
    private String identifier;
    private int line;
    private int column;
    private int type;
    private Expression left;
    private Expression right;
    private Expression[] vectors;

    public MatrixAccess(String identifier, int line, int column, int type, Expression dister) {
        this.identifier = identifier;
        this.line = line;
        this.column = column;
        this.type = type;
        if (this.type == 1) {
            this.left = dister;
            this.right = null;
        }
        else {
            this.right = dister;
            this.left = null;
        }
        vectors = null;
    }

    public MatrixAccess(String identifier, int line, int column, int type, Expression dister, Expression[] vectors) {
        this.identifier = identifier;
        this.line = line;
        this.column = column;
        this.type = type;
        if (this.type == 1) {
            this.left = dister;
            this.right = null;
        }
        else {
            this.right = dister;
            this.left = null;
        }
        this.vectors = vectors;
    }

    public MatrixAccess(String identifier, int line, int column, Expression left, Expression right) {
        this.identifier = identifier;
        this.line = line;
        this.column = column;
        this.left = left;
        this.right = right;
        this.type = 0;
        this.vectors = null;
    }

    public MatrixAccess(String identifier, int line, int column, Expression left, Expression right, Expression[] vectors) {
        this.identifier = identifier;
        this.line = line;
        this.column = column;
        this.left = left;
        this.right = right;
        this.vectors = vectors;
        this.type = 0;
    }

    @Override
    public Object process(SymbolsTable env) {
        return null;
    }
    
    
}

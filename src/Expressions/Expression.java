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
public interface Expression {
    public Object process(SymbolsTable env);
}

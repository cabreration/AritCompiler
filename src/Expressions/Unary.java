/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Expressions;

import APIServices.CompileError;
import Symbols.Enviroment;

/**
 *
 * @author jacab
 */
public class Unary implements Expression {

    public enum Operator { MINUS, NEGATION }
    private Expression exp;
    private Operator operator;
    private int line;
    private int column;
    
    public Unary(Operator operator, Expression exp) {
        this.operator = operator;
        this.exp = exp;
    }

    public Unary(Expression exp, Operator operator, int line, int column) {
        this.exp = exp;
        this.operator = operator;
        this.line = line;
        this.column = column;
    }
    
    @Override
    public Object process(Enviroment env) {
        Object atom = exp.process(env);
        
        if (atom instanceof CompileError)
            return atom;
        
        if (!(atom instanceof Value)) {
            // ESTO NO DEBERIA PASAR
            throw new Error("This should not be happening");
        }
        
        Object res;        
        if (this.operator == Operator.MINUS) {
            res = ((Value)atom).aritmeticNegation(env);
        } else {
            res = ((Value)atom).booleanNegation(env);
        }
        
        if (res instanceof CompileError) {
            ((CompileError)res).setRow(this.line);
            ((CompileError)res).setColumn(this.column);
        }
        
        return res;
    }
}

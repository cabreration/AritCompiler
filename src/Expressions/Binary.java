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
public class Binary implements Expression {
    
    public enum Operator {
        PLUS,
        MINUS,
        TIMES,
        DIV,
        MOD,
        POWER,
        ASIGNMENT,
        EQUALS,
        NOT_EQUALS,
        GREATER,
        GREATER_EQUALS,
        LESSER,
        LESSER_EQUALS,
        AND,
        OR
    }
    
    private Operator operator;
    private Expression exp1;
    private Expression exp2;
    private int line;
    private int column;

    public Binary(Operator operator, Expression exp1, Expression exp2, int line, int column) {
        this.operator = operator;
        this.exp1 = exp1;
        this.exp2 = exp2;
        this.line = line;
        this.column = column;
    }

    public Binary(Operator operator, Expression exp1, Expression exp2) {
        this.operator = operator;
        this.exp1 = exp1;
        this.exp2 = exp2;
    }
    
    @Override
    public Object process(Enviroment env) {
        Object atom1 = exp1.process(env);
        Object atom2 = exp2.process(env);
        
        if (atom1 instanceof CompileError)
            return atom1;
        
        if (atom2 instanceof CompileError)
            return atom2;
        
        if (!(atom1 instanceof Value) && !(atom2 instanceof Value)) {
            throw new Error("Esto no deberia estar pasando Binario");
        }
        
        Object res = operations(env, (Value)atom1, (Value)atom2);
        if (res instanceof CompileError) {
            if (((CompileError)res).getRow() == 0 && ((CompileError)res).getColumn() == 0) {
                ((CompileError)res).setRow(this.line);
                ((CompileError)res).setColumn(this.column);
            }
        }
        
        return res;
    }
    
    private Object operations(Enviroment env, Value op1, Value op2) {
        
        Object res = null;
        switch (this.operator) {
        
            case MINUS:
                res = op1.minus(env, op2, 1);
                break;
            
            case PLUS:
                res = op1.plus(env, op2, 1);
                break;
                
            case TIMES:
                res = op1.times(env, op2, 1);
                break;
            
            case DIV:
                res = op1.div(env, op2, 1);
                break;
                
            case MOD:
                res = op1.mod(env, op2, 1);
                break;
                
            case POWER:
                res = op1.power(env, op2, 1);
                break;
                
            case ASIGNMENT:
                break;
                
            case EQUALS:
                break;
                
            case NOT_EQUALS:
                break;
                
            case GREATER:
                break;
                
            case GREATER_EQUALS:
                break;
                
            case LESSER:
                break;
                
            case LESSER_EQUALS:
                break;
                
            case AND:
                break;
                
            case OR:
                break;
        }
        
        if (res instanceof CompileError) {
            if (((CompileError)res).getRow() == 0 || ((CompileError)res).getColumn() == 0) {
                ((CompileError)res).setRow(this.line);
                ((CompileError)res).setColumn(this.line);
            }
        }
        return res;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Expressions;

import APIServices.CompileError;
import Symbols.Symbol;
import Symbols.SymbolsTable;
import Symbols.Vector;
import java.util.ArrayList;

/**
 *
 * @author jacab
 */
public class Binary implements Expression {

    public Binary() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
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
    public Object process(SymbolsTable env) {
        Object atom1 = exp1.process(env);
        Object atom2 = exp2.process(env);
        
        if (atom1 == null || atom2 == null)
            return new CompileError("Semantico", "No es posible realizar operaciones el valor nulo", this.line, this.column);
        
        if (atom1 instanceof CompileError) {
            if (((CompileError)atom1).getRow() == 0 && ((CompileError)atom1).getColumn() == 0) {
                ((CompileError)atom1).setRow(this.line);
                ((CompileError)atom1).setColumn(this.column);
            }
            return atom1;
        }
        
        if (atom2 instanceof CompileError) {
            if (((CompileError)atom2).getRow() == 0 && ((CompileError)atom2).getColumn() == 0) {
                ((CompileError)atom2).setRow(this.line);
                ((CompileError)atom2).setColumn(this.column);
            }
            return atom2;
        }
        
        if (!(atom1 instanceof Value) || !(atom2 instanceof Value)) {
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
    
    private Object operations(SymbolsTable env, Value op1, Value op2) {
        Value aux = op1;
        Value aux2 = op2;
        if (op1 instanceof Vector) {
            if (((Vector)op1).getSize() == 1) {
                op1 = ((ArrayList<Atomic>)((Vector)op1).getValue()).get(0);
            }
        }
        if (op2 instanceof Vector) {
            if (((Vector)op2).getSize() == 1) {
                op2 = ((ArrayList<Atomic>)((Vector)op2).getValue()).get(0);
            }
        }
        
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
                res = asign(op1, op2, env);
                break;
                
            case EQUALS:
                res = op1.equals(env, op2, 1);
                break;
                
            case NOT_EQUALS:
                res = op1.notEquals(env, op2, 1);
                break;
                
            case GREATER:
                res = op1.greater(env, op2, 1);
                break;
                
            case GREATER_EQUALS:
                res = op1.greaterEquals(env, op2, 1);
                break;
                
            case LESSER:
                res = op1.lesser(env, op2, 1);
                break;
                
            case LESSER_EQUALS:
                res = op1.lesserEquals(env, op2, 1);
                break;
                
            case AND:
                res = op1.and(env, op2);
                break;
                
            case OR:
                res = op1.or(env, op2);
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
    
    private Object asign(Value op1, Value op2, SymbolsTable env) {
        // first one needs to be an id
        if (!(op1 instanceof Atomic))
            return new CompileError("Semantico", "No es posible asignar un valor otro valor", this.line, this.column);
        
        if (((Atomic)op1).getType() != Atomic.Type.IDENTIFIER)
            return new CompileError("Semantico", "No es posible asignar un valor a otro valor", this.line, this.column);
        
        String id = String.valueOf(((Atomic)op1).getValue());
        
        Symbol sym;
        if (op2 instanceof Atomic) {
            if (((Atomic)op2).getType() == Atomic.Type.IDENTIFIER) {
                String name = String.valueOf(((Atomic)op2).getValue());
                int line = ((Atomic)op2).getLine();
                int col = ((Atomic)op2).getColumn();
                
                sym = env.getSymbol(name, line);
                if (sym == null)
                    return new CompileError("Semantico", "La variable '" + name + "' no existe en el contexto actual", line, col);
            }
            else {
                sym = new Vector(((Atomic)op2).clonation());
                env.updateSymbol(id, sym);
                return op2;
            }
        }
        else {
            sym = (Symbol)op2;
        }
        env.updateSymbol(id, sym);
        return sym;
    }
}

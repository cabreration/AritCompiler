/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Instructions;

import APIServices.CompileError;
import Expressions.Atomic;
import Expressions.Expression;
import Expressions.Value;
import Symbols.Symbol;
import Symbols.SymbolRef;
import Symbols.SymbolsTable;
import Symbols.Vector;
import aritcompiler.Singleton;

/**
 *
 * @author jacab
 */
public class Asignment implements Instruction {
    
    private String identifier;
    private Expression expression;
    private int line;
    private int column;

    public Asignment(String identifier, Expression expression, int line, int column) {
        this.identifier = identifier;
        this.expression = expression;
        this.line = line;
        this.column = column;
    }

    public String getIdentifier() {
        return identifier;
    }

    public Expression getExpression() {
        return expression;
    }

    @Override
    public Object process(SymbolsTable env) {
        Object exp = expression.process(env);
        
        if (exp == null)
            return null;
        
        if (exp instanceof CompileError) {
            if (((CompileError)exp).getRow() == 0 && ((CompileError)exp).getColumn() == 0) {
                ((CompileError)exp).setRow(this.line);
                ((CompileError)exp).setColumn(this.column);
            }
            Singleton.insertError((CompileError)exp);
            return null;
        }
            
        if (exp instanceof Atomic) {
            if (((Atomic)exp).getType() == Atomic.Type.IDENTIFIER) {
                String id = (String.valueOf((((Atomic)exp).getValue())));
                int line = ((Atomic)exp).getLine();
                int col = ((Atomic)exp).getColumn();
                exp = env.getSymbol(String.valueOf((((Atomic)exp).getValue())), this.line);
                
                if (exp == null) {
                    CompileError error = new CompileError("Semantico", "La variable " + id + " no existe en el contexto actual", line, col);
                    Singleton.insertError(error);
                }
                
                env.updateSymbol(this.identifier, (Symbol)exp);
                return null;
            }
            
            Vector vector = new Vector((Atomic)exp);
            String type = String.valueOf(((Value)vector).typeof(env).getValue());
            SymbolRef ref = new SymbolRef(this.identifier, type, this.line, this.column);
            Singleton.insertRef(ref);
            env.updateSymbol(this.identifier, vector);
            return null;
        }   
        
        // if it is a vector, array, matrix or list
        String type = String.valueOf(((Value)exp).typeof(env).getValue());
        SymbolRef ref = new SymbolRef(this.identifier, type, this.line, this.column);
        Singleton.insertRef(ref);
        env.updateSymbol(this.identifier, (Symbol)exp);
        return null;
    }
    
}

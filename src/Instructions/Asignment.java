/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Instructions;

import APIServices.CompileError;
import Expressions.Atomic;
import Expressions.Expression;
import Symbols.Symbol;
import Symbols.SymbolsTable;
import Symbols.Vector;

/**
 *
 * @author jacab
 */
public class Asignment implements Instruction {
    
    private String identifier;
    private Expression expression;

    public Asignment(String identifier, Expression expression) {
        this.identifier = identifier;
        this.expression = expression;
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
        
        if (exp instanceof CompileError) {
            // agregar el error a la lista de errores
            return null;
        }
            
        if (exp instanceof Atomic) {
            if (((Atomic)exp).getType() == Atomic.Type.IDENTIFIER) {
                String id = (String.valueOf((((Atomic)exp).getValue())));
                exp = env.getSymbol(String.valueOf((((Atomic)exp).getValue())));
                
                if (exp == null) {
                    CompileError error = new CompileError("Semantico", "La variable " + id + " no existe", 0, 0);
                    // guardar el error en la lista de errores
                }
                
                env.updateSymbol(this.identifier, (Symbol)exp);
            }
            
            Vector vector = new Vector(((Atomic)exp).getValue());
            env.updateSymbol(this.identifier, vector);
        }
            
        return null;
    }
    
}

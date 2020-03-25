/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Instructions;

import APIServices.CompileError;
import Expressions.Atomic;
import Expressions.Expression;
import Symbols.SymbolsTable;
import aritcompiler.Singleton;

/**
 *
 * @author jacab
 */
public class Return_Sentence implements Instruction {
    private Expression value;
    private Object processedValue;
    private int line;
    private int column;

    public Return_Sentence(Expression value, int line, int column) {
        this.value = value;
        this.line = line;
        this.column = column;
    }

    public Return_Sentence(int line, int column) {
        this.value = null;
        this.line = line;
        this.column = column;
    }

    @Override
    public Object process(SymbolsTable env) {
        if (!env.getType().contains("function")) {
            Singleton.insertError( new CompileError("Semantico", "La sentencia return no tiene sentido dentro del contexto actual", this.line, this.column));
            return null;
        }
        
        if (this.value == null) {
            this.processedValue = "void";
            return this;
        }
        
        this.processedValue = value.process(env);
        if (this.processedValue instanceof CompileError) {
            Singleton.insertError((CompileError)this.processedValue);
            return null;
        }
        
        if (this.processedValue instanceof Atomic) {
            if (((Atomic)this.processedValue).getType() == Atomic.Type.IDENTIFIER) {
                String id = String.valueOf(((Atomic)this.processedValue).getValue());
                this.processedValue = env.getSymbol(id, this.line);
                
                if (this.processedValue == null) {
                    Singleton.insertError(new CompileError("Semantico", "La variable '" +  id + "' no existe en el contexto actual", this.line, this.column));
                    return null;
                }
            }
        }
        return this;
    }
    
    public Object getProcessedValue() {
        return this.processedValue;
    }
}

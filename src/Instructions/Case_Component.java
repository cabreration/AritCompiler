/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Instructions;

import APIServices.CompileError;
import Expressions.Atomic;
import Expressions.Expression;
import Symbols.List;
import Symbols.Matrix;
import Symbols.SymbolsTable;
import Symbols.Vector;
import aritcompiler.Singleton;
import java.util.ArrayList;

/**
 *
 * @author jacab
 */
public class Case_Component implements Instruction {
    private Expression kase;
    private ArrayList<Instruction> sentences;
    private int line;
    private int column;
    private Object original;

    public Case_Component(Expression kase, ArrayList<Instruction> sentences, int line, int column) {
        this.kase = kase;
        this.sentences = sentences;
        this.line = line;
        this.column = column;
        this.original = null;
    }

    public Case_Component(ArrayList<Instruction> sentences, int line, int column) { /* THIS IS THE DEFAULT */
        this.sentences = sentences;
        this.line = line;
        this.column = column;
        this.original = null;
    }

    public void setOriginal(Object original) {
        this.original = original;
    }
    
    @Override
    public Object process(SymbolsTable env) {
        if (this.kase == null) {
            for (Instruction ins : this.sentences) {
                Object r = ins.process(env);
                
                if (r != null && (r instanceof Continue_Sentence || r instanceof Return_Sentence || r instanceof Break_Sentence)) {
                    return r;     
                }  
                else if (r != null && r instanceof CompileError) {
                    Singleton.insertError((CompileError)r);
                }
            }
            return null;
        }
        
        Object val = kase.process(env);
        
        if (val instanceof CompileError) {
            if (((CompileError)val).getRow() == 0 && ((CompileError)val).getColumn() == 0) {
                ((CompileError)val).setRow(this.line);
                ((CompileError)val).setColumn(this.column);
            }
            
            return val;
        }
        
        if (val instanceof Atomic) {
            if (((Atomic)val).getType() == Atomic.Type.IDENTIFIER) {
                String id = String.valueOf(((Atomic)val).getValue());
                int line = ((Atomic)val).getLine();
                int col = ((Atomic)val).getColumn();
                
                val = env.getSymbol(id, line);
                if (val == null)
                    return new CompileError("Semantico", "La variable '" + id + "' no existe en el contexto actual", line, col);
            }
        }
        
        if (val instanceof Matrix)
            val = ((Atomic[][])((Matrix)val).getValue())[0][0];
        
        while (val instanceof List) {
            val = ((ArrayList)((List)val).getValue()).get(0);
        }
        
        if (val instanceof Vector) {
            // Retornamos el primer valor
            val = ((ArrayList<Atomic>)(((Vector)val).getValue())).get(0);
        }
        /* MATRIX, ARRAY */
        
        Atomic one = (Atomic)original;
        Atomic two = (Atomic)val;
        
        boolean flag = false;
        if (one.getType() == Atomic.Type.STRING && two.getType() == Atomic.Type.STRING) {
            if (one.getValue() == null || two.getValue() == null)
                return new CompileError("Semantico", "No es posible operar valores nulos", this.line, this.column);
            
            String f = String.valueOf(one.getValue());
            String s = String.valueOf(two.getValue());
            flag = f.equals(s);
        }
        else if (one.getType() == Atomic.Type.BOOLEAN && two.getType() == Atomic.Type.BOOLEAN) {
            boolean f = ((Boolean)one.getValue()).booleanValue();
            boolean s = ((Boolean)two.getValue()).booleanValue();
            
            flag = f == s;
        }
        else if ((one.getType() == Atomic.Type.NUMERIC || one.getType() == Atomic.Type.INTEGER)
                && (two.getType() == Atomic.Type.NUMERIC || two.getType() == Atomic.Type.INTEGER)) {
            String af = String.valueOf(one.getValue());
            String as = String.valueOf(two.getValue());
            double f = Double.parseDouble(af);
            double s = Double.parseDouble(as);
            
            flag = f == s;
        }
        else {
            return new CompileError("Semantico", "El valor no concuerda con el tipo dado para el switch", this.line, this.column);
        }
        
        if (flag) {
            for (Instruction ins : this.sentences) {
                Object r = ins.process(env);
                
                if (r != null && (r instanceof Continue_Sentence || r instanceof Return_Sentence) || r instanceof Break_Sentence) {
                    return r;     
                }
            }
        }
        return null;
    }
    
    
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Instructions;

import APIServices.CompileError;
import Expressions.Atomic;
import Expressions.Expression;
import Expressions.StructureAccess;
import Symbols.SymbolsTable;
import Symbols.Vector;
import aritcompiler.Singleton;
import java.util.ArrayList;

/**
 *
 * @author jacab
 */
public class Print implements Instruction {

    private Expression arg;
    
    public Print(Expression arg) {
        this.arg = arg;
    }
    
    @Override
    public Object process(SymbolsTable env) {
        Object val = arg.process(env);
        
        if (val instanceof CompileError) {
            Singleton.insertError((CompileError)val);
            return "";
        }
        
        if (val instanceof Atomic) {
            if (((Atomic)val).getType() == Atomic.Type.IDENTIFIER) {
                String id = String.valueOf(((Atomic)val).getValue());
                int line = ((Atomic)val).getLine();
                int column = ((Atomic)val).getColumn();
                
                val = env.getValue(id);
                if (val == null) {
                    CompileError error = new CompileError("Semantico", "La variable '" + id + "' no existe en el contexto actual", line, column);
                    Singleton.insertError(error);
                    return "";
                }
            }
        }
        
        if (val instanceof Atomic) {
            String ret = String.valueOf(((Atomic)val).getValue());
            //if (((Atomic)val).getType() == Atomic.Type.STRING && !ret.equals("\n"))
                //ret = "\"" + String.valueOf(((Atomic)val).getValue()) + "\"";
           
            return ret + "\n";
        }
        else if (val instanceof Vector)  {
            if (((Vector)val).getSize() == 1) {
                Atomic a = ((ArrayList<Atomic>)((Vector)val).getValue()).get(0);
                return String.valueOf(a.getValue()) + "\n";
            }
            StringBuilder builder = new StringBuilder();
            builder.append("[ ");
            for (Atomic obj : (ArrayList<Atomic>)((Vector)val).getValue()) {
                String ap = "";
                if (obj.getType() == Atomic.Type.STRING)
                    ap = String.valueOf(obj.getValue());
                else 
                    ap = String.valueOf(obj.getValue());
                builder.append(ap);
                builder.append(", ");
            }
            builder.deleteCharAt(builder.length() - 2);
            builder.append("]");
            return builder.toString() + "\n";
        }
        //else if (val instanceof Matrix) {}
        //else if (val instanceof List) {}
        // else {} // instance of array
        return "";
    }
    
}

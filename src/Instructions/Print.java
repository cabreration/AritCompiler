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
                
                val = env.getValue(id, line);
                if (val == null) {
                    CompileError error = new CompileError("Semantico", "La variable '" + id + "' no existe en el contexto actual", line, column);
                    Singleton.insertError(error);
                    return "";
                }
            }
        }
        
        if (val instanceof Atomic) {
            String ret = "[ " + String.valueOf(((Atomic)val).getValue()) + " ]";
            //if (((Atomic)val).getType() == Atomic.Type.STRING && !ret.equals("\n"))
                //ret = "\"" + String.valueOf(((Atomic)val).getValue()) + "\"";
           
            return ret;// + "\n";
        }
        else if (val instanceof Vector)  {
            return printVector((Vector)val, "[", "]");
        }
        else if (val instanceof List) {
            return printList((List)val);
        }
        else if (val instanceof Matrix) {
            return printMatrix((Matrix)val);
        }
        // else {} // instance of array
        return "";
    }
    
    private String printVector(Vector val, String limit, String limit2) {
        /*if (val.getSize() == 1) {
                Atomic a = ((ArrayList<Atomic>)val.getValue()).get(0);
                return String.valueOf(a.getValue()) + "\n";
        }*/
        StringBuilder builder = new StringBuilder();
        builder.append(limit + " ");
        for (Atomic obj : (ArrayList<Atomic>)val.getValue()) {
            String ap = String.valueOf(obj.getValue());
            //if (obj.getType() == Atomic.Type.STRING)
                //builder.append("\"" + ap + "\"");
            //else
                builder.append(ap);
            builder.append(", ");
        }
        builder.deleteCharAt(builder.length() - 2);
        builder.append(limit2);
        return builder.toString();
    }
    
    private String printList(List val) {
        StringBuilder builder = new StringBuilder();
        builder.append("[ ");
        for (Object obj : (ArrayList)val.getValue()) {
            if (obj instanceof List) {
                String li = printList((List)obj);
                builder.append(li);
            }
            else {
                String vec = printVector((Vector)obj, "(", ")");
                builder.append(vec);
            }
            builder.append(", ");
        }
        builder.deleteCharAt(builder.length() - 2);
        builder.append("]");
        return builder.toString();
    }

    private Object printMatrix(Matrix matrix) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < matrix.getRows(); i++) {
            builder.append("| ");
            for (int j = 0; j < matrix.getColumns(); j++) {
                int maxLength = 0;
                for (int k = 0; k < matrix.getRows(); k++) {
                    String aux = String.valueOf(((Atomic[][])matrix.getValue())[k][j].getValue());
                    if (aux.length() > maxLength)
                        maxLength = aux.length();
                }
                Atomic atom = ((Atomic[][])matrix.getValue())[i][j];
                String cur = String.valueOf(atom.getValue());
                //if (atom.getType() == Atomic.Type.STRING) {
                   // cur = "\"" + cur + "\"";
                    //maxLength += 2;
                //}
                builder.append(cur);
                int spaces = (maxLength - cur.length()) + 1;
                for (int k = 0; k < spaces; k++) 
                    builder.append(" ");
            }
            builder.append("|\n");
        }
        return builder.toString();
    }
    
}

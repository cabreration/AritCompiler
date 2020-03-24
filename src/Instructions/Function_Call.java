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
import Symbols.Function;
import Symbols.List;
import Symbols.Parameter;
import Symbols.Symbol;
import Symbols.SymbolsTable;
import Symbols.Vector;
import aritcompiler.Singleton;
import java.util.ArrayList;
import Symbols.Matrix;

/**
 *
 * @author jacab
 */
public class Function_Call implements Instruction, Expression {
    private String name;
    private ArrayList<Object> params;
    private int line;
    private int column;

    public Function_Call(String name, ArrayList<Object> params, int line, int column) {
        this.name = name;
        this.params = params;
        this.line = line;
        this.column = column;
    }

    public Function_Call(String name, int line, int column) {
        this.name = name;
        this.line = line;
        this.column = column;
        this.params = new ArrayList<Object>();
    }
    
    public String getName() {
        return name;
    }

    public ArrayList<Object> getParams() {
        return params;
    }

    @Override
    public Object process(SymbolsTable env) {
        Object retorno = null;
        //Funciones Nativas
        switch (name.toLowerCase()) {
            case "print":
                Print(env);
                return null;
            case "c":
                retorno = Concat(env);
                break;
            case "list":
                retorno = List(env);
                break;
            case "matrix":
                retorno = Matrix(env);
                break;
            case "mean":
                retorno = Stat(env, 1);
                break;
            case "median":
                retorno = Stat(env, 2);
                break;
            case "mode":
                retorno = Stat(env, 3);
                break;
            case "typeof":
                retorno = nativeOne(env, 1);
                break;
            case "length":
                retorno = nativeOne(env, 2);
                break;
            case "ncol":
                retorno = nativeOne(env, 3);
                break;
            case "nrow":
                retorno = nativeOne(env, 4);
                break;
            case "stringlength":
                retorno = stringFunctions(env, 1);
                break;
            case "remove":
                retorno = stringFunctions(env, 2);
                break;
            case "tolowercase":
                retorno = stringFunctions(env, 3);
                break;
            case "touppercase":
                retorno = stringFunctions(env, 4);
                break;
            case "trunk":
                retorno = numberFunctions(env, 1);
                break;
            case "round":
                retorno = numberFunctions(env, 2);
                break;
            case "barplot":
                Barplot(env);
                return null;
            case "pie":
                Pie(env);
                return null;
            case "plot":
                Plot(env);
                return null;
            case "hist":
                Histogram(env);
                return null;
        }
        
        Function f = Singleton.getFunction(this.name);
        if (f == null)
            Singleton.insertError(new CompileError("Semantico", "La funcion '" + this.name + "' no existe", this.line, this.column));
        else 
            retorno = executeFunction(f, env);
        
        return retorno;
    }
    
    private boolean validateNoDefault() {
        for (Object obj : this.params) {
            if (String.valueOf(obj).equals("default"))
                return false;
        }
        return true;
    }
    
    private Object Print(SymbolsTable env) {
        if (params.size() != 1)
            Singleton.insertError(new CompileError("Semantico", "La funcion print recibe una sola expresion como argumento", this.line, this.column));
        
        if (!(params.get(0) instanceof Expression))
            Singleton.insertError(new CompileError("Semantico", "La funcion print no puede tener default como parametro", this.line, this.column));
        
        Print printer = new Print((Expression)this.params.get(0));
        String result = (String)printer.process(env);
        Singleton.insertPrint(result);
        return null;
    }
    
    private Symbol Concat(SymbolsTable env) {
        if (!validateNoDefault()) {
            Singleton.insertError(new CompileError("Semantico", "La funcion C no acepta 'default' como parametro", this.line, this.column));
            return null;
        }
        
        if (params.size() == 0) {
            Singleton.insertError(new CompileError("Semantico","No puede realizar un funcion de concatenacion sin argumentos", this.line, this.column));
            return null;
        }
        
        Concat concatenator = new Concat(this.params, this.line, this.column);
        Symbol sym = (Symbol)concatenator.process(env);
        return sym;
    }
    
    private Symbol List(SymbolsTable env) {
        if (!validateNoDefault()) {
            Singleton.insertError(new CompileError("Semantico", "La funcion C no acepta 'default' como parametro", this.line, this.column));
            return null;
        }
        if (params.size() == 0) {
            Singleton.insertError(new CompileError("Semantico","No puede realizar un funcion de concatenacion sin argumentos", this.line, this.column));
            return null;
        }
        
        List_Function listator = new List_Function(this.line, this.column, this.params);
        List list = (List)listator.process(env);
        return list;
    }
    
    private Matrix Matrix(SymbolsTable env) {
        if (!validateNoDefault()) {
            Singleton.insertError(new CompileError("Semantico", "La funcion Matrix no acepta 'default' como parametro", this.line, this.column));
            return null;
        }
        if (params.size() != 3) {
            Singleton.insertError(new CompileError("Semantico","No puede realizar un funcion de matrix con ese numero de argumentos", this.line, this.column));
            return null;
        }
        
        Matrix_Function matrixator = new Matrix_Function(this.line, this.column,
                (Expression)this.params.get(0), (Expression)this.params.get(1), (Expression)this.params.get(2));
        Matrix matrix = (Matrix)matrixator.process(env);
        return matrix;
    }
    
    private Object Stat(SymbolsTable env, int type) { 
        if (!validateNoDefault()) {
            Singleton.insertError(new CompileError("Semantico", "Las funciones estadisticas no acepta 'default' como parametro", this.line, this.column));
            return null;
        }
        
        if (params.size() > 2 || params.size() < 1) {
            Singleton.insertError(new CompileError("Semantico", "La cantidad de parametros es incorrecta para las funciones estadisticas", this.line, this.column));
            return null;
        }
        
        Expression vector = (Expression)params.get(0);
        Expression trim = null;
        if (params.size() == 2)
            trim = (Expression)params.get(1);
        
        if (type == 1) {
            if (trim == null) {
                Mean mean = new Mean(vector, this.line, this.column);
                return mean.process(env);
            }
            else {
                Mean mean = new Mean(vector, trim, this.line, this.column);
                return mean.process(env);
            }
        }
        else if (type == 2) {
            if (trim == null) {
                Median median = new Median(vector, this.line, this.column);
                return median.process(env);
            }
            else {
                Median median = new Median(vector, trim, this.line, this.column);
                return median.process(env);
            }
        }
        else {
            if (trim == null) {
                Mode mode = new Mode(vector, this.line, this.column);
                return mode.process(env);
            }
            else {
                Mode mode = new Mode(vector, trim, this.line, this.column);
                return mode.process(env);
            }
        }
    }
    
    private Object nativeOne(SymbolsTable env, int type) {
        if (!validateNoDefault()) {
            Singleton.insertError(new CompileError("Semantico", "Las funciones nativas no acepta 'default' como parametro", this.line, this.column));
            return null;
        }
        
        if (params.size() != 1) {
            Singleton.insertError(new CompileError("Semantico", "La cantidad de parametros es incorrecta para las funciones nativas", this.line, this.column));
            return null;
        }
        
        Expression exp = (Expression)this.params.get(0);
        Object sym = exp.process(env);
        
        Object ret;
        if (type == 1)
            ret = ((Value)sym).typeof(env);
        else if (type == 2)
            ret = ((Value)sym).length(env);
        else if (type == 3)
            ret = ((Value)sym).nRow(env);
        else
            ret = ((Value)sym).nCol(env);
        
        return ret;
    }
    
    private Object stringFunctions(SymbolsTable env, int type) {
        if (!validateNoDefault()) {
            Singleton.insertError(new CompileError("Semantico", "Las funciones de cadenas no acepta 'default' como parametro", this.line, this.column));
            return null;
        }
        
        if (type == 2) {
            if (params.size() != 2) {
                Singleton.insertError(new CompileError("Semantico", "La cantidad de parametros es incorrecta para las funciones sobre cadenas", this.line, this.column));
                return null;
            }
            
            Expression str = (Expression)this.params.get(0);
            Expression regex = (Expression)this.params.get(1);
            StringFunction strF = new StringFunction(str, regex, this.line, this.column);
            return strF.process(env);
        }
        else {
            if (params.size() != 1) {
                Singleton.insertError(new CompileError("Semantico", "La cantidad de parametros es incorrecta para las funciones numericas", this.line, this.column));
                return null;
            }
            
            Expression str = (Expression)this.params.get(0);
            StringFunction strF = new StringFunction(type, str, this.line, this.column);
            return strF.process(env);
        }
    }
    
    private Object numberFunctions(SymbolsTable env, int type) {
        if (!validateNoDefault()) {
            Singleton.insertError(new CompileError("Semantico", "Las funciones numericas no acepta 'default' como parametro", this.line, this.column));
            return null;
        }
        if (params.size() != 1) {
            Singleton.insertError(new CompileError("Semantico", "La cantidad de parametros es incorrecta para las funciones numericas", this.line, this.column));
            return null;
        }
        
        Expression number = (Expression)this.params.get(0);
        NumberFunction numberF = new NumberFunction(number, type, this.line, this.column);
        return numberF.process(env);
    }

    private Object executeFunction(Function f, SymbolsTable env) {
        if (f.getParameters() == null) {
            // Una funcion sin parametros
            if (this.params != null) {
                return new CompileError("Semantico", "La funcion '" + this.name + "' no acepta parametros", this.line, this.column);
            }
            
            Object ret = null;
            SymbolsTable local = new SymbolsTable("function", env);
            for (Instruction ins : f.getSentences()) {
                ret = ins.process(local);
                if (ret != null && ret instanceof Return_Sentence) {
                    env.update(local);
                    return ((Return_Sentence)ret).getProcessedValue();
                } 
            }
        }
        else {
            // Una funcion con parametros
            if (f.getParameters().size() != this.params.size()) {
                return new CompileError("Semantico", "La funcion '" + f.getName() + "' acepta unicamente " + f.getParameters().size() + " parametros", this.line, this.column);
            }
            
            Object ret = null;
            SymbolsTable local = new SymbolsTable("function", env);
            for (Parameter param : f.getParameters()) {
                local.deleteSymbol(param.getName());
            }
            
            for (int i = 0; i < this.params.size(); i++) {
                Object param = this.params.get(i);
                if (param instanceof String) {
                    // Default value
                    Parameter p = f.getParameters().get(i);
                    if (p.getDefaultValue() == null)
                        return new CompileError("Semantico", "El parametro no tiene un valor por defecto definido", this.line, this.column);
                    
                    Object value = p.getDefaultValue().process(env);
                    if (value instanceof CompileError) {
                        if (((CompileError)value).getRow() == 0 && ((CompileError)value).getColumn() == 0) {
                            ((CompileError)value).setRow(this.line);
                            ((CompileError)value).setColumn(this.column);
                        }
                        return value;
                    }
                    
                    if (value instanceof Atomic) {
                        if (((Atomic)value).getType() == Atomic.Type.IDENTIFIER) {
                            String id = String.valueOf(((Atomic)value).getValue());
                            int line = ((Atomic)value).getLine();
                            int column = ((Atomic)value).getColumn();
                            
                            value = env.getSymbol(id);
                            if (value == null)
                                return new CompileError("Semantico", "La variable '" + id + "' no existe en el contexto actual", line, column);
                        }
                    }
                    
                    if (value instanceof Matrix) {
                        Matrix nu = ((Matrix)value).clonation();
                        local.updateSymbol(p.getName(), nu);
                    } 
                    else if (value instanceof List) {
                        List nu = ((List)value).clonation();
                        local.updateSymbol(p.getName(), nu);
                    }
                    else if (value instanceof Vector) {
                        Vector nu = ((Vector)value).clonation();
                        local.updateSymbol(p.getName(), nu);
                    }
                    else if (value instanceof Atomic) {
                        Atomic at = ((Atomic)value).clonation();
                        Vector nu = new Vector(at);
                        local.updateSymbol(p.getName(), nu);
                    }
                    /* ARRAY */
                }
                else {
                    Parameter p = f.getParameters().get(i);
                    Expression exp = (Expression)param;
                    Object val = exp.process(env);
                    if (val instanceof CompileError) {
                        return val;
                    }
                    
                    if (val instanceof Atomic) {
                        if (((Atomic)val).getType() == Atomic.Type.IDENTIFIER) {
                            String id = String.valueOf(((Atomic)val).getValue());
                            int line = ((Atomic)val).getLine();
                            int column = ((Atomic)val).getColumn();
                            
                            val = env.getSymbol(id);
                            if (val == null)
                                return new CompileError("Semantico", "La variable '" + id + "' no existe en el contexto actual", line, column);
                        }
                    }
                    
                    if (val instanceof Matrix) {
                        Matrix nu = ((Matrix)val).clonation();
                        local.updateSymbol(p.getName(), nu);
                    } 
                    else if (val instanceof List) {
                        List nu = ((List)val).clonation();
                        local.updateSymbol(p.getName(), nu);
                    }
                    else if (val instanceof Vector) {
                        Vector nu = ((Vector)val).clonation();
                        local.updateSymbol(p.getName(), nu);
                    }
                    else if (val instanceof Atomic) {
                        Atomic at = ((Atomic)val).clonation();
                        Vector nu = new Vector(at);
                        local.updateSymbol(p.getName(), nu);
                    }
                    /* ARRAY */
                }
            }
            
            for (Instruction ins : f.getSentences()) {
                ret = ins.process(local);
                if (ret != null && ret instanceof Return_Sentence) {
                    for (Parameter param : f.getParameters())
                        local.deleteSymbol(param.getName());
                    
                    env.update(local);
                    Object returnment = ((Return_Sentence)ret).getProcessedValue();
                    if (returnment instanceof String)
                        return null;
                    
                    return returnment;
                } 
            }
            
            for (Parameter param : f.getParameters()) {
                local.deleteSymbol(param.getName());
            }
            env.update(local);
        }
        return null;
    }
    
    private void Pie(SymbolsTable env) {
        if (!validateNoDefault()) {
            Singleton.insertError(new CompileError("Semantico", "La funcion pie no acepta valores por defecto", this.line, this.column));
            return;
        }
        
        if (this.params.size() != 3) {
            Singleton.insertError(new CompileError("Semantico", "Cantidad incorrecta de parametros para la funcion pie", this.line, this.column));
            return;
        }
        
        Expression vector = (Expression)this.params.get(0);
        Expression labels = (Expression)this.params.get(1);
        Expression title = (Expression)this.params.get(2);
        Pie pie = new Pie(vector, labels, title, this.line, this.column);
        pie.process(env);
    }

    private void Barplot(SymbolsTable env) {
        if (!validateNoDefault()) {
            Singleton.insertError(new CompileError("Semantico", "La funcion barplot no acepta valores por defecto", this.line, this.column));
            return;
        }
        
        if (this.params.size() != 5) {
            Singleton.insertError(new CompileError("Semantico", "Cantidad incorrecta de parametros para la funcion barplot", this.line, this.column));
            return;
        }
        
        Expression h = (Expression)this.params.get(0);
        Expression x = (Expression)this.params.get(1);
        Expression y = (Expression)this.params.get(2);
        Expression main = (Expression)this.params.get(3);
        Expression names = (Expression)this.params.get(4);
        Barplot barplot = new Barplot(h, x, y, main, names, this.line, this.column);
        barplot.process(env);
    }
    
    private void Histogram(SymbolsTable env) {
        if (!validateNoDefault()) {
            Singleton.insertError(new CompileError("Semantico", "La funcion hist no acepta valores por defecto", this.line, this.column));
            return;
        }
        
        if (this.params.size() != 3) {
            Singleton.insertError(new CompileError("Semantico", "Cantidad incorrecta de parametros para la funcion hist", this.line, this.column));
            return;
        }
        
        Expression h = (Expression)this.params.get(0);
        Expression x = (Expression)this.params.get(1);
        Expression y = (Expression)this.params.get(2);
        Histogram hist = new Histogram(h, x, y, this.line, this.column);
        hist.process(env);
    }
    
    private void Plot(SymbolsTable env) {
        if (!validateNoDefault()) {
            Singleton.insertError(new CompileError("Semantico", "La funcion plot no acepta valores por defecto", this.line, this.column));
            return;
        }
        
        if (this.params.size() != 5) {
            Singleton.insertError(new CompileError("Semantico", "Cantidad incorrecta de parametros para la funcion plot", this.line, this.column));
            return;
        }
        
        Expression mat = (Expression)this.params.get(0);
        Expression x = (Expression)this.params.get(1);
        Expression y = (Expression)this.params.get(2);
        Expression main = (Expression)this.params.get(3);
        Expression lim = (Expression)this.params.get(4);
        Dispersion dispersion = new Dispersion(mat, x, y, main, lim, this.line, this.column);
        dispersion.process(env);
    }
}

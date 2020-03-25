/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Symbols;

import APIServices.CompileError;
import Expressions.Atomic;
import Expressions.Value;
import aritcompiler.Singleton;
import java.util.ArrayList;

/**
 *
 * @author jacab
 */
public class Matrix implements Symbol, Value{

    private Atomic[][] elements;
    private int nRows;
    private int nCols;
    private int type;
    
    public Matrix(int nRows, int nCols, Vector vector, int type) {
        this.nRows = nRows;
        this.nCols = nCols;
        this.type = type;
        
        this.elements = new Atomic[this.nRows][this.nCols];
        // now we fill it up
        int size = this.nRows*this.nCols;
        int line = 0;
        int col = 0;
        int pos = 0;
        for (int i = 0; i < size; i++) {
            Atomic atom = ((ArrayList<Atomic>)vector.getValue()).get(pos);
            this.elements[line][col] = atom;
            line++;
            pos++;
            if (pos == vector.getSize())
                pos = 0;
            if (line == this.nRows) {
                line = 0;
                col++;
            }
        }
    }

    private Matrix(Atomic[][] elements, int nRows, int nCols, int type) {
        this.elements = elements;
        this.nRows = nRows;
        this.nCols = nCols;
        this.type = type;
    }
    
    public Matrix clonation() {
        Atomic[][] nuMatrix = new Atomic[this.nRows][this.nCols];
        // now we fill it up
        for (int i = 0; i < this.nRows; i++) {
            for (int j = 0; j < this.nCols; j++) {
                Atomic atom = this.elements[i][j];
                if (atom.getType() == Atomic.Type.INTEGER) {
                  int one = ((Integer)atom.getValue()).intValue();
                  nuMatrix[i][j] = new Atomic(Atomic.Type.INTEGER, Integer.valueOf(one));
              }
              else if (atom.getType() == Atomic.Type.NUMERIC) {
                  double two = ((Double)atom.getValue()).doubleValue();
                  nuMatrix[i][j] = new Atomic(Atomic.Type.NUMERIC, Double.valueOf(two));
              }
              else if (atom.getType() == Atomic.Type.BOOLEAN) {
                  boolean three = ((Boolean)atom.getValue()).booleanValue();
                  nuMatrix[i][j] = new Atomic(Atomic.Type.BOOLEAN, Boolean.valueOf(three));
              }
              else {
                  String four = ((String)atom.getValue());
                  if (four == null)
                    nuMatrix[i][j] = new Atomic(Atomic.Type.STRING, null);
                  else
                    nuMatrix[i][j] = new Atomic(Atomic.Type.STRING, new String(four));
              }
            }
        }
        return new Matrix(nuMatrix, this.nRows, this.nCols, this.type);
    }
    
    public int getRows() {
        return this.nRows;
    }
    
    public int getColumns() {
        return this.nCols;
    }
    
    public int getType() {
        return this.type;
    }
    
    @Override
    public Object getValue() {
        return this.elements;
    }

    @Override
    public int getSize() {
        return this.nRows * this.nCols;
    }

    @Override
    public Object getValue(int i) {
        if (i > this.nRows * this.nCols)
            return new CompileError("Semantico", "El indice de acceso por lista a matriz se encuentra fuera de rango", 0, 0);
        
        Atomic atom = null;
        int line = 0;
        int col = 0;
        for (int j = 0; j < i; j++) {
            if (j == i - 1) {
                atom = this.elements[line][col];
                break;
            }
            line++;
            if (line == this.nRows) {
                line = 0;
                col++;
            }
        }
        
        return new Vector(atom);
    }

    @Override
    public Object getValue2B(int i) {
        return new CompileError("Semantico", "Las operaciones con [[]] no estan definidas para matrices", 0, 0);
    }

    @Override
    public void expand(int i) {
        // Im not going to use this
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void insertValue(Object obj, int i) {
        if (i > this.nRows * this.nCols) {
            Singleton.insertError(new CompileError("Semantico", "El indice de acceso por lista a matriz se encuentra fuera de rango", 0, 0));
            return;
        }
        
        if (obj instanceof Vector) {
            if (((Vector)obj).getSize() > 1) {
                Singleton.insertError(new CompileError("Semantico", "No puede asignar mas de un elemento a la posicion", 0, 0));
                return;
            }
            
            obj = ((ArrayList<Atomic>)((Vector)obj).getValue()).get(0);
        }
        
        obj = cast(obj);
        
        int line = 0;
        int col = 0;
        for (int j = 0; j < i; j++) {
            if (j == i - 1) {
                this.elements[line][col] = (Atomic)obj;
                break;
            }
            line++;
            if (line == this.nRows) {
                line = 0;
                col++;
            }
        }
    }
    
    private Atomic cast(Object obj) {
        if (this.type == 4) {
            obj = new Atomic(Atomic.Type.STRING, String.valueOf(((Atomic)obj).getValue()));
        }
        else if (this.type == 2) {
            if (((Atomic)obj).getType() == Atomic.Type.STRING) 
                castToString();
            else if (((Atomic)obj).getType() == Atomic.Type.INTEGER) {
                double doub = ((Integer)((Atomic)obj).getValue()).doubleValue();
                obj = new Atomic(Atomic.Type.NUMERIC, Double.valueOf(doub));
            }
            else if (((Atomic)obj).getType() == Atomic.Type.BOOLEAN) {
                double doub = ((Boolean)((Atomic)obj).getValue()).booleanValue() ? 1.0 : 0.0;
                obj = new Atomic(Atomic.Type.NUMERIC, Double.valueOf(doub));
            }
        }
        else if (this.type == 1) {
            if (((Atomic)obj).getType() == Atomic.Type.STRING) 
                castToString();
            else if (((Atomic)obj).getType() == Atomic.Type.NUMERIC) 
                castToNumeric();       
            else if (((Atomic)obj).getType() == Atomic.Type.BOOLEAN) {
                int doub = ((Boolean)((Atomic)obj).getValue()).booleanValue() ? 1 : 0;
                obj = new Atomic(Atomic.Type.INTEGER, Integer.valueOf(doub));
            }
        }
        else {
            if (((Atomic)obj).getType() == Atomic.Type.STRING) 
                castToString();
            else if (((Atomic)obj).getType() == Atomic.Type.NUMERIC) 
                castToNumeric();
            else if (((Atomic)obj).getType() == Atomic.Type.INTEGER) 
                castToInteger();
        }
        return ((Atomic)obj);
    }
    
    private void castToString() {
        this.type = 4;
        for (int i = 0; i < this.nRows; i++) {
            for (int j = 0; j < this.nCols; j++) {
                this.elements[i][j].setType(Atomic.Type.STRING);
                this.elements[i][j].setValue(String.valueOf(this.elements[i][j].getValue()));
            }
        }
    }
    
    private void castToNumeric() {
        this.type = 2;
        for (int i = 0; i < this.nRows; i++) {
            for (int j = 0; j < this.nCols; j++) {
                if (this.elements[i][j].getType() == Atomic.Type.INTEGER) {
                    double doub = ((Integer)this.elements[i][j].getValue()).doubleValue();
                    this.elements[i][j].setValue(Double.valueOf(doub));
                }
                else if (this.elements[i][j].getType() == Atomic.Type.BOOLEAN) {
                    double doub = ((Boolean)this.elements[i][j].getValue()).booleanValue() ? 1.0 : 0.0;
                    this.elements[i][j].setValue(Double.valueOf(doub));
                }
                this.elements[i][j].setType(Atomic.Type.NUMERIC);
            }
        }
    }
    
    private void castToInteger() {
        this.type = 1;
        for (int i = 0; i < this.nRows; i++) {
            for (int j = 0; j < this.nCols; j++) {
                this.elements[i][j].setType(Atomic.Type.INTEGER);
                int doub = ((Boolean)this.elements[i][j].getValue()).booleanValue() ? 1 : 0;
                this.elements[i][j].setValue(Integer.valueOf(doub));
            }
        }
    }
    
    @Override 
    public void insertValueBoth(Object obj, int i, int j) {
        i--;
        j--;
        
        if (i >= this.elements.length) {
            Singleton.insertError(new CompileError("Semantico", "El indice de las filas se encuentra fuera de rango", 0, 0));
            return;
        }
        
        if (j >= this.elements[i].length) {
            Singleton.insertError(new CompileError("Semantico", "El indice de las columnas se encuentra fuera de rango", 0, 0));
            return;
        }
        
        if (obj instanceof Vector) {
            if (((Vector)obj).getSize() > 1) {
                Singleton.insertError(new CompileError("Semantico", "No puede asignar mas que un elemento a una posicion", 0, 0));
                return;
            }
            
            obj = ((ArrayList<Atomic>)((Vector)obj).getValue()).get(0);
        }
        
        obj = cast(obj);
        this.elements[i][j] = (Atomic)obj;
    }
    
    public void insertValueBothVector(Object obj, int i, int j, int[] next) {
        i--;
        j--;
        
        if (i >= this.elements.length) {
            Singleton.insertError(new CompileError("Semantico", "El indice de las filas se encuentra fuera de rango", 0, 0));
            return;
        }
        
        if (j >= this.elements[i].length) {
            Singleton.insertError(new CompileError("Semantico", "El indice de las columnas se encuentra fuera de rango", 0, 0));
            return;
        }
        
        if (obj instanceof Vector) {
            if (((Vector)obj).getSize() > 1) {
                Singleton.insertError(new CompileError("Semantico", "No puede asignar mas que un elemento a una posicion", 0, 0));
                return;
            }
            
            obj = ((ArrayList<Atomic>)((Vector)obj).getValue()).get(0);
        }

        for (int k = 0; k < next.length; k++) {
            if (next[k] != 1) {
                Singleton.insertError(new CompileError("Semantico", "El indice se encuentra fuera de rango", 0, 0));
                return;
            }
        }
        obj = cast(obj);
        this.elements[i][j] = (Atomic)obj;
    }
    
    @Override 
    public void insertValueLeft(Object obj, int i) {
        i--;
        if (i >= this.nRows) {
            Singleton.insertError(new CompileError("Semantico", "El indice de acceso por la izquierda se encuentra fuera de rango", 0, 0));
            return;
        }
        
        if (obj instanceof Vector) {
            if (((Vector)obj).getSize() == this.nCols) {
                for (int j = 0; j < this.nCols; j++) {
                    Atomic atom = ((ArrayList<Atomic>)(((Vector)obj).getValue())).get(j);
                    atom = cast(atom);
                    this.elements[i][j] = atom;
                }
            }
            else if (((Vector)obj).getSize() == 1) {
                Atomic atom = ((ArrayList<Atomic>)(((Vector)obj).getValue())).get(0);
                atom = cast(atom);
                for (int j = 0; j < this.nCols; j++) {
                    this.elements[i][j] = atom;
                }
            }
            else {
                Singleton.insertError(new CompileError("Semantico", "Cantidad de parametros incorrecta", 0, 0));
                return;
            }
        }
        else {
            obj = cast(obj);
            for (int j = 0; j < this.nCols; j++) {
                this.elements[i][j] = (Atomic)obj;
            }
        }
    }
    
    public void insertValueLeftVectors(Object obj, int i, int[] next) {
        i--;
        if (i >= this.nRows) {
            Singleton.insertError(new CompileError("Semantico", "El indice de acceso por la izquierda se encuentra fuera de rango", 0, 0));
            return;
        }
        
        if (obj instanceof Vector) {
            if (((Vector)obj).getSize() > 1) {
                Singleton.insertError(new CompileError("Semantico", "No puede asignar mas que un elemento a una posicion", 0, 0));
                return;
            }
            
            obj = ((ArrayList<Atomic>)((Vector)obj).getValue()).get(0);
        }
        
        int j = next[0];
        j--;
        
        if (j >= this.nCols) {
            Singleton.insertError(new CompileError("Semantico", "El indice de acceso al vector de la matriz se encuentra fuera de rango", 0, 0));
            return;
        }
        
        if (next.length > 1) {
            for (int k = 1; k < next.length; k++) {
                if (next[k] != 1) {
                    Singleton.insertError(new CompileError("Semantico", "El indice se encuentra fuera de rango para el vector en esa posicion de la matrix", 0, 0));
                    return;
                }
            }
        }
        
        obj = cast(obj);
        this.elements[i][j] = (Atomic)obj;
    }
    
    @Override 
    public void insertValueRight(Object obj, int j) {
        j--;
        
        if (j >= this.nCols) {
            Singleton.insertError(new CompileError("Semantico", "El indice de acceso por la derecha se encuentra fuera de rango", 0, 0));
            return;
        }
        
        if (obj instanceof Vector) {
            if (((Vector)obj).getSize() == this.nRows) {
                for (int i = 0; i < this.nRows; i++) {
                    Atomic atom = ((ArrayList<Atomic>)(((Vector)obj).getValue())).get(i);
                    atom = cast(atom);
                    this.elements[i][j] = atom;
                }
            }
            else if (((Vector)obj).getSize() == 1) {
                Atomic atom = ((ArrayList<Atomic>)(((Vector)obj).getValue())).get(0);
                atom = cast(atom);
                for (int i = 0; i < this.nRows; i++) {
                    this.elements[i][j] = atom;
                }
            }
            else {
                Singleton.insertError(new CompileError("Semantico", "Cantidad de parametros incorrecta", 0, 0));
                return;
            }
        }
        else {
            obj = cast(obj);
            for (int i = 0; i < this.nCols; i++) {
                this.elements[i][j] = (Atomic)obj;
            }
        }
    }
    
    public void insertValueRightVectors(Object obj, int j, int[] next) {
        j--;
        if (j >= this.nCols) {
            Singleton.insertError(new CompileError("Semantico", "El indice de acceso por la izquierda se encuentra fuera de rango", 0, 0));
            return;
        }
        
        if (obj instanceof Vector) {
            if (((Vector)obj).getSize() > 1) {
                Singleton.insertError(new CompileError("Semantico", "No puede asignar mas que un elemento a una posicion", 0, 0));
                return;
            }
            
            obj = ((ArrayList<Atomic>)((Vector)obj).getValue()).get(0);
        }

        int i = next[0];
        i--;
        
        if (i >= this.nRows) {
            Singleton.insertError(new CompileError("Semantico", "El indice de acceso al vector de la matriz se encuentra fuera de rango", 0, 0));
            return;
        }
        
        if (next.length > 1) {
            for (int k = 1; k < next.length; k++) {
                if (next[k] != 1) {
                    Singleton.insertError(new CompileError("Semantico", "El indice se encuentra fuera de rango para el vector en esa posicion de la matrix", 0, 0));
                    return;
                }
            }
        }
        
        obj = cast(obj);
        this.elements[i][j] = (Atomic)obj;
    }

    @Override
    public void insertValue2B(Object obj, int i) {
        Singleton.insertError(new CompileError("Semantico", "Las operaciones con [[]] no estan definidas para matrices", 0, 0));
    }
    
    @Override
    public Object accessBoth(int i, int j) {
        i--;
        j--;
        
        if (i >= this.elements.length)
            return new CompileError("Semantico", "El indice de las filas se encuentra fuera de rango", 0, 0);
        
        if (j >= this.elements[i].length)
            return new CompileError("Semantico", "El indice de las columnas se encuentra fuera de rango", 0, 0);
        
        Atomic atom = this.elements[i][j];
        return new Vector(atom);
    }
    
    @Override
    public Object accessLeft(int i) {
        i--;
        if (i >= this.nRows)
            return new CompileError("Semantico", "El indice de acceso por la izquierda se encuentra fuera de rango", 0, 0);
        
        ArrayList<Atomic> atoms = new ArrayList<Atomic>();
        for (int j = 0; j < this.nCols; j++) {
            atoms.add(this.elements[i][j]);
        }
        
        return new Vector(atoms, this.type);
    }
    
    @Override
    public Object accessRight(int j) {
        j--;
        
        if (j >= this.nCols)
            return new CompileError("Semantico", "El indice de acceso por la derecha se encuentra fuera de rango", 0, 0);
        
        ArrayList<Atomic> atoms = new ArrayList<Atomic>();
        for (int i = 0; i < this.nRows; i++)
            atoms.add(this.elements[i][j]);
        
        return new Vector(atoms, this.type);
    }

    // Value methods
    
    @Override
    public Atomic typeof(SymbolsTable env) {
        String type = "matrix";
        if (this.type == 1) 
            type += "-integer";
        else if (this.type == 2) 
            type += "-numeric";
        else if (this.type == 3) 
            type += "-boolean";
        else 
            type += "-string";
        
        return new Atomic(Atomic.Type.STRING, type);
    }
    
    @Override
    public Atomic length(SymbolsTable env) {
        int length = this.nRows * this.nCols;
        return new Atomic(Atomic.Type.INTEGER, Integer.valueOf(length));
    }
    
    @Override
    public Atomic nRow(SymbolsTable env) {
        return new Atomic(Atomic.Type.INTEGER, Integer.valueOf(this.nRows));
    }
    
    @Override
    public Atomic nCol(SymbolsTable env) {
        return new Atomic(Atomic.Type.INTEGER, Integer.valueOf(this.nCols));
    }
    
    @Override
    public Object booleanNegation(SymbolsTable env) {
        if (this.type != 3)
            return new CompileError("Semantico", "Tipo de operando incorrecto, no se puede aplicar negacion booleana a valores no booleanos", 0, 0);
        
        Atomic[][] matrix = new Atomic[this.nRows][this.nCols];
        for (int i = 0; i < this.nRows; i++) {
            for (int j = 0; j < this.nCols; j++) {
                matrix[i][j] = new Atomic(Atomic.Type.BOOLEAN, Boolean.valueOf(!((Boolean)this.elements[i][j].getValue()).booleanValue()));
            }
        }
        return new Matrix(matrix, this.nRows, this.nCols, 3);
    }

    @Override
    public Object aritmeticNegation(SymbolsTable env) {
        if (this.type != 1 && this.type != 2)
            return new CompileError("Semantico", "Tipo de operando incorrecto, no se puede aplicar negacion aritmetica a valores no numericos", 0, 0);
        
        Atomic[][] matrix = new Atomic[this.nRows][this.nCols];
        for (int i = 0; i < this.nRows; i++) {
            for (int j = 0; j < this.nCols; j++) {
                if (this.type == 1)
                    matrix[i][j] = new Atomic(Atomic.Type.INTEGER, Integer.valueOf(-((Integer)this.elements[i][j].getValue()).intValue()));
                else
                    matrix[i][j] = new Atomic(Atomic.Type.NUMERIC, Double.valueOf(-((Double)this.elements[i][j].getValue()).doubleValue()));
            }
        }
        return new Matrix(matrix, this.nRows, this.nCols, this.type);
    }

    private Object validateBaldor(SymbolsTable env, Value op, String operator, int order) {
        if (op instanceof Atomic) {
            if (((Atomic)op).getType() == Atomic.Type.IDENTIFIER) {
                String name = String.valueOf(((Atomic)op).getValue());
                int l = ((Atomic)op).getLine();
                int c = ((Atomic)op).getColumn();
                op = env.getValue(String.valueOf(((Atomic)op).getValue()), l);
                
                if (op == null)
                    return new CompileError("Semantico", "La variable '" + name + "' no ha sido declarada", l, c);
            }
        }
        if (op instanceof Matrix) {
            Matrix matrix = (Matrix)op;
            if (this.nRows == matrix.getRows() && this.nCols == matrix.getColumns()) {
                if (this.type == 1) {
                    if (matrix.type == 1) {
                        if (operator.equals("^")) {
                            Matrix thor = baldorMatrixes(this.elements, (Atomic[][])matrix.getValue(), 2, operator);
                            if (thor == null)
                                return new CompileError("Semantico", "Las operaciones de division y modulo sobre 0 no estan definidas", 0, 0);
                            
                            return thor;
                        }
                              
                        Matrix thor = baldorMatrixes(this.elements, (Atomic[][])matrix.getValue(), 1, operator);
                        if (thor == null)
                                return new CompileError("Semantico", "Las operaciones de division y modulo sobre 0 no estan definidas", 0, 0);
                            
                        return thor;
                    }
                        
                    
                    if (matrix.type == 2) {
                        Matrix thor = baldorMatrixes(this.elements, (Atomic[][])matrix.getValue(), 2, operator);
                        if (thor == null)
                                return new CompileError("Semantico", "Las operaciones de division y modulo sobre 0 no estan definidas", 0, 0);
                            
                        return thor;
                    }
                    
                    if (!operator.equals("+"))
                        return new CompileError("Semantico", "Tipo de operando invalido, no se puede aplicar al operador '" + operator + "'", 0, 0);
                }
                if (this.type == 2) {
                    if (matrix.type == 1 || matrix.type == 2) {
                        Matrix thor = baldorMatrixes(this.elements, (Atomic[][])matrix.getValue(), 2, operator);
                        if (thor == null)
                            return new CompileError("Semantico", "Las operaciones de division y modulo sobre 0 no estan definidas", 0, 0);
                            
                        return thor;
                    } 
                    
                    if (!operator.equals("+"))
                        return new CompileError("Semantico", "Tipo de operando invalido, no se puede aplicar al operador '" + operator + "'", 0, 0);
                }
                
                if (operator.equals("+")) {
                    if (this.type < 4) {
                        if (matrix.type == 4) {
                            Matrix thor = baldorMatrixes(this.elements, (Atomic[][])matrix.getValue(), 4, "+");
                            if (thor == null) {
                                return new CompileError("Semantico", "Operacion invalida, no es posible realizar operaciones con el valor null", 0, 0);
                            }
                            
                            return thor;
                        }
                        
                        return new CompileError("Semantico", "Tipo de operando invalido, no se puede aplicar al operador '" + operator + "'", 0, 0);
                    }
                
                    if (this.type == 4) {
                        Matrix thor = baldorMatrixes(this.elements, (Atomic[][])matrix.getValue(), 4, "+");
                        if (thor == null)
                            return new CompileError("Semantico", "Operacion invalida, no es posible realizar operaciones con el valor null", 0, 0);
                            
                        return thor;
                    }
                }
                
                return new CompileError("Semantico", "Tipo de operando invalido, no se puede aplicar al operador '" + operator + "'", 0, 0);
             
            }
            return new CompileError("Semantico", "Para poder operar matrices estas deben ser del mismo tamanio", 0, 0);
        }
        
        if (op instanceof Vector) {
            if (((Vector)op).getSize() == 1) {
                op = ((ArrayList<Atomic>)((Vector)op).getValue()).get(0);
            }
            else 
                return new CompileError("Semantico", "Tipo de operando incorrecto, no pueden aplicarse operaciones aritmeticas entre matrices y otro tipo de estructura", 0, 0);
        }
        
        if (op instanceof Atomic) {
            if (this.type == 2) {
                if (((Atomic)op).getType() == Atomic.Type.INTEGER) {
                    double val = ((Integer)(((Atomic)op).getValue())).doubleValue();
                    Matrix thor = baldorMatrix(this.elements, val, operator, order);
                    if (thor == null)
                        return new CompileError("Semantico", "Las operaciones de division y modulo sobre 0 no estan definidas", 0, 0);
                            
                    return thor;
                }
                
                if (((Atomic)op).getType() == Atomic.Type.NUMERIC) {
                    double val = ((Double)(((Atomic)op).getValue())).doubleValue();
                    Matrix thor = baldorMatrix(this.elements, val, operator, order);
                    if (thor == null)
                        return new CompileError("Semantico", "Las operaciones de division y modulo sobre 0 no estan definidas", 0, 0);
                            
                    return thor;
                }
                
                if (!operator.equals("+"))
                    return new CompileError("Semantico", "Tipo de operando invalido, incompatible con el operador '" + operator + "'", 0, 0);
            }
            if (this.type == 1) {
                if (((Atomic)op).getType() == Atomic.Type.INTEGER) {
                    Matrix thor;
                    if (operator.equals("^")) {
                        double val = ((Integer)(((Atomic)op).getValue())).doubleValue();
                        thor = baldorMatrix(this.elements, val, operator, order);
                    }
                    else {
                        int val = ((Integer)(((Atomic)op).getValue())).intValue();
                        thor = baldorMatrix(this.elements, val, operator, order);
                    }
                    if (thor == null)
                        return new CompileError("Semantico", "Las operaciones de division y modulo sobre 0 no estan definidas", 0, 0);
                            
                    return thor;
                }
                
                if (((Atomic)op).getType() == Atomic.Type.NUMERIC) {
                    double val = ((Double)(((Atomic)op).getValue())).doubleValue();
                    Matrix thor = baldorMatrix(this.elements, val, operator, order);
                    if (thor == null)
                        return new CompileError("Semantico", "Las operaciones de division y modulo sobre 0 no estan definidas", 0, 0);
                            
                    return thor;
                }
                
                if (!operator.equals("+"))
                    return new CompileError("Semantico", "Tipo de operando invalido, incompatible con el operador '" + operator + "'", 0, 0);
            }
            
            if (operator.equals("+")) {
                if (this.type < 4) {
                    if (((Atomic)op).getType() == Atomic.Type.STRING) {
                        Matrix thor = stringAdding(this.elements, String.valueOf(((Atomic)op).getValue()), order);
                        if (thor == null)
                            return new CompileError("Semantico", "Operacion Invalida, no es posible operar valores null", 0, 0);
                            
                        return thor;
                    }
                    
                    return new CompileError("Semantico", "Tipo de operando invalido, incompatible con el operador '" + operator + "'", 0, 0);
                }
                
                if (this.type == 4) {
                    Matrix thor = stringAdding(this.elements, String.valueOf(((Atomic)op).getValue()), order);
                    if (thor == null)
                        return new CompileError("Semantico", "No es posible operar valores nulos", 0, 0);
                            
                    return thor;
                }
            }
        }
        
        return new CompileError("Semantico", "Tipo de operando incorrecto, no pueden aplicarse operaciones aritmeticas entre matrices y otro tipo de estructura", 0, 0);
    }
    
    private Matrix baldorMatrixes(Atomic[][] m1, Atomic[][] m2, int type, String operator) { 
        Atomic[][] nuMatrix = new Atomic[this.nRows][this.nCols];
        
        for (int i = 0; i < this.nRows; i++) {
            for (int j = 0; j < this.nCols; j++) {
                if (type == 1) {
                    int r = 0;
                
                    if (operator.equals("-"))
                        r = ((Integer)m1[i][j].getValue()).intValue() - ((Integer)m2[i][j].getValue()).intValue();
                    else if (operator.equals("*"))
                        r = ((Integer)m1[i][j].getValue()).intValue() *((Integer)m2[i][j].getValue()).intValue();
                    else if (operator.equals("/")) {
                        if (((Integer)m2[i][j].getValue()).intValue()  == 0)
                            return null;
                    
                        r = ((Integer)m1[i][j].getValue()).intValue() / ((Integer)m2[i][j].getValue()).intValue();
                }
                    else if (operator.equals("%")) {
                        if (((Integer)m2[i][j].getValue()).intValue() == 0)
                            return null;
                    
                        r = ((Integer)m1[i][j].getValue()).intValue() % ((Integer)m2[i][j].getValue()).intValue();
                    }
                    else 
                        r = ((Integer)m1[i][j].getValue()).intValue() + ((Integer)m2[i][j].getValue()).intValue();
                
                    nuMatrix[i][j] = new Atomic(Atomic.Type.INTEGER, Integer.valueOf(r));
                }
                else if (type == 2){
                    double one = ((Number)m1[i][j].getValue()).doubleValue();
                    double two = ((Number)m2[i][j].getValue()).doubleValue();
                    double r = 0;
                
                    if (operator.equals("-"))
                        r = one - two;
                    else if (operator.equals("+"))
                        r = one + two;
                    else if (operator.equals("*"))
                        r = one * two;
                    else if (operator.equals("/")) {
                        if (two == 0.0)
                            return null;
                    
                        r = one / two;
                    }
                    else if (operator.equals("%")) {
                        if (two == 0.0)
                            return null;
                    
                        r = one % two;
                    }
                    else 
                        r = Math.pow(one, two);
                    
                    nuMatrix[i][j] = new Atomic(Atomic.Type.NUMERIC, Double.valueOf(r));
                }
                else {
                    if (m1[i][j].getValue() == null)
                        return null;
                    String one = String.valueOf(m1[i][j].getValue());
                    if (m2[i][j].getValue() == null)
                        return null;
                    String two = String.valueOf(m2[i][j].getValue());
                
                    nuMatrix[i][j] = new Atomic(Atomic.Type.STRING, one + two);
                }
            }
        }
        
        return new Matrix(nuMatrix, this.nRows, this.nCols, type);
    }
    
    private Matrix baldorMatrix(Atomic[][] m1, double val, String operator, int order) { 
        Atomic [][] values = new Atomic[this.nRows][this.nCols];
        
        for (int i = 0; i < this.nRows; i++) {
            for (int j = 0; j < this.nCols; j++) {
                Atomic atom = m1[i][j];
                double doub = ((Number)atom.getValue()).doubleValue();
                double r = 0;
            
                if (operator.equals("-"))
                    r = order == 1 ? doub - val : val - doub;
                else if (operator.equals("+"))
                    r = order == 1 ? doub + val : val + doub;
                else if (operator.equals("*"))
                    r = order == 1 ? doub * val : val * doub;
                else if (operator.equals("/")) {
                    if (order == 1 && val == 0) 
                        return null;
                    else if (order == 2 && doub == 0) 
                        return null;
                
                    r = order == 1 ? doub / val : val / doub;
                }
                else if (operator.equals("%")) {
                    if (order == 1 && val == 0) 
                        return null;
                    else if (order == 2 && doub == 0) 
                        return null;
                
                    r = order == 1 ? doub % val : val % doub;
                }
                else 
                    r = order == 1 ? Math.pow(doub, val) : Math.pow(val, doub);
                    
                values[i][j] = new Atomic(Atomic.Type.NUMERIC, Double.valueOf(r));
            }
        }
        
        return new Matrix(values, this.nRows, this.nCols, 2);
    }
    
    private Matrix baldorMatrix(Atomic[][] m1, int val, String operator, int order) { 
        Atomic [][] values = new Atomic[this.nRows][this.nCols];
        
        for (int i = 0; i < this.nRows; i++) {
            for (int j = 0; j < this.nCols; j++) {
                Atomic atom = m1[i][j];
                int ent = ((Integer)atom.getValue()).intValue();
                int r = 0;
            
                if (operator.equals("-"))
                    r = order == 1 ? ent - val : val - ent;
                else if (operator.equals("+"))
                    r = order == 1 ? ent + val : val + ent;
                else if (operator.equals("*"))
                    r = order == 1 ? ent * val : val * ent;
                else if (operator.equals("/")) {
                    if (order == 1 && val == 0)
                        return null;
                    else if (order == 2 && ent == 0)
                        return null;
                
                    r = order == 1 ? ent / val : val / ent;
                }              
                else if (operator.equals("%")) {
                    if (order == 1 && val == 0)
                        return null;
                    else if (order == 2 && ent == 0)
                        return null;
                
                    r = order == 1 ? ent % val : val % ent;
                } 
                    
                values[i][j] = new Atomic(Atomic.Type.INTEGER, Integer.valueOf(r));
            }
        }
        
        return new Matrix(values, this.nRows, this.nCols, 1);
    }
    
    private Matrix stringAdding(Atomic[][] m1, String val, int order) {
        Atomic[][] values = new Atomic[this.nRows][this.nCols];
        if (val == null)
            return null;
        
        for (int i = 0; i < this.nRows; i++) {
            for (int j = 0; j < this.nCols; j++) {
                Atomic atom = m1[i][j];
                if (atom.getValue() == null)
                return null;
                String one = String.valueOf(atom.getValue());
            
            
                if (order == 1)
                    values[i][j] = new Atomic(Atomic.Type.STRING, one + val);
                else 
                    values[i][j] = new Atomic(Atomic.Type.STRING, val + one);
                }
        }
        
        return new Matrix(values, this.nRows, this.nCols, 4);
    }
    
    @Override
    public Object minus(SymbolsTable env, Value op, int order) {
        return validateBaldor(env, op, "-", order);
    }

    @Override
    public Object plus(SymbolsTable env, Value op, int order) {
        return validateBaldor(env, op, "+", order);
    }

    @Override
    public Object times(SymbolsTable env, Value op, int order) {
        return validateBaldor(env, op, "*", order);
    }

    @Override
    public Object div(SymbolsTable env, Value op, int order) {
        return validateBaldor(env, op, "/", order);
    }

    @Override
    public Object power(SymbolsTable env, Value op, int order) {
        return validateBaldor(env, op, "^", order);
    }

    @Override
    public Object mod(SymbolsTable env, Value op, int order) {
        return validateBaldor(env, op, "%", order);
    }
    
    private Object validateRelational(SymbolsTable env, Value op, String operator, int order) {
        if (op instanceof Atomic) {
            if (((Atomic)op).getType() == Atomic.Type.IDENTIFIER) {
                String name = String.valueOf(((Atomic)op).getValue());
                int l = ((Atomic)op).getLine();
                int c = ((Atomic)op).getColumn();
                op = env.getValue(String.valueOf(((Atomic)op).getValue()), l);
                
                if (op == null)
                    return new CompileError("Semantico", "La variable '" + name + "' no ha sido declarada", l, c);
            }
        }
        
        if (op instanceof Matrix) {
            Matrix matrix = (Matrix)op;
            if (this.nRows != matrix.getRows() || this.nCols != matrix.getColumns())
                return new CompileError("Semantico", "No es posible operar matrices con diferentes dimensiones", 0, 0);
            
            if (this.type == 1 || this.type == 2) {
                if (matrix.type == 1 || matrix.type == 2){
                    return relationalMatrixes(this.elements, (Atomic[][])matrix.getValue(), 2, operator);
                }
                
                return new CompileError("Semantico", "Tipo de operando invalido para el operador '" + operator + "'", 0, 0);
            }
            
            if (this.type == 3) {
                if (!operator.equals("==") && !operator.equals("!="))
                   return new CompileError("Semantico", "Tipo de operando invalido para el operador '" + operator + "'", 0, 0);
                
                if (matrix.type == 3) {
                    return relationalMatrixes(this.elements, (Atomic[][])matrix.getValue(), 3, operator);
                }
                
                return new CompileError("Semantico", "Tipo de operando invalido para el operador '" + operator + "'", 0, 0);
            }
            
            if (this.type == 4) {
                if (matrix.type == 4) {
                    Matrix thor = relationalMatrixes(this.elements, (Atomic[][])matrix.getValue(), 4, operator);
                    if (thor == null)
                        return new CompileError("Semantico", "No es posible operar valores nulos", 0, 0);
                    
                    return thor;
                }
                
                return new CompileError("Semantico", "Tipo de operando invalido para el operador '" + operator + "'", 0, 0);
            }
        }
        
        if (op instanceof Vector) {
            if (((Vector)op).getSize() == 1) {
                op = ((ArrayList<Atomic>)((Vector)op).getValue()).get(0);
            }
            else 
                return new CompileError("Semantico", "Tipo de operando incorrecto, no pueden aplicarse operaciones aritmeticas entre matrices y otro tipo de estructura", 0, 0);
        }
        
        if (op instanceof Atomic) {
            if (this.type == 1 || this.type == 2) {
                if (((Atomic)op).getType() == Atomic.Type.INTEGER || ((Atomic)op).getType() == Atomic.Type.NUMERIC) {
                    double arg = ((Number)((Atomic)op).getValue()).doubleValue();
                    return relationalMatrix(this.elements, arg, order, operator);
                }
                
                return new CompileError("Semantico", "Tipo de operando invalido para el operador '" + operator + "'", 0, 0);
            }
            
            if (this.type == 3) {
                if (!operator.equals("==") && !operator.equals("!="))
                    return new CompileError("Semantico", "Tipo de operando invalido para el operador '" + operator + "'", 0, 0);
                
                if (((Atomic)op).getType() == Atomic.Type.BOOLEAN) {
                    boolean arg = ((Boolean)(((Atomic)op).getValue())).booleanValue();
                    return relationalMatrix(this.elements, arg, operator);
                }
                
                return new CompileError("Semantico", "Tipo de operando invalido para el operador '" + operator + "'", 0, 0);
            }
            
            if (this.type == 4) {
                if (((Atomic)op).getType() == Atomic.Type.STRING) {
                    if (((Atomic)op).getValue() == null)
                        return new CompileError("Semantico", "No es posible operar valores nulos", 0, 0);
                    
                    String str = String.valueOf(((Atomic)op).getValue());
                    Matrix thor = relationalMatrix(this.elements, str, order, operator);
                    if (thor == null)
                        return new CompileError("Semantico", "No es posible operar valores nulos", 0, 0);
                    
                    return thor;
                }
            }
        }
        
        //operand is an array, list or matrix
        return new CompileError("Semantico", "Tipo de operando invalido para el operador '" + operator + "'", 0, 0);
    }
    
    private Matrix relationalMatrixes(Atomic[][] m1, Atomic[][] m2, int type, String operator) {
        Atomic[][] values = new Atomic[this.nRows][this.nCols];
        
        for (int i = 0; i < this.nRows; i++) {
            for (int j = 0; j < this.nCols; j++) {
                if (type == 2) {
                    double one = ((Number)m1[i][j].getValue()).doubleValue();
                    double two = ((Number)m2[i][j].getValue()).doubleValue();
                
                    boolean flag = false;
                    switch (operator) {
                        case "<":
                            flag = one < two;
                            break;
                        case ">":
                            flag = one > two;
                            break;
                        case "<=":
                            flag = one <= two;
                            break;
                        case ">=":
                            flag = one >= two;
                            break;
                        case "==":
                            flag = one == two;
                            break;
                        case "!=":
                            flag = one != two;
                            break;
                    }
                
                    values[i][j] = new Atomic(Atomic.Type.BOOLEAN, Boolean.valueOf(flag));
                }
                else if (type == 3) {
                    boolean one = ((Boolean)m1[i][j].getValue()).booleanValue();
                    boolean two = ((Boolean)m2[i][j].getValue()).booleanValue();
                
                    boolean flag = false;
                    if (operator.equals("=="))
                        flag = one == two;
                    else
                        flag = one != two;
                
                    values[i][j] = new Atomic(Atomic.Type.BOOLEAN, Boolean.valueOf(flag));
                }
                else {
                    if (m1[i][j].getValue() == null || m2[i][j].getValue() == null)
                        return null;
                    String one = String.valueOf(m1[i][j].getValue());
                    String two = String.valueOf(m2[i][j].getValue());
                
                    boolean flag = false;
                    switch (operator) {
                        case "<":
                            flag = one.compareTo(two) < 0;
                            break;
                        case ">":
                            flag = one.compareTo(two) > 0;
                            break;
                        case "<=":
                            flag = one.compareTo(two) <= 0;
                            break;
                        case ">=":
                            flag = one.compareTo(two) >= 0;
                            break;
                        case "==":
                            flag = one.compareTo(two) == 0;
                            break;
                        case "!=":
                            flag = one.compareTo(two) != 0;
                            break;
                    }
                
                    values[i][j] = new Atomic(Atomic.Type.BOOLEAN, flag);
                }
            }
        }
        
        return new Matrix(values, this.nRows, this.nCols, 3);
    }
    
    private Matrix relationalMatrix(Atomic[][] m1, double doub, int order, String operator) {
        Atomic[][] values = new Atomic[this.nRows][this.nCols];
        
        for (int i = 0; i < this.nRows; i++) {
            for (int j = 0; j < this.nCols; j++) {
                Atomic atom = m1[i][j];
                double dib = ((Number)atom.getValue()).doubleValue();
            
                boolean flag  = false;
                switch (operator) {
                    case "<":
                        flag = order == 1 ? dib < doub : doub < dib;
                        break;
                    case ">":
                        flag = order == 1 ? dib > doub : doub > dib;
                        break;
                    case "<=":
                        flag = order == 1 ? dib <= doub : doub <= dib;
                        break;
                    case ">=":
                        flag = order == 1 ? dib >= doub : doub >= dib;
                        break;
                    case "==":
                        flag = order == 1 ? dib == doub : doub == dib;
                        break;
                    case "!=":
                        flag = order == 1 ? dib != doub : doub != dib;
                        break;
                }
            
                values[i][j] = new Atomic(Atomic.Type.BOOLEAN, Boolean.valueOf(flag));
            }
        }
        
        return new Matrix(values, this.nRows, this.nCols, 3);
    }
    
    private Matrix relationalMatrix(Atomic[][] m1, boolean bool, String operator) {
        Atomic[][] values = new Atomic[this.nRows][this.nCols];
        
        for (int i = 0; i < this.nRows; i++) {
            for (int j = 0; j < this.nCols; j++) {
                Atomic atom = m1[i][j];
                boolean dib = ((Boolean)atom.getValue()).booleanValue();
            
                boolean flag  = false;
                switch (operator) {
                    case "==":
                        flag = dib == bool;
                        break;
                    case "!=":
                        flag = dib != bool;
                        break;
                }
            
                values[i][j] = new Atomic(Atomic.Type.BOOLEAN, Boolean.valueOf(flag));
            }
        }
        return new Matrix(values, this.nRows, this.nCols, 3);
    }
    
    private Matrix relationalMatrix(Atomic[][] m1, String str, int order, String operator) {
        Atomic[][] values = new Atomic[this.nRows][this.nCols];
        
        for (int i = 0; i < this.nRows; i++) {
            for (int j = 0; j < this.nCols; j++) {
                Atomic atom = m1[i][j];
                if (atom.getValue() == null)
                    return null;
                String one = String.valueOf(atom.getValue());
            
                boolean flag  = false;
                switch (operator) {
                    case "<":
                        flag = order == 1 ? one.compareTo(str) < 0 : str.compareTo(one) < 0;
                        break;
                    case ">":
                        flag = order == 1 ? one.compareTo(str) > 0 : str.compareTo(one) > 0;
                        break;
                    case "<=":
                        flag = order == 1 ? one.compareTo(str) <= 0 : str.compareTo(one) <= 0;
                        break;
                    case ">=":
                        flag = order == 1 ? one.compareTo(str) >= 0 : str.compareTo(one) >= 0;
                        break;
                    case "==":
                        flag = order == 1 ? one.compareTo(str) == 0 : str.compareTo(one) == 0;
                        break;
                    case "!=":
                        flag = order == 1 ? one.compareTo(str) != 0 : str.compareTo(one) != 0;
                        break;
                }
            
                values[i][j] = new Atomic(Atomic.Type.BOOLEAN, Boolean.valueOf(flag));
            }
        }
        
        return new Matrix(values, this.nRows, this.nCols, 3);
    }

    @Override
    public Object lesser(SymbolsTable env, Value op, int order) {
        return validateRelational(env, op, "<", order);
    }

    @Override
    public Object greater(SymbolsTable env, Value op, int order) {
        return validateRelational(env, op, ">", order);
    }

    @Override
    public Object lesserEquals(SymbolsTable env, Value op, int order) {
        return validateRelational(env, op, "<=", order);
    }

    @Override
    public Object greaterEquals(SymbolsTable env, Value op, int order) {
        return validateRelational(env, op, ">=", order);
    }

    @Override
    public Object equals(SymbolsTable env, Value op, int order) {
        return validateRelational(env, op, "==", order);
    }

    @Override
    public Object notEquals(SymbolsTable env, Value op, int order) {
        return validateRelational(env, op, "!=", order);
    }
    
    private Object validateBoolean(SymbolsTable env, Value op, String operator) {
        if (this.type != 3)
            return new CompileError("Semantico", "Tipo de operando invalido para el operador '" + operator + "'", 0, 0);
        
        if (op instanceof Atomic) {
            if (((Atomic)op).getType() == Atomic.Type.IDENTIFIER) {
                String name = String.valueOf(((Atomic)op).getValue());
                int l = ((Atomic)op).getLine();
                int c = ((Atomic)op).getColumn();
                op = env.getValue(String.valueOf(((Atomic)op).getValue()), l);
                
                if (op == null)
                    return new CompileError("Semantico", "La variable '" + name + "' no ha sido declarada", l, c);
            }
        }
        
        if (op instanceof Vector) {
            if (((Vector)op).getSize() == 1) {
                op = ((ArrayList<Atomic>)((Vector)op).getValue()).get(0);
            }
        }
        
        if (op instanceof Matrix) {
            Matrix matrix = (Matrix)op;
            if (this.nRows != matrix.getRows() || this.nCols != matrix.getColumns())
                return new CompileError("Semantico", "No es posible operar matrices con diferentes dimensiones", 0, 0);
            
            if (matrix.type == 3) {
                return booleanMatrixes(this.elements, (Atomic[][])matrix.getValue(), operator);
            }
                    
            return new CompileError("Semantico", "Tipo de operando invalido para el operador '" + operator + "'", 0, 0);
           
        }
        
        if (op instanceof Atomic) {
            if (((Atomic)op).getType() == Atomic.Type.BOOLEAN) {
                Atomic val = (Atomic)op;
                boolean bool = ((Boolean)val.getValue()).booleanValue();
                return booleanMatrix(this.elements, bool, operator);
            }
            
            return new CompileError("Semantico", "Tipo de operando invalido para el operador '" + operator + "'", 0, 0);
        }
        
        // op is vector, array or list
        return new CompileError("Semantico", "Tipo de operando invalido para el operador '" + operator + "'", 0, 0);
    }
    
    private Matrix booleanMatrixes(Atomic[][] m1, Atomic[][] m2, String operator) {
        Atomic[][] values = new Atomic[this.nRows][this.nCols];
        
        for (int i = 0; i < this.nRows; i++) {
            for (int j = 0; j < this.nCols; j++) {
                boolean one = ((Boolean)m1[i][j].getValue()).booleanValue();
                boolean two = ((Boolean)m2[i][j].getValue()).booleanValue();
            
                boolean flag = false;
                if (operator.equals("&"))
                    flag = one && two;
                else 
                    flag = one || two;
            
                values[i][j] = new Atomic(Atomic.Type.BOOLEAN, Boolean.valueOf(flag));
            }
        }
        
        return new Matrix(values, this.nRows, this.nCols, 3);
    }
    
    private Matrix booleanMatrix(Atomic[][] m1, boolean bool, String operator) {
        Atomic[][] values = new Atomic[this.nRows][this.nCols];
        
        for (int i = 0; i < this.nRows; i++) {
            for (int j = 0; j < this.nCols; j++) {
                boolean one = ((Boolean)m1[i][j].getValue()).booleanValue();
            
                boolean flag = false;
                if (operator.equals("&"))
                    flag = one && bool;
                else 
                    flag = one || bool;
            
                values[i][j] = new Atomic(Atomic.Type.BOOLEAN, Boolean.valueOf(flag));
            }
        }
        
        return new Matrix(values, this.nRows, this.nCols, 3);
    }

    @Override
    public Object and(SymbolsTable env, Value op) {
        return validateBoolean(env, op, "&");
    }

    @Override
    public Object or(SymbolsTable env, Value op) {
        return validateBoolean(env, op, "|");
    }
    
}

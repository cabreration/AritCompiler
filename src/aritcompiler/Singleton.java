/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aritcompiler;

import APIServices.CompileError;
import Symbols.Function;
import Symbols.SymbolRef;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;
import javax.swing.JFrame;
import javax.swing.WindowConstants;
import org.knowm.xchart.SwingWrapper;

/**
 *
 * @author jacab
 */
public class Singleton {
    
    private static Hashtable<String, Function> functions = new Hashtable<String, Function>();
    private static ArrayList<CompileError> errors = new ArrayList<CompileError>();
    private static ArrayList<String> console = new ArrayList<String>();
    private static ArrayList<SwingWrapper> figures = new ArrayList<SwingWrapper>();
    private static Hashtable<String, SymbolRef> symbolsRefs = new Hashtable<String, SymbolRef>();
    
    private Singleton() {}
    
    public static Hashtable<String, Function> functions() {
        return functions;
    }
    
    public static Function getFunction(String name) {
        boolean flag = functions.containsKey(name);
        if (flag) {
            return functions.get(name);
        }
        
        return null;
    }
    
    public static ArrayList<CompileError> errors() {
        return errors;
    }
    
    public static void insertError(CompileError error) {
        errors.add(error);
    }
    
    public static boolean insertFunction(Function function) {
        String name = function.getName();
        
        if (functions.containsKey(name))
            return false;
        
        switch (name.toLowerCase()) {
            case "print":
            case "c":
            case "list":
            case "matrix":
            case "array":
            case "typeof":
            case "length":
            case "ncol":
            case "nrow":
            case "stringlength":
            case "remove":
            case "touppercase":
            case "tolowercase":
            case "trunk":
            case "round":
            case "mean":
            case "mode":
            case "median":
            case "plot":
            case "pie":
            case "hist":
            case "barplot":
                return false;
        }
        
        functions.put(name, function);
        
        return true;
    }
    
    public static void newCompilation() {
        console = new ArrayList<String>();
        figures = new ArrayList<SwingWrapper>();
        functions = new Hashtable<String, Function>();
        symbolsRefs = new Hashtable<String, SymbolRef>();
        errors = new ArrayList<CompileError>();
    }
    
    public static void insertPrint(String str) {
        if (str == null)
            return;
        if (str.equals(""))
            return;
        console.add(str);
    }
    
    public static String print() {
        StringBuilder builder = new StringBuilder();
        for (String str : console) {
            builder.append(str + "\n");
        }
        return builder.toString();
        //System.out.println(builder.toString());
    }
    
    public static void insertFigure(SwingWrapper figure) {
        if (figure == null)
            return;
        figures.add(figure);
    }
    
    public static void showFigure(int i) {
        JFrame frame = figures.get(i).displayChart();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
    }
    
    public static void showFigures() {
        for (SwingWrapper wrapper : figures) {
            JFrame frame = wrapper.displayChart();
            frame.setVisible(true);
            frame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        }
    }
    
    public static void reportErrors() {
        String html = buildHtml( new String[] {"#", "Tipo", "Descripcion", "Fila", "Columna"}, 1 );
        
        String fileName = "./reports/errors/errors.html";
        writeFile(fileName, html);
    }
    
    private static String buildHtml(String[] categories, int type) {
        StringBuilder builder = new StringBuilder();
        builder.append("<!DOCTYPE html>\n");
        builder.append("\t<html>\n");
        builder.append("\t\t<head>\n");
        builder.append("<link rel=\"stylesheet\" href=\"https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/css/bootstrap.min.css\" integrity=\"sha384-Vkoo8x4CGsO3+Hhxv8T/Q5PaXtkKtu6ug5TOeNV6gBiFeWPGFN9MuhOf23Q9Ifjh\" crossorigin=\"anonymous\">");
        builder.append("\t\n</head>\n");
        builder.append("\t\t<body>\n");
        // Build the table here
        builder.append("\t\t\t<table class=\"table table-striped table-dark\"\n");
        builder.append("\t\t\t\t<thead>\n");
        builder.append("\t\t\t\t\t<th scope=\"col\">" + categories[0] + "</th>\n");
        builder.append("\t\t\t\t\t<th scope=\"col\">" + categories[1] + "</th>\n");
        builder.append("\t\t\t\t\t<th scope=\"col\">" + categories[2] + "</th>\n");
        builder.append("\t\t\t\t\t<th scope=\"col\">" + categories[3] + "</th>\n");
        builder.append("\t\t\t\t\t<th scope=\"col\">" + categories[4] + "</th>\n");
        builder.append("\t\t\t\t</thead>\n");
        builder.append("\t\t\t\t<tbody>\n");
        
        if (type == 1)
            builder.append(buildRows());
        else {
            builder.append(buildSymbols());
        }
        
        builder.append("\t\t\t\t</tbody>\n");
        builder.append("\t\t\t</table>\n");
        // Build table here
        builder.append("\t\t</body>\n");
        builder.append("\t</html>\n");
        return builder.toString();
    }
    
    private static void writeFile(String fileName, String content) {
        File file = new File(fileName);
        FileWriter writer = null;
        try {
            writer = new FileWriter(file);
            writer.write(content);
            writer.close();
        }
        catch(Exception e) {
            System.err.println(e.getMessage());
        }
    }
    
    private static String buildRows() {
        StringBuilder builder = new StringBuilder();
        int i = 1;
        for (CompileError error: errors) {
            builder.append("\t\t\t\t\t<tr>\n");
            builder.append("\t\t\t\t\t\t<th scope=\"row\">" + i + "</th>\n");
            builder.append("\t\t\t\t\t\t<td scope=\"row\">" + error.getType() + "</td>\n");
            builder.append("\t\t\t\t\t\t<td scope=\"row\">" + error.getDescription() + "</td>\n");
            builder.append("\t\t\t\t\t\t<td scope=\"row\">" + error.getRow() + "</td>\n");
            builder.append("\t\t\t\t\t\t<td scope=\"row\">" + error.getColumn() + "</td>\n");
            builder.append("\t\t\t\t\t</tr>\n");
            i++;
        }
        return builder.toString();
    }
    
    private static String buildSymbols() {
        StringBuilder builder = new StringBuilder();
        int i = 1;
        Set<String> functionKeys = functions.keySet();
        for (String key : functionKeys) {
            Function f = functions.get(key);
            builder.append("\t\t\t\t\t<tr>\n");
            builder.append("\t\t\t\t\t\t<th scope=\"row\">" + i + "</th>\n");
            builder.append("\t\t\t\t\t\t<td scope=\"row\">" + f.getName() + "</td>\n");
            builder.append("\t\t\t\t\t\t<td scope=\"row\">Función</td>\n");
            builder.append("\t\t\t\t\t\t<td scope=\"row\"> (" + f.getLine() + ", " + f.getColumn() + ")</td>\n");
            builder.append("\t\t\t\t\t\t<td scope=\"row\">" + f.getReferences() + "</td>\n");
            builder.append("\t\t\t\t\t</tr>\n");
            i++;
        }
        Set<String> symbolsKeys = symbolsRefs.keySet();
        for (String key : symbolsKeys) {
            SymbolRef s = symbolsRefs.get(key);
            builder.append("\t\t\t\t\t<tr>\n");
            builder.append("\t\t\t\t\t\t<th scope=\"row\">" + i + "</th>\n");
            builder.append("\t\t\t\t\t\t<td scope=\"row\">" + s.getId() + "</td>\n");
            builder.append("\t\t\t\t\t\t<td scope=\"row\">" + s.getType() + "</td>\n");
            builder.append("\t\t\t\t\t\t<td scope=\"row\"> (" + s.getLine() + ", " + s.getColumn() + ")</td>\n");
            builder.append("\t\t\t\t\t\t<td scope=\"row\">" + s.getReferences() + "</td>\n");
            builder.append("\t\t\t\t\t</tr>\n");
            i++;
        }
        return builder.toString();
    }
    
    public static void reportSymbols() {
        String html = buildHtml( new String[] {"#", "Id", "Tipo", "Declaración (fila, columna)", "Referencias (fila)"}, 2 );
        
        String fileName = "./reports/table/symbols.html";
        writeFile(fileName, html);
    }
    
    public static void insertRef(SymbolRef ref) {
        String id = ref.getId();
        if (symbolsRefs.containsKey(id)) {
            //actualizamos tipo y referncias
            SymbolRef s = symbolsRefs.get(id);
            ref.setColumn(s.getColumn());
            ref.setLine(s.getLine());
            ref.setReferences(s.getReferences());
            ref.addReference(String.valueOf(ref.getLine()));
            
        }
        symbolsRefs.put(id, ref);
    }
    
    public static void addRef(String id, int line) {
        if (symbolsRefs.containsKey(id)) {
            symbolsRefs.get(id).addReference(String.valueOf(line));
        }
    }
}

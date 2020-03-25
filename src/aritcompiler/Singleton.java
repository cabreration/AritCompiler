/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aritcompiler;

import APIServices.CompileError;
import Symbols.Function;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Hashtable;
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
        frame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
    }
    
    public static void showFigures() {
        for (SwingWrapper wrapper : figures) {
            JFrame frame = wrapper.displayChart();
            frame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        }
    }
    
    public static void reportErrors() {
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
        builder.append("\t\t\t\t\t<th scope=\"col\">#</th>\n");
        builder.append("\t\t\t\t\t<th scope=\"col\">Tipo</th>\n");
        builder.append("\t\t\t\t\t<th scope=\"col\">Descripcion</th>\n");
        builder.append("\t\t\t\t\t<th scope=\"col\">Fila</th>\n");
        builder.append("\t\t\t\t\t<th scope=\"col\">Columna</th>\n");
        builder.append("\t\t\t\t</thead>\n");
        builder.append("\t\t\t\t<tbody>\n");
        builder.append(buildRows());
        builder.append("\t\t\t\t</tbody>\n");
        builder.append("\t\t\t</table>\n");
        // Build table here
        builder.append("\t\t</body>\n");
        builder.append("\t</html>\n");
        
        File file = new File("./reports/errors/errors.html");
        FileWriter writer = null;
        try {
            writer = new FileWriter(file);
            writer.write(builder.toString());
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
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aritcompiler;

import APIServices.CompileError;
import Symbols.Function;
import java.util.ArrayList;
import java.util.Hashtable;

/**
 *
 * @author jacab
 */
public class Singleton {
    
    private static Hashtable<String, Function> functions = new Hashtable<String, Function>();
    private static ArrayList<CompileError> errors = new ArrayList<CompileError>();
    private static ArrayList<String> console = new ArrayList<String>();
    
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
    }
    
    public static void insertPrint(String str) {
        console.add(str);
    }
    
    public static void print() {
        StringBuilder builder = new StringBuilder();
        for (String str : console) {
            builder.append(str + "\n");
        }
        System.out.println(builder.toString());
    }
}

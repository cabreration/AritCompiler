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
    
    private Singleton() {}
    
    public static Hashtable<String, Function> functions() {
        return functions;
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
                return false;
        }
        
        functions.put(name, function);
        
        return true;
    }
}
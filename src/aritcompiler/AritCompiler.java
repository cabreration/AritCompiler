/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aritcompiler;

import APIServices.Node;
import APIServices.TreePrinter;
import APIServices.TreeProcesor;
import Instructions.Instruction;
import JFlexNCup.Parser;
import JFlexNCup.Scanner;
import JavaCC.Grammar;
import Symbols.SymbolsTable;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java_cup.runtime.Symbol;

/**
 *
 * @author jacab
 */
public class AritCompiler {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        TextEditor txtE = new TextEditor();
        txtE.setVisible(true);
    }
    
    public static void javaccTry() {
        
        try {
            String name = "C:\\Users\\jacab\\Documents\\Compi 2\\R-it\\Pruebas\\tree.arit";
            Grammar parser = new Grammar(new BufferedReader(new FileReader(name)));
            Node root = parser.Root();
            TreePrinter.printTree(root, "javaccTree");
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
    
}

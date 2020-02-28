/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aritcompiler;

import APIServices.Node;
import APIServices.TreePrinter;
import JFlexNCup.Parser;
import JFlexNCup.Scanner;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Reader;
import java.io.StringReader;
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
        cupTry();
    }
    
    public static void cupTry() {
        BufferedReader bufferedReader;
        String name = "C:\\Users\\jacab\\Documents\\Compi 2\\R-it\\Pruebas\\tree.arit";
        try {
            bufferedReader = new BufferedReader(new FileReader(name));
            String parcial, completo= "";
            parcial = bufferedReader.readLine();
            while (parcial != null) 
            {
                completo += parcial +"\n";
                parcial = bufferedReader.readLine();
            }
            
            System.out.println(completo);
            bufferedReader.close();

            /* Here we will try to parse it and see what does it generate */
            Reader reader = new StringReader(completo); 
            Scanner scanner = new Scanner(reader);
            Parser parser = new Parser(scanner);
            Symbol parse_tree = null;
            
            try {
                parse_tree = parser.debug_parse(); 
                Node root = parser.root;
                TreePrinter.printTree(root);
            }
            catch (Exception e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    
    
    public static void javaccTry() {}
    
}

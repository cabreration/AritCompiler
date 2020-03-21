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
import org.knowm.xchart.PieChart;
import org.knowm.xchart.PieChartBuilder;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.style.PieStyler.AnnotationType;
import org.knowm.xchart.style.Styler.ChartTheme;

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
        Node root = cupTry();
        if (root != null) {
            TreeProcesor.processFunctions(root);
            ArrayList<Instruction> sentences = TreeProcesor.processTree(root);
            SymbolsTable env = new SymbolsTable("global");
            for (Instruction ins : sentences) {
                if (ins != null)
                    ins.process(env);
            }
            Singleton.print();
        }
    }
   
    public static Node cupTry() {
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
            
            //System.out.println(completo);
            bufferedReader.close();

            /* Here we will try to parse it and see what does it generate */
            Reader reader = new StringReader(completo); 
            Scanner scanner = new Scanner(reader);
            Parser parser = new Parser(scanner);
            Symbol parse_tree = null;
            
            try {
                //parse_tree = parser.debug_parse();
                parse_tree = parser.parse();
                Node root = parser.root;
                TreePrinter.printTree(root);
                return root;
            }
            catch (Exception e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
    
    
    public static void javaccTry() {
        
        try {
            String name = "C:\\Users\\jacab\\Documents\\Compi 2\\R-it\\Pruebas\\tree.arit";
            Grammar parser = new Grammar(new BufferedReader(new FileReader(name)));
            Node root = parser.Root();
            TreePrinter.printTree(root);
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
    
}

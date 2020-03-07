/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package APIServices;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

/**
 *
 * @author jacab
 */
public class TreePrinter {
    
    private static int counter;
    private static StringBuilder graph;
    
    private static String GetDot(Node root) {
        graph = new StringBuilder("digraph G{");
        graph.append("nodo0[label=\"");
        graph.append(Scape(root.getNodeType()));
        graph.append("\"];\n");
        counter = 1;
        RunTree("nodo0", root);
        graph.append("}");
        return graph.toString();
    }
    
    private static void RunTree(String father, Node node) {
        
        for (Node child : node.getChildren()) {
            String childsName = "node" + String.valueOf(counter);
            graph.append(childsName);
            if (child.getNodeType().equals("string value") || child.getNodeType().equals("identifier")
                    || child.getNodeType().equals("numeric value") || child.getNodeType().equals("bool value")
                    || child.getNodeType().equals("integer value"))
                graph.append("[label=\"" + Scape(child.getNodeType() + ": " + String.valueOf(child.getContent())) + "\"];\n");
            else
                graph.append("[label=\"" + Scape(child.getNodeType()) + "\"];\n");
            graph.append(father);
            graph.append("->");
            graph.append(childsName);
            graph.append(";\n");
            counter++;
            RunTree(childsName, child);
        }
    }
    
    private static String Scape(String buffer) {
            buffer = buffer.replace("\\", "\\\\");
            buffer = buffer.replace("\"", "\\\"");
            return buffer;
    }
    
    public static void printTree(Node root) {
        String dot = GetDot(root);
        
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("cupTree.dot"));
            writer.write(dot);
            writer.close();
            
            String directory = new File("").getAbsolutePath();
            System.out.println(directory);
            Runtime rt = Runtime.getRuntime();
            //rt.exec("cd " + "\""+directory+"\"");
            rt.exec(new String[] {"dot", "-Tpng", "cupTree.dot", "-o", "cupTree.png"});
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
        
    }
}

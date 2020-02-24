/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package JFlexNCup;

/**
 *
 * @author jacab
 */
public class Generator {
    
    public static void main(String[] args) 
    {
        generateTools();
    }
    
    public static void generateTools() 
    {
        try 
        {
            String route = "src/JFlexNCup/";
            String oflex[] = {route + "Lexic.jflex", "-d", route};
            
            String ocup[] = {"-destdir", route, "-symbols", "Sym", "-parser", "Parser", route + "Sintactic.cup"};
            java_cup.Main.main(ocup);
            jflex.Main.generate(oflex);
        }
        catch (Exception e) 
        {
            e.printStackTrace();
        }
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Symbols;

import Instructions.Instruction;
import java.util.ArrayList;

/**
 *
 * @author jacab
 */
public class Function {
    
    private String name;
    private ArrayList<Object> parameters;
    private ArrayList<Instruction> sentences;
    
    public Function(String name) {
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }
}

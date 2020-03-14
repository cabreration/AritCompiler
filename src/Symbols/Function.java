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
    private ArrayList<Parameter> parameters;
    private ArrayList<Instruction> sentences;
    private int line;
    private int column;

    public Function(String name, ArrayList<Parameter> parameters, ArrayList<Instruction> sentences, int line, int column) {
        this.name = name;
        this.parameters = parameters;
        this.sentences = sentences;
        this.line = line;
        this.column = column;
    }

    public Function(String name, ArrayList<Instruction> sentences, int line, int column) {
        this.name = name;
        this.sentences = sentences;
        this.line = line;
        this.column = column;
    }
    
    public String getName() {
        return this.name;
    }

    public ArrayList<Parameter> getParameters() {
        return parameters;
    }

    public ArrayList<Instruction> getSentences() {
        return sentences;
    }
}

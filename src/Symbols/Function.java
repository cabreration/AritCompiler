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
    private String references;

    public Function(String name, ArrayList<Parameter> parameters, ArrayList<Instruction> sentences, int line, int column) {
        this.name = name;
        this.parameters = parameters;
        this.sentences = sentences;
        this.line = line;
        this.column = column;
        this.references = "";
    }

    public Function(String name, ArrayList<Instruction> sentences, int line, int column) {
        this.name = name;
        this.sentences = sentences;
        this.line = line;
        this.column = column;
        this.references = "";
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

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    public String getReferences() {
        return references;
    }
    
    public void addReference(String ref) {
        if (this.references == null || this.references.equals(""))
            this.references += ref;
        else {
            if (!this.references.contains(ref))
                this.references += "," + ref;
        }
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Symbols;

/**
 *
 * @author jacab
 */
public class SymbolRef {
    private String id;
    private String type;
    private int line;
    private int column;
    private String references;

    public SymbolRef(String id, String type, int line, int column) {
        this.id = id;
        this.type = type;
        this.line = line;
        this.column = column;
        this.references = "";
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
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

    public void setType(String type) {
        this.type = type;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public void setReferences(String references) {
        this.references = references;
    }
    
    public void addReference(String ref) {
        if (this.references == null || this.references.equals(""))
            this.references += ref;
        else {
            if (!this.references.contains(ref)) {
                this.references += "," + ref; 
            }
        }
    }
}

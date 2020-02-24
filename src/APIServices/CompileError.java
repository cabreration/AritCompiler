/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package APIServices;

/**
 *
 * @author jacab
 */
public class CompileError {
    private String type;
    private String description;
    private int row;
    private int column;

    public CompileError(String type, String description, int row, int column) {
        this.type = type;
        this.description = description;
        this.row = row;
        this.column = column;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }
}

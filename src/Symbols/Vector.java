/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Symbols;

import java.util.ArrayList;

/**
 *
 * @author jacab
 */
public class Vector extends Symbol {

    private ArrayList<Object> content;
    private int type; // 1 - integer, 2 - numeric, 3 - bool, 4 - string
    
    public Vector(String name, Object value) {
        super(name);
        content = new ArrayList<Object>();
        
        /* Define type of the vector */
    }
    
    public Vector(String name, ArrayList<Object> values) {
        super(name);
        content = values;
        
        /* Define type of the vector */
    }
    
    @Override
    public Object getValue() {
        return content;
    }
    
    public int size() {
        return this.content.size();
    }
    
    public int type() {
        return this.type;
    }
    
}

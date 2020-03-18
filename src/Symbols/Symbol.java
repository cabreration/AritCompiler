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
public interface Symbol {
    
    public Object getValue();
    
    public int getSize();
    
    public Object getValue(int i);
    
    public Object getValue2B(int i);
    
    public Object accessBoth(int i, int j);
    
    public Object accessLeft(int i);
    
    public Object accessRight(int j);
    
    public void expand(int i);
    
    public void insertValue(Object obj, int i);
    
    public void insertValue2B(Object obj, int i);
}

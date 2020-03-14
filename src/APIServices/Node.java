/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package APIServices;

import java.util.ArrayList;

/**
 *
 * @author jacab
 */
public class Node {
    private String nodeType;
    private int row;
    private int column;
    private Object content;
    private ArrayList<Node> children;
    
    public Node(String nodeType, int row, int column, Object content, ArrayList<Node> children) {
        this.content = content;
        this.row = row;
        this.column = column;
        this.children = children;
        this.nodeType = nodeType;
    }
    
    public Node(String nodeType, int row, int column, Object content) {
        this.content = content;
        this.row = row;
        this.column = column;
        children = new ArrayList<Node>();
        this.nodeType = nodeType;
    }
    
    public Node(String nodeType) {
        this.nodeType = nodeType;
        this.row = 0;
        this.column = 0;
        children = new ArrayList<Node>();
        this.content = null;
    }

    public Object getContent() {
        return content;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }
    
    public Node getChildAt(int i) {
        return children.get(i);
    }
    
    public void deleteChildAt(int i) {
        if (i > children.size() - 1)
            return;
        
        children.remove(i);
    }
    
    public String getNodeType() {
        return nodeType;
    }
    
    public void addChildren(Node node) {
        children.add(node);
    }
    
    public void addChildrenAt(int i, Node node) {
        children.add(i, node);
    }
    
    public int getChildrenCount() {
        return children.size();
    }
    
    public ArrayList<Node> getChildren() {
        return children;
    }
}

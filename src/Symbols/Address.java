/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Symbols;

import Expressions.Expression;

/**
 *
 * @author jacab
 */
public class Address {
    private int type; // 1 - [], 2 - [[]]
    private Expression address;

    public Address(int type, Expression address) {
        this.type = type;
        this.address = address;
    }

    public int getType() {
        return type;
    }

    public Expression getAddress() {
        return address;
    }
}

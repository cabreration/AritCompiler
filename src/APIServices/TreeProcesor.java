/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package APIServices;

import Expressions.Atomic;
import Expressions.Binary;
import Expressions.Expression;
import Expressions.Ternary;
import Expressions.Unary;
import Instructions.Asignment;
import Instructions.Function_Call;
import Instructions.Instruction;
import Symbols.Function;
import java.util.ArrayList;

/**
 *
 * @author jacab
 */
public class TreeProcesor {
    
    public static ArrayList<Function> processFunctions(Node root) { return null; }
    
    public static ArrayList<Instruction> processTree(Node root) {
        
        ArrayList<Instruction> instructions = new ArrayList<Instruction>();
        for (Node child : root.getChildren()) {
            Instruction sentence = processIndividual(child);
            
            if (sentence != null)
                instructions.add(sentence);
        }
        return instructions;
    }
    
    private static Instruction processIndividual(Node ins) { 
        Instruction sent = null;
        
        switch (ins.getNodeType()) {
            case "asignment":
                return processAsignment(ins.getChildAt(0), ins.getChildAt(1));
            case "call":
                return processCall(ins);
        }
        
        return sent;
    }
    
    public static Asignment processAsignment(Node identifier, Node expression) {
        String id = String.valueOf(identifier.getContent());
        
        if (expression.getNodeType().equals("arrow single")) {
            // arrow function, manage accordingly
            
        }
        
        Expression exp = processExpression(expression);
        return new Asignment(id, exp);
    }
    
    private static Expression processExpression(Node expression) {
        
        switch (expression.getNodeType()) {
            case "ternary expression":
                return processTernary(expression);
            case "binary expression":
                return processBinary(expression);
            case "unary expression":
                return processUnary(expression);
            case "null value":
                return new Atomic(Atomic.Type.STRING, expression.getRow(), expression.getColumn(), null);
            case "string value":
                return new Atomic(Atomic.Type.STRING, expression.getRow(), expression.getColumn(), String.valueOf(expression.getContent()));   
            case "numeric value":
                return new Atomic(Atomic.Type.NUMERIC, expression.getRow(), expression.getColumn(), Double.valueOf((double)expression.getContent()));
            case "integer value":
                return new Atomic(Atomic.Type.INTEGER, expression.getRow(), expression.getColumn(), Integer.valueOf((int)expression.getContent()));
            case "bool value":
                return new Atomic(Atomic.Type.BOOLEAN, expression.getRow(), expression.getColumn(), Boolean.valueOf((boolean)expression.getContent()));
            default:
                return new Atomic(Atomic.Type.IDENTIFIER, expression.getRow(), expression.getColumn(), String.valueOf(expression.getContent()));
        }
    }
    
    private static Expression processUnary(Node expression) {
        String operator = expression.getChildAt(0).getNodeType();
        Expression exp = processExpression(expression.getChildAt(0).getChildAt(0));
        
        int line = expression.getChildAt(0).getRow();
        int column = expression.getChildAt(0).getColumn();
        
        if (operator.equals("!"))
            return new Unary(exp, Unary.Operator.NEGATION, line, column);
        else 
            return new Unary(exp, Unary.Operator.MINUS, line, column);
    }
    
    private static Expression processBinary(Node expression) {
        String operator = expression.getChildAt(0).getNodeType();
        Expression exp1 = processExpression(expression.getChildAt(0).getChildAt(0));
        Expression exp2 = processExpression(expression.getChildAt(0).getChildAt(1));
        
        int line = expression.getChildAt(0).getRow();
        int column = expression.getChildAt(0).getColumn();
        
        Binary.Operator op = matchOperator(operator);
        return new Binary(op, exp1, exp2, line, column);
    }
    
    private static Expression processTernary(Node expression) {
        int line = expression.getChildAt(0).getRow();
        int column = expression.getChildAt(0).getColumn();
        
        Expression exp1 = processExpression(expression.getChildAt(0).getChildAt(0));
        Expression exp2 = processExpression(expression.getChildAt(1).getChildAt(0));
        Expression exp3 = processExpression(expression.getChildAt(1).getChildAt(1));
        
        return new Ternary(exp1, exp2, exp3, line, column);
    }
    
    private static Function_Call processCall(Node call) {
        String name = String.valueOf(call.getChildAt(0).getContent());
        
        if (call.getChildrenCount() == 1) {
            return new Function_Call(name);
        } else {
            ArrayList<Object> params = new ArrayList<Object>();
            for (Node child : call.getChildAt(1).getChildren()) {
                if (child.getNodeType().equals("default"))
                    params.add("default");
                else {
                    Expression exp = processExpression(child);
                    params.add(exp);
                }
            }
            return new Function_Call(name, params);
        }
    }
    
    private static Binary.Operator matchOperator(String operator) {
        switch (operator) {
            case "+":
                return Binary.Operator.PLUS;
            case "-":
                return Binary.Operator.MINUS;
            case "*":
                return Binary.Operator.TIMES;
            case "/":
                return Binary.Operator.DIV;
            case "%%":
                return Binary.Operator.MOD;
            case "^":
                return Binary.Operator.POWER;
            case "!=":
                return Binary.Operator.NOT_EQUALS;
            case "==":
                return Binary.Operator.EQUALS;
            case "<":
                return Binary.Operator.LESSER;
            case ">":
                return Binary.Operator.GREATER;
            case "<=":
                return Binary.Operator.LESSER_EQUALS;
            case ">=":
                return Binary.Operator.GREATER_EQUALS;
            case "=":
                return Binary.Operator.ASIGNMENT;
            case "&":
                return Binary.Operator.AND;
            default:
                return Binary.Operator.OR;
        }
    } 
}

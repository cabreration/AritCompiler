/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package APIServices;

import Expressions.Atomic;
import Expressions.Binary;
import Expressions.Expression;
import Expressions.StructureAccess;
import Expressions.Ternary;
import Expressions.Unary;
import Instructions.Asignment;
import Instructions.Break_Sentence;
import Instructions.Case_Component;
import Instructions.Continue_Sentence;
import Instructions.DoWhile_Sentence;
import Instructions.For_Sentence;
import Instructions.Function_Call;
import Instructions.If_Sentence;
import Instructions.Instruction;
import Instructions.StructureAsignment;
import Instructions.Switch_Sentence;
import Instructions.While_Sentence;
import Symbols.Address;
import Symbols.Function;
import Symbols.Parameter;
import aritcompiler.Singleton;
import java.util.ArrayList;

/**
 *
 * @author jacab
 */
public class TreeProcesor {
    
    public static void processFunctions(Node root) { 
        for (Node child : root.getChildAt(0).getChildren()) {
            if (child.getNodeType().equals("function")) 
                processIndividualFunction(child);
            else if (child.getNodeType().equals("arrow function"))
                processArrowFunction(child);
            else if (child.getNodeType().equals("asignment")) {
                if (child.getChildAt(1).getNodeType().equals("arrow def")) {
                    processArrowFunction(child);
                }
            }
        }
    }
    
    private static void processArrowFunction(Node arrow) {
        String id = String.valueOf(arrow.getChildAt(0).getContent());
        int line = arrow.getRow();
        int column = arrow.getColumn();
        Node def = arrow.getChildAt(1);
        Node instructions = null;
        ArrayList<Parameter> params = new ArrayList<Parameter>();
        if (def.getChildrenCount() == 1) 
            instructions = def.getChildAt(0);
        else {
            instructions = def.getChildAt(1);           
            Node parameters = def.getChildAt(0);
            for (Node parameter : parameters.getChildren()) {
                Parameter param = processParameter(parameter);
                params.add(param);
            }
        }
        ArrayList<Instruction> sentences = new ArrayList<Instruction>();
        for (Node ins : instructions.getChildren()) {
            Instruction sentence = processIndividual(ins);
            sentences.add(sentence);
        }
        
        if (def.getChildrenCount() == 1) {
            boolean flag = Singleton.insertFunction(new Function(id, sentences, line, column));
            if (!flag) 
                Singleton.insertError(new CompileError("Semantico", "Ya existe una funcion '" + id + "'", line, column));
        }
        else {
            boolean flag =Singleton.insertFunction(new Function(id, params, sentences, line, column));
            if (!flag)
                Singleton.insertError(new CompileError("Semantico", "Ya existe una funcion '" + id + "'", line, column));
        }
    }
    
    private static void processIndividualFunction(Node func) {
        String id = String.valueOf(func.getChildAt(0).getContent());
        int line = func.getRow();
        int column = func.getColumn();
        ArrayList<Instruction> sentences = new ArrayList<Instruction>();
        if (func.getChildrenCount() == 2) {
            // No tiene parametros
            for (Node sentence : func.getChildAt(1).getChildren()) {
                Instruction sent = processIndividual(sentence);
                sentences.add(sent);
            }
            boolean flag = Singleton.insertFunction(new Function(id, sentences, line, column));
            if (!flag)
                Singleton.insertError(new CompileError("Semantico", "Ya existe una funcion '" + id + "'", line, column));
        }
        else {
            // Si tiene parametros y deben ser procesados
            for (Node sentence : func.getChildAt(2).getChildren()) {
                Instruction sent = processIndividual(sentence);
                sentences.add(sent);
            }
            
            ArrayList<Parameter> params = new ArrayList<Parameter>();
            for (Node parameter : func.getChildAt(1).getChildren()) {
                Parameter param = processParameter(parameter);
                params.add(param);
            }
            boolean flag = Singleton.insertFunction(new Function(id, params, sentences, line, column));
            if (!flag)
                Singleton.insertError(new CompileError("Semantico", "Ya existe una funcion '" + id + "'", line, column));
        }
    }
    
    private static Parameter processParameter(Node param) {
        int line = param.getRow();
        int column = param.getColumn();
        if (param.getNodeType().equals("identifier")) {
            String id = String.valueOf(param.getContent());
            return new Parameter(line, column, id);
        }
        else {
            String id = String.valueOf(param.getChildAt(0).getContent());
            Expression exp = processExpression(param.getChildAt(1));
            return new Parameter(line, column, id, exp);
        }
    }
    
    public static ArrayList<Instruction> processTree(Node root) {
        
        ArrayList<Instruction> instructions = new ArrayList<Instruction>();
        for (Node child : root.getChildAt(0).getChildren()) {
            if (child.getNodeType().equals("function"))
                continue;
            
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
            case "structure asignment":
                return processStructureAsignment(ins.getChildAt(0), ins.getChildAt(1));
            case "if sentence":
                return processIfSentence(ins);
            case "switch sentence":
                return processSwitchSentence(ins);
            case "break sentence":
                return new Break_Sentence(ins.getRow(), ins.getColumn());
            case "continue sentence":
                return new Continue_Sentence(ins.getRow(), ins.getColumn());
            case "while sentence":
                return processWhileSentence(ins);
            case "do while sentence":
                return processDoWhileSentence(ins);
            case "for sentence":
                return processForSentence(ins);
        }
        
        return sent;
    }
    
    private static Asignment processAsignment(Node identifier, Node expression) {
        String id = String.valueOf(identifier.getContent());
        
        if (expression.getNodeType().equals("arrow single")) {
            return null;
        }
        
        Expression exp = processExpression(expression);
        return new Asignment(id, exp, identifier.getRow(), identifier.getColumn());
    }
    
    private static StructureAsignment processStructureAsignment(Node structure, Node expression) {
        StructureAccess access = processStructureAccess(structure);
        Expression exp = processExpression(expression);
        
        return new StructureAsignment(access.getIdentifier(), access.getLine(), access.getColumn(), access.getDirecciones(), exp);
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
            case "call":
                return processCall(expression);
            case "structure access":
                return processStructureAccess(expression);
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
        int line = call.getChildAt(0).getRow();
        int col = call.getChildAt(0).getColumn();
        
        if (call.getChildrenCount() == 1) {
            return new Function_Call(name, line, col);
        } 
        else {
            ArrayList<Object> params = new ArrayList<Object>();
            for (Node child : call.getChildAt(1).getChildren()) {
                if (child.getNodeType().equals("default"))
                    params.add("default");
                else {
                    Expression exp = processExpression(child);
                    params.add(exp);
                }
            }
            return new Function_Call(name, params, line, col);
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
    
    private static StructureAccess processStructureAccess(Node exp) { 
        String id = String.valueOf(exp.getChildAt(0).getContent());
        int line = exp.getChildAt(0).getRow();
        int column = exp.getChildAt(0).getColumn();
        int n = exp.getChildAt(1).getChildrenCount();
        Address[] adds = new Address[n];
        n = 0;
        
        for (Node child : exp.getChildAt(1).getChildren()) {
            int type = 0;
            if (String.valueOf(child.getContent()).equals("singleB"))
                type = 1;
            else
                type = 2;
            Expression address = processExpression(child.getChildAt(0));
            
            adds[n] = new Address(type, address);
            n++;
        }
        
        return new StructureAccess(id, line, column, adds); 
    }

    private static If_Sentence processIfSentence(Node ins) {
        if (ins.getChildrenCount() == 3) {
            Expression condition = processExpression(ins.getChildAt(0).getChildAt(0));
            ArrayList<Instruction> sentences = new ArrayList<Instruction>();
            for (Node sentence : ins.getChildAt(1).getChildren()) {
                Instruction sent = processIndividual(sentence);
                if (sent != null)
                    sentences.add(sent);
            }
            If_Sentence elseIf = processIfSentence(ins.getChildAt(2));
            return new If_Sentence(condition, sentences, elseIf, ins.getRow(), ins.getColumn());
        }
        else if (ins.getChildrenCount() == 2) {
            Expression condition = processExpression(ins.getChildAt(0).getChildAt(0));
            ArrayList<Instruction> sentences = new ArrayList<Instruction>();
            for (Node sentence : ins.getChildAt(1).getChildren()) {
                Instruction sent = processIndividual(sentence);
                if (sent != null)
                    sentences.add(sent);
            }
            return new If_Sentence(condition, sentences, ins.getRow(), ins.getColumn());
        }
        else {
            ArrayList<Instruction> sentences = processTree(ins);
            return new If_Sentence(sentences, ins.getRow(), ins.getColumn());
        }
    }
    
    private static Switch_Sentence processSwitchSentence(Node swit) {
        int line = swit.getRow();
        int col = swit.getColumn();
        Expression cond = processExpression(swit.getChildAt(0).getChildAt(0));
        ArrayList<Case_Component> cases = processCases(swit.getChildAt(1));
        
        return new Switch_Sentence(cond, line, col, cases);
    } 

    private static ArrayList<Case_Component> processCases(Node casesList) {
        ArrayList<Case_Component> cases = new ArrayList<Case_Component>();
        for (Node kase : casesList.getChildren()) {
            if (kase.getNodeType().equals("case")) {
                Expression exp = processExpression(kase.getChildAt(0).getChildAt(0));
                ArrayList<Instruction> sentences = new ArrayList<Instruction>();
                for (Node sent : kase.getChildAt(1).getChildren()) {
                    Instruction ins = processIndividual(sent);
                    if (ins != null)
                        sentences.add(ins);
                }
                cases.add(new Case_Component(exp, sentences, kase.getRow(), kase.getColumn()));
            }
            else { //default
                ArrayList<Instruction> sentences = new ArrayList<Instruction>();
                for (Node sent : kase.getChildAt(0).getChildren()) {
                    Instruction ins = processIndividual(sent);
                    if (ins != null)
                        sentences.add(ins);
                }
                cases.add(new Case_Component(sentences, kase.getRow(), kase.getColumn()));
            }
        }
        return cases;
    }

    private static While_Sentence processWhileSentence(Node ins) {
        int line = ins.getRow();
        int col = ins.getColumn();
        Expression cond = processExpression(ins.getChildAt(0).getChildAt(0));
        ArrayList<Instruction> sentences = new ArrayList<Instruction>();
            for (Node sentence : ins.getChildAt(1).getChildren()) {
                Instruction sent = processIndividual(sentence);
                if (sent != null)
                    sentences.add(sent);
        }
            
        return new While_Sentence(cond, sentences, line, col);  
    }

    private static DoWhile_Sentence processDoWhileSentence(Node ins) {
        int line = ins.getRow();
        int col = ins.getColumn();
        Expression cond = processExpression(ins.getChildAt(1).getChildAt(0));
        ArrayList<Instruction> sentences = new ArrayList<Instruction>();
        for (Node sentence : ins.getChildAt(0).getChildren()) {
            Instruction sent = processIndividual(sentence);
            if (sent != null)
                sentences.add(sent);
        }
            
        return new DoWhile_Sentence(cond, sentences, line, col);
    }

    private static For_Sentence processForSentence(Node ins) {
        int line = ins.getRow();
        int column = ins.getColumn();
        String id = String.valueOf(ins.getChildAt(0).getContent());
        Expression condition = processExpression(ins.getChildAt(1).getChildAt(0));
        ArrayList<Instruction> sentences = new ArrayList<Instruction>();
        for (Node sentence : ins.getChildAt(2).getChildren()) {
            Instruction sent = processIndividual(sentence);
            if (sent != null)
                sentences.add(sent);
        }
        
        return new For_Sentence(line, column, id, condition, sentences);
    }
}

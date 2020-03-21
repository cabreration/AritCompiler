/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Instructions;

import APIServices.CompileError;
import Expressions.Atomic;
import Expressions.Expression;
import Symbols.List;
import Symbols.Matrix;
import Symbols.SymbolsTable;
import Symbols.Vector;
import aritcompiler.Singleton;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.WindowConstants;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.BitmapEncoder.BitmapFormat;
import org.knowm.xchart.PieChart;
import org.knowm.xchart.PieChartBuilder;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.style.PieStyler;
import org.knowm.xchart.style.Styler;

/**
 *
 * @author jacab
 */
public class Pie implements Instruction {

    private Expression values;
    private Expression labels;
    private Expression title;
    private int line;
    private int column;

    public Pie(Expression values, Expression labels, Expression title, int line, int column) {
        this.values = values;
        this.labels = labels;
        this.title = title;
        this.line = line;
        this.column = column;
    }
    
    @Override
    public Object process(SymbolsTable env) {
        Object vals = processExpression(env, this.values);
        if (vals == null)
            return null;
        
        if (vals instanceof CompileError) {
            Singleton.insertError((CompileError)vals);
            return null;
        }
        
        if (((Vector)vals).type() != 1 && ((Vector)vals).type() != 2) {
            Singleton.insertError(new CompileError("Semantico", "El vector utilizado par crear un diagrama de pie debe contener valores numericos", this.line, this.column));
            return null;
        }
        
        Object labs = processExpression(env, this.labels);
        if (labs == null)
            return null;
        
        if (labs instanceof CompileError) {
            Singleton.insertError((CompileError)labs);
            return null;
        }
        
        if (((Vector)labs).type() != 4) {
            Singleton.insertError(new CompileError("Semantico", "El vector de las descripciones debe ser de tipo cadena en un diagrama de pie", this.line, this.column));
            return null;
        }
        
        if (((Vector)vals).getSize() > ((Vector)labs).getSize()) {
            int n = 1;
            int index = ((Vector)labs).getSize();
            for (int i = index; i < ((Vector)vals).getSize(); i++) {
                ((ArrayList<Atomic>)((Vector)labs).getValue()).add(new Atomic(Atomic.Type.STRING, "Desconocido #"));
                n++;
            }
        }
        
        Object mainT = processExpression(env, this.title);
        
        if (mainT == null) 
            return null;
        
        if (mainT instanceof CompileError) {
            Singleton.insertError((CompileError)mainT);
            return null;
        }
        
        String titler = String.valueOf(((ArrayList<Atomic>)((Vector)mainT).getValue()).get(0).getValue());
        double[] numbers = new double[((Vector)vals).getSize()];
        String[] names = new String[((Vector)vals).getSize()];
        
        for (int i = 0; i < ((Vector)vals).getSize(); i++) {
            numbers[i] = ((Number)((ArrayList<Atomic>)((Vector)vals).getValue()).get(i).getValue()).doubleValue();
            names[i] = String.valueOf(((ArrayList<Atomic>)((Vector)labs).getValue()).get(i).getValue());
        }
        
        int desc = 1;
        for (int i = 0; i < names.length; i++) {
            if (names[i] == null)
                names[i] = "Desconocido #" + desc;
            else if (names[i].equals("null"))
                names[i] = "Desconocido #" + desc;
            else if (names[i].equals("Desconocido #"))
                names[i] += desc;
            else 
                continue;
            desc++;
        }
        generatePie(titler, numbers, names);
        return null;
    }
    
    private Object processExpression(SymbolsTable env, Expression exp) {
        Object res = exp.process(env);
        
        if (res == null)
            return null;
        
        if (res instanceof CompileError) {
            if (((CompileError)res).getRow() == 0 && ((CompileError)res).getColumn() == 0) {
                ((CompileError)res).setRow(this.line);
                ((CompileError)res).setColumn(this.column);
            }
            return res;
        }
        
        if (res instanceof Atomic) {
            if (((Atomic)res).getType() == Atomic.Type.IDENTIFIER) {
                String id = String.valueOf(((Atomic)res).getValue());
                int line = ((Atomic)res).getLine();
                int col = ((Atomic)res).getColumn();
                
                res = env.getSymbol(id);
                
                if (res == null) {
                    return new CompileError("Semantico", "La variable '" + id + "' no existe en el contexto actual", line, col);
                }
            }
        }
        
        if (res instanceof Matrix) {
            res = ((Atomic[][])((Matrix)res).getValue())[0][0];
        }
        
        while (res instanceof List) {
            res = ((ArrayList<Object>)((List)res).getValue()).get(0);
        }
        
        if (res instanceof Atomic) {
            return new Vector((Atomic)res);
        }
        
        if (res instanceof Vector)
            return res;
        
        return null;
    }

    private void generatePie(String titler, double[] numbers, String[] names) {
        PieChart chart = new PieChartBuilder().width(800).height(600).title(titler).theme(Styler.ChartTheme.GGPlot2).build();

        // Customize Chart
        chart.getStyler().setLegendVisible(true);
        chart.getStyler().setAnnotationType(PieStyler.AnnotationType.Percentage);
        chart.getStyler().setAnnotationDistance(1.15);
        chart.getStyler().setPlotContentSize(.7);
        chart.getStyler().setStartAngleInDegrees(90);
        
        for (int i = 0; i < numbers.length; i++) {
            chart.addSeries(names[i], numbers[i]);
        }
        
        JFrame pie = new SwingWrapper(chart).displayChart();
        pie.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        
        try {
            BitmapEncoder.saveBitmap(chart, "./images/pie/" + titler, BitmapFormat.PNG);
        } catch (IOException ex) {
            Logger.getLogger(Pie.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

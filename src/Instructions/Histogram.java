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
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.style.Styler;

/**
 *
 * @author jacab
 */
public class Histogram implements Instruction {

    private Expression v;
    private Expression xLabel;
    private Expression main;
    private int line;
    private int column;

    public Histogram(Expression v, Expression xLabel, Expression main, int line, int column) {
        this.v = v;
        this.xLabel = xLabel;
        this.main = main;
        this.line = line;
        this.column = column;
    }
    
    @Override
    public Object process(SymbolsTable env) {
        Object vector = processFirst(env, this.v);
        if (vector == null)
            return null;
        
        if (((Vector)vector).type() != 1 && ((Vector)vector).type() != 2) {
            Singleton.insertError(new CompileError("Semantico", "La funcion de histogramas solo trabaja con vectores de tipo numerico", this.line, this.column));
            return null;
        }
        int n = ((Vector)vector).getSize();
        double[] nums = new double[n];
        int i = 0;
        for (Atomic atom : (ArrayList<Atomic>)(((Vector)vector).getValue())) {
            nums[i] = ((Number)atom.getValue()).doubleValue();
            i++;
        }
        Arrays.sort(nums);
        
        Object x = processNext(env, this.xLabel);
        if (x == null)
            return null;
        
        String xl = String.valueOf(((Atomic)x).getValue());
        
        Object name = processNext(env, this.main);
        if (name == null)
            return null;
        
        String naame = String.valueOf(((Atomic)name).getValue());
        generateHistogram(nums, xl, naame);
        
        return null;
    }
    
    private Object processFirst(SymbolsTable env, Expression exp) {
        Object res = processExpression(env, exp);
        if (res == null)
            return null;
        
        if (res instanceof CompileError) {
            Singleton.insertError((CompileError)res);
            return null;
        }
        
        if (res instanceof Matrix || res instanceof List || res instanceof Atomic) {
            Singleton.insertError(new CompileError("Semantico", "El parametro V puede unicamente ser un vector", this.line, this.column));
            return null;
        }
        
        return res;
    }
    
    private Object processNext(SymbolsTable env, Expression exp) {
        Object res = processExpression(env, exp);
        if (res == null)
            return null;
        
        if (res instanceof CompileError) {
            Singleton.insertError((CompileError)res);
            return null;
        }
        
        if (res instanceof Matrix) {
            res = ((Atomic[][])((Matrix)res).getValue())[0][0];
        }
        
        while (res instanceof List) {
            res = ((ArrayList<Object>)((List)res).getValue()).get(0);
        }
        
        if (res instanceof Vector) {
            res = ((ArrayList<Atomic>)((Vector)res).getValue()).get(0);
        }
        
        return res;
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
                
                res = env.getSymbol(id, line);
                
                if (res == null) {
                    return new CompileError("Semantico", "La variable '" + id + "' no existe en el contexto actual", line, col);
                }
            }
        }
        
        if (res instanceof Matrix) {
            return res;
        }
        
        if (res instanceof List) {
            return res;
        }
        
        if (res instanceof Atomic) {
            return res;
        }
        
        if (res instanceof Vector)
            return res;
        
        return null;
    }

    private void generateHistogram(double[] nums, String xl, String naame) {
        CategoryChart chart = new CategoryChartBuilder().width(1300).height(600).title(naame).xAxisTitle(xl).theme(Styler.ChartTheme.GGPlot2).build();

        // Customize Chart
        chart.getStyler().setLegendVisible(true);
        chart.getStyler().setLegendPosition(Styler.LegendPosition.OutsideE);
        chart.getStyler().setAxisTitlesVisible(true);
        chart.getStyler().setYAxisTitleVisible(true);
        chart.getStyler().setAxisTitlePadding(20);
        chart.getStyler().setHasAnnotations(true);
        
        double distance = nums[nums.length - 1] - nums[0];
        int range = (int)(distance/10);
        Integer[] count = new Integer[10];
        String[] labels = new String[10];
        for (int i = 0; i < 10; i++) {
            count[i] = 0;
        }
        count[0] = 1;
        
        // Contamos
        for (int i = 0; i < nums.length; i++) {
            for (int j = 1; j < 11; j++) {
                if (nums[i] > nums[0] + range*(j-1) && nums[i] <= nums[0] + range*(j)) {
                    count[j-1]++;
                    break;
                }
            }
        }
        
        //Creating the labels
        for (int i = 1; i < 11; i++) {
            labels[i-1] = "( " + String.valueOf(nums[0] + range*(i-1)) + " - " + String.valueOf(nums[0] + range*i) + "]";
        }
        
        Double[] ceros = new Double[10];
        for (int i = 0; i < ceros.length; i++) {
            ceros[i] = Double.valueOf(0);
        }
        chart.addSeries(".", Arrays.asList(labels), Arrays.asList(count), Arrays.asList(ceros));
        
        JFrame hist = new SwingWrapper(chart).displayChart();
        hist.setVisible(false);
        Singleton.insertFigure(hist);
        //pie.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        
        try {
            BitmapEncoder.saveBitmap(chart, "./images/histogram/" + naame, BitmapEncoder.BitmapFormat.PNG);
        } catch (IOException ex) {
            Logger.getLogger(Pie.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}

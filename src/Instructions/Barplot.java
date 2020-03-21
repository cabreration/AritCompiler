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
import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.WindowConstants;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;
import org.knowm.xchart.PieChart;
import org.knowm.xchart.PieChartBuilder;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.style.PieStyler;
import org.knowm.xchart.style.Styler;

/**
 *
 * @author jacab
 */
public class Barplot implements Instruction {
    
    private Expression h;
    private Expression xLabel;
    private Expression yLabel;
    private Expression title;
    private Expression names;
    private int line;
    private int column;

    public Barplot(Expression h, Expression xLabel, Expression yLabel, Expression title, Expression names, int line, int column) {
        this.h = h;
        this.xLabel = xLabel;
        this.yLabel = yLabel;
        this.title = title;
        this.names = names;
        this.line = line;
        this.column = column;
    }
    
    @Override
    public Object process(SymbolsTable env) {
        Object vec = processExpression(env, this.h);
        if (vec == null)
            return null;
        if (vec instanceof CompileError) {
            Singleton.insertError((CompileError)vec);
            return null;
        }
        
        if (((Vector)vec).type() != 1 && ((Vector)vec).type() != 2) {
            Singleton.insertError(new CompileError("Semantico", "No pueden crearse graficos con estructuras que no sean de tipo numerico", this.line, this.column));
            return null;
        }
        
        Object nameList = processExpression(env, this.names);
        if (nameList == null)
            return null;
        if (nameList instanceof CompileError) {
            Singleton.insertError((CompileError)nameList);
            return null;
        }
        
        if (((Vector)vec).getSize() > ((Vector)nameList).getSize()) {
            Singleton.insertError(new CompileError("Semantico", "No hay suficientes etiquetas para los valores de la grafica", this.line, this.column));
            int index = ((Vector)nameList).getSize();
            for (int i = index; i < ((Vector)vec).getSize(); i++) {
                ((ArrayList<Atomic>)((Vector)nameList).getValue()).add(new Atomic(Atomic.Type.STRING, "Desc #"));
            }
        }
        
        String xLab = "X axis";
        Object x = processExpression(env, this.xLabel);
        if (x == null)
            return null;
        if (x instanceof CompileError) {
            Singleton.insertError((CompileError)x);
        }
        else 
            xLab = String.valueOf(((ArrayList<Atomic>)((Vector)x).getValue()).get(0).getValue());
        
        String yLab = "Y axis";
        Object y = processExpression(env, this.yLabel);
        if (y == null)
            return null;
        if (y instanceof CompileError) {
            Singleton.insertError((CompileError)y);
        }
        else 
            yLab = String.valueOf(((ArrayList<Atomic>)((Vector)y).getValue()).get(0).getValue());
        
        String titler = "Barplot";
        Object title = processExpression(env, this.title);
        if (title == null)
            return null;
        if (title instanceof CompileError)
            Singleton.insertError((CompileError)title);
        else
            titler = String.valueOf(((ArrayList<Atomic>)((Vector)title).getValue()).get(0).getValue());
        
        Double[] numbers = new Double[((Vector)vec).getSize()];
        String[] names = new String[((Vector)vec).getSize()];
        
         for (int i = 0; i < ((Vector)vec).getSize(); i++) {
            numbers[i] = Double.valueOf(((Number)((ArrayList<Atomic>)((Vector)vec).getValue()).get(i).getValue()).doubleValue());
            names[i] = String.valueOf(((ArrayList<Atomic>)((Vector)nameList).getValue()).get(i).getValue());
        }
        
        int desc = 1;
        for (int i = 0; i < names.length; i++) {
            if (names[i] == null)
                names[i] = "Desc #" + desc;
            else if (names[i].equals("null"))
                names[i] = "Desc #" + desc;
            else if (names[i].equals("Desc #"))
                names[i] += desc;
            else 
                continue;
            desc++;
        }
        generateBarplot(titler, xLab, yLab, numbers, names);
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
            ArrayList<Atomic> nu = new ArrayList<Atomic>();
            Atomic[][] atoms = ((Atomic[][])((Matrix)res).getValue());
            int line = 0;
            int col = 0;
            for (int i = 0; i < ((Matrix)res).getRows() * ((Matrix)res).getColumns(); i++) {
                nu.add(atoms[line][col]);
                line++;
                if (line == ((Matrix)res).getRows()) {
                    line = 0;
                    col++;
                }
            }
            return new Vector(nu, ((Matrix)res).getType());
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

    private void generateBarplot(String titler, String x, String y, Double[] numbers, String[] names) {
        CategoryChart chart = new CategoryChartBuilder().width(1300).height(600).title(titler).xAxisTitle(x).yAxisTitle(y).theme(Styler.ChartTheme.GGPlot2).build();

        // Customize Chart
        chart.getStyler().setLegendVisible(true);
        chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideN);
        chart.getStyler().setAxisTitlesVisible(true);
        chart.getStyler().setYAxisTitleVisible(true);
        chart.getStyler().setAxisTitlePadding(20);
        chart.getStyler().setHasAnnotations(true);
        
        Double[] ceros = new Double[numbers.length];
        for (int i = 0; i < ceros.length; i++) {
            ceros[i] = Double.valueOf(0);
        }
        chart.addSeries("barplot", Arrays.asList(names), Arrays.asList(numbers), Arrays.asList(ceros));
        
        JFrame pie = new SwingWrapper(chart).displayChart();
        pie.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        
        try {
            BitmapEncoder.saveBitmap(chart, "./images/barplot/" + titler, BitmapEncoder.BitmapFormat.PNG);
        } catch (IOException ex) {
            Logger.getLogger(Pie.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

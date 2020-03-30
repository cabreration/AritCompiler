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
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.Styler;
import org.knowm.xchart.style.colors.ChartColor;
import org.knowm.xchart.style.lines.SeriesLines;
import org.knowm.xchart.style.markers.SeriesMarkers;

/**
 *
 * @author jacab
 */
public class Plot implements Instruction {
    
    private Expression v;
    private Expression type;
    private Expression xLab;
    private Expression yLab;
    private Expression main;
    private int line;
    private int column;

    public Plot(Expression v, Expression type, Expression xLab, Expression yLab, Expression main, int line, int column) {
        this.v = v;
        this.type = type;
        this.xLab = xLab;
        this.yLab = yLab;
        this.main = main;
        this.line = line;
        this.column = column;
    }

    @Override
    public Object process(SymbolsTable env) {
        Object v = processExpression(env, this.v);
        if (v == null)
            return null;
        
        if (((Vector)v).type() != 1 && ((Vector)v).type() != 2) {
            Singleton.insertError(new CompileError("Semantico", "Los diagramas de dispersion solo pueden crearse con datos numericos", this.line, this.column));
            return null;
        }

        Double[] nums = new Double[((Vector)v).getSize()];
        for (int i = 0; i < nums.length; i++) {
            nums[i] = ((Number)(((Atomic)(((ArrayList<Atomic>)((Vector)v).getValue()).get(i))).getValue())).doubleValue();
        }
        
        Object x = processExpression(env, this.xLab);
        if (x == null)
            return null;
        String xl = String.valueOf((((ArrayList<Atomic>)((Vector)x).getValue()).get(0)).getValue());
        
        Object y = processExpression(env, this.yLab);
        if (y == null)
            return null;
        String yl = String.valueOf((((ArrayList<Atomic>)((Vector)y).getValue()).get(0)).getValue());
        
        Object name = processExpression(env, this.main);
        if (name == null)
            return null;
        String naame = String.valueOf((((ArrayList<Atomic>)((Vector)name).getValue()).get(0)).getValue());
        
        Object typ = processExpression(env, this.type);
        if (type == null)
            return null;
        String type = String.valueOf((((ArrayList<Atomic>)((Vector)typ).getValue()).get(0)).getValue());
        if (!type.equalsIgnoreCase("P") && !type.equalsIgnoreCase("l") && !type.equalsIgnoreCase("O")) {
            Singleton.insertError(new CompileError("Semantico", "El parametro tipo de la funcion plot solo puede tomar como valor p, l u o", this.line, this.column));
            return null;
        }
        
        generatePlot(nums, xl, yl, naame, type);
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
            Singleton.insertError((CompileError)res);
            return null;
        }
        
        if (res instanceof Atomic) {
            if (((Atomic)res).getType() == Atomic.Type.IDENTIFIER) {
                String id = String.valueOf(((Atomic)res).getValue());
                int line = ((Atomic)res).getLine();
                int col = ((Atomic)res).getColumn();
                
                res = env.getSymbol(id, line);
                
                if (res == null) {
                    Singleton.insertError(new CompileError("Semantico", "La variable '" + id + "' no existe en el contexto actual", line, col));
                    return null;
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

    private void generatePlot(Double[] nums, String xl, String yl, String naame, String type) {
        XYChart chart = new XYChartBuilder().width(1300).height(600).title(naame).xAxisTitle(xl).yAxisTitle(yl).theme(Styler.ChartTheme.GGPlot2).build();

        // Customize Chart
        chart.getStyler().setPlotBackgroundColor(ChartColor.getAWTColor(ChartColor.WHITE));
        chart.getStyler().setLegendVisible(false);
        chart.getStyler().setXAxisLabelRotation(90);
        chart.getStyler().setAxisTitlesVisible(true);
        chart.getStyler().setYAxisTitleVisible(true);
        chart.getStyler().setAxisTitlePadding(20);
        chart.getStyler().setHasAnnotations(true);
        
        Integer[] xx = new Integer[nums.length];
        for (int i = 0; i < nums.length; i++) {
            xx[i] = i+1;
        }
        XYSeries series = chart.addSeries(".", Arrays.asList(xx), Arrays.asList(nums));
        if (type.equalsIgnoreCase("p")) {
            series.setLineColor(Color.white);
            series.setLineStyle(SeriesLines.NONE);
        }
        else if (type.equalsIgnoreCase("o")) {
            series.setLineColor(Color.black);
            series.setMarkerColor(Color.blue);
            series.setLineStyle(SeriesLines.SOLID);
        }
        else {
            series.setMarker(SeriesMarkers.NONE);
            series.setLineStyle(SeriesLines.SOLID);
            series.setLineColor(Color.blue);
        }
        
        
        SwingWrapper plot = new SwingWrapper(chart);
        Singleton.insertFigure(plot);
        //pie.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        
        try {
            int v = Singleton.graphCount();
            BitmapEncoder.saveBitmap(chart, "./images/plot/" + naame + String.valueOf(v), BitmapEncoder.BitmapFormat.PNG);
            String address = "./images/plot/" + naame + String.valueOf(v) + ".png";
            Singleton.insertGraph(address);
        } catch (IOException ex) {
            Logger.getLogger(Pie.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}

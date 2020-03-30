/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aritcompiler;

import APIServices.CompileError;
import APIServices.Node;
import APIServices.TreePrinter;
import APIServices.TreeProcesor;
import Instructions.Function_Call;
import Instructions.Instruction;
import JFlexNCup.Parser;
import JFlexNCup.Scanner;
import JavaCC.Grammar;
import JavaCC.ParseException;
import JavaCC.TokenMgrError;
import Symbols.SymbolsTable;
import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java_cup.runtime.Symbol;
import javax.swing.JFileChooser;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JViewport;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.BadLocationException;

/**
 *
 * @author jacab
 */
public class TextEditor extends javax.swing.JFrame {

    /**
     * Creates new form TextEditor
     */
    ArrayList<String> paths;
    int line;
    int col;
    
    public TextEditor() {
        initComponents();
        paths = new ArrayList<String>();
        line = 1;
        col = 1;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tabs = new javax.swing.JTabbedPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        console = new javax.swing.JTextArea();
        cursorInfo = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenuItem6 = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        jMenuItem7 = new javax.swing.JMenuItem();
        jMenuItem8 = new javax.swing.JMenuItem();
        jMenu4 = new javax.swing.JMenu();
        jMenuItem9 = new javax.swing.JMenuItem();
        jMenu5 = new javax.swing.JMenu();
        jMenuItem10 = new javax.swing.JMenuItem();
        jMenuItem11 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        console.setEditable(false);
        console.setColumns(20);
        console.setRows(5);
        jScrollPane1.setViewportView(console);

        cursorInfo.setText("Línea: - Columna: ");

        jMenu1.setText("Archivo");

        jMenuItem1.setText("Abrir");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuItem2.setText("Guardar Como");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem2);

        jMenuItem3.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem3.setText("Guardar");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem3);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Reportes");

        jMenuItem4.setText("Reporte de Errores");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem4);

        jMenuItem5.setText("Reporte de TS");
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem5);

        jMenuItem6.setText("Reporte AST");
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem6ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem6);

        jMenuBar1.add(jMenu2);

        jMenu3.setText("Ejecución");

        jMenuItem7.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_J, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem7.setText("Ejecución javacc");
        jMenuItem7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem7ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem7);

        jMenuItem8.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem8.setText("Ejecución jflex & cup");
        jMenuItem8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem8ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem8);

        jMenuBar1.add(jMenu3);

        jMenu4.setText("Gráficas");

        jMenuItem9.setText("Mostrar Todas");
        jMenuItem9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem9ActionPerformed(evt);
            }
        });
        jMenu4.add(jMenuItem9);

        jMenuBar1.add(jMenu4);

        jMenu5.setText("Pestañas");

        jMenuItem10.setText("Nueva Pestaña");
        jMenuItem10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem10ActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItem10);

        jMenuItem11.setText("Cerrar Pestaña");
        jMenuItem11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem11ActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItem11);

        jMenuBar1.add(jMenu5);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabs)
            .addComponent(jScrollPane1)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(468, Short.MAX_VALUE)
                .addComponent(cursorInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(431, 431, 431))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(tabs, javax.swing.GroupLayout.PREFERRED_SIZE, 346, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 16, Short.MAX_VALUE)
                .addComponent(cursorInfo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 218, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        // Abrir archivo
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String path = selectedFile.getAbsolutePath();
            String content = readFile(path);
            if (content == null)
                return;
            
            // open a new tab
            paths.add(path);
            tabs.addTab(selectedFile.getName(), createTab(content));
            cursorInfo.setText("Linea: 0 - Columna: 0");
            tabs.setSelectedIndex(tabs.getTabCount()-1);
        }
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private String readFile(String path) {
        BufferedReader bufferedReader;
        try {
            bufferedReader = new BufferedReader(new FileReader(path));
            String parcial;
            StringBuilder complete = new StringBuilder();
            parcial = bufferedReader.readLine();
            while (parcial != null) 
            {
                complete.append(parcial + "\n");
                parcial = bufferedReader.readLine();
            }
            
            bufferedReader.close();
            return complete.toString();
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
    
    private JScrollPane createTab(String text) {
        JTextArea tab = new JTextArea(text);
        tab.addCaretListener(new CaretListener() {
            public void caretUpdate(CaretEvent e) {
                JTextArea editArea = (JTextArea)e.getSource();
                int caret = editArea.getCaretPosition();
                try {
                    line = editArea.getLineOfOffset(caret);
                    col = caret - editArea.getLineStartOffset(line);
                    cursorInfo.setText("Linea: " + line + " - Columna: " + col);
                } catch (BadLocationException ex) {
                    Logger.getLogger(TextEditor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        JScrollPane scroll = new JScrollPane(tab);
        return scroll;
    }
    
    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        // Guardar como
        saveAs();
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void saveAs() {
        int currentIndex = tabs.getSelectedIndex();
        JScrollPane scroll = (JScrollPane)tabs.getComponentAt(currentIndex);
        JTextArea area = (JTextArea)(scroll.getViewport().getView());
        String text = area.getText();
        
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                FileWriter writer = new FileWriter(fileChooser.getSelectedFile());
                writer.write(text);
                writer.close();
                paths.set(currentIndex, fileChooser.getSelectedFile().getAbsolutePath());
                tabs.setTitleAt(currentIndex, fileChooser.getSelectedFile().getName());
            }
            catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
    }
    
    private void save(String path, String content) {
        File file = new File(path);
        FileWriter writer = null;
        try {
            writer = new FileWriter(file);
            writer.write(content);
            writer.close();
        }
        catch(Exception e) {
            System.err.println(e.getMessage());
        }
    }
    
    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        // Guardar
        try {
            int currentIndex = tabs.getSelectedIndex();
            if (paths.get(currentIndex).equals("@#$%")) {
                saveAs();
            }
            else {
                String path = paths.get(currentIndex);
                JScrollPane scroll = (JScrollPane)tabs.getComponentAt(currentIndex);
                JTextArea area = (JTextArea)(scroll.getViewport().getView());
                String text = area.getText();
                save(path, text);
            }
        }
        catch(Exception e) {
            System.err.println(e.getMessage());
        }
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void jMenuItem10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem10ActionPerformed
        // Nueva Pesta;a
        tabs.add("nuevo", createTab(""));
        cursorInfo.setText("Linea: 0 - Columna: 0");
        tabs.setSelectedIndex(tabs.getTabCount()-1);
        paths.add("@#$%");
    }//GEN-LAST:event_jMenuItem10ActionPerformed

    private void jMenuItem11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem11ActionPerformed
        // Cerrar Pesta;a actual
        int currentIndex = tabs.getSelectedIndex();
        paths.remove(currentIndex);
        tabs.remove(currentIndex);
    }//GEN-LAST:event_jMenuItem11ActionPerformed

    private void jMenuItem9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem9ActionPerformed
        // Mostrar todas las graficas
        Singleton.showFigures();
    }//GEN-LAST:event_jMenuItem9ActionPerformed

    private void jMenuItem8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem8ActionPerformed
        // Ejecucion jflex & cup
        if (tabs.getTabCount() == 0)
            return;
        Singleton.newCompilation();
        int currentIndex = tabs.getSelectedIndex();
        JScrollPane scroll = (JScrollPane)tabs.getComponentAt(currentIndex);
        JTextArea area = (JTextArea)(scroll.getViewport().getView());
        String code = area.getText();
        
        Reader reader = new StringReader(code); 
        Scanner scanner = new Scanner(reader);
        Parser parser = new Parser(scanner);
        Symbol parse_tree = null;
            
        try {
            //parse_tree = parser.debug_parse();
            parse_tree = parser.parse();
            Node root = parser.root;
            if (root != null)
                TreePrinter.printTree(root, "cupTree");
            execute(root);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }//GEN-LAST:event_jMenuItem8ActionPerformed

    private void execute(Node root) {
        if (root != null) {
            TreeProcesor.processFunctions(root);
            ArrayList<Instruction> sentences = TreeProcesor.processTree(root);
            SymbolsTable env = new SymbolsTable("global");
            for (Instruction ins : sentences) {
                if (ins != null) {
                    if (ins instanceof Function_Call)
                        ins.process(env);
                    else
                        ins.process(env);
                }
                    
            }
            String output = Singleton.print();
            this.console.setText(output);
            Singleton.reportErrors();
            Singleton.reportSymbols();
        }
    }
    
    private void jMenuItem7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem7ActionPerformed
        // Ejecucion javacc
        if (tabs.getTabCount() == 0)
            return;
        Singleton.newCompilation();
        int currentIndex = tabs.getSelectedIndex();
        JScrollPane scroll = (JScrollPane)tabs.getComponentAt(currentIndex);
        JTextArea area = (JTextArea)(scroll.getViewport().getView());
        String code = area.getText();
        
        try {
            Reader reader = new StringReader(code);
            Grammar parser = new Grammar(reader);
            Node root = parser.Root();
            if (root == null) {
                System.err.println("Raiz nula");
                return;
            }
            TreePrinter.printTree(root, "javaccTree");
            execute(root);
        }
        catch (Exception e) {
            if (e instanceof ParseException) {
                int line = (((ParseException) e).currentToken).next.beginLine - 1;
                int column = (((ParseException) e).currentToken).next.beginColumn -1;
                String type = "Sintactico";
                String tok = (((ParseException) e).currentToken).next.image;
                String message= "No se esperaba el caracter '" + tok + "'";
                Singleton.insertError(new CompileError(type, message, line, column));
                Singleton.reportErrors();
            }
            System.err.println(e.getMessage());
        }    
        catch (Error r) {
            if (r instanceof TokenMgrError) {
                String one = ((TokenMgrError)r).getMessage();
                Singleton.insertError(new CompileError("Lexico", one, 0, 0));
                Singleton.reportErrors();
            }
        }
    }//GEN-LAST:event_jMenuItem7ActionPerformed

    private void openTree(String type) throws IOException {
        String path = "./reports/tree/" + type + ".png";
        File file = new File(path);
        Desktop dt = Desktop.getDesktop();
        dt.open(file);
    }
    
    private void jMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem6ActionPerformed
        // TODO add your handling code here:
        try {
            openTree("cupTree");
            openTree("javaccTree");
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_jMenuItem6ActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        // Reporte de errores
        try {
            String path = "./reports/errors/errors.html";
            File file = new File(path);
            Desktop dt = Desktop.getDesktop();
            dt.open(file);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
        // Reporte de Simbolos
        try {
            String path = "./reports/table/symbols.html";
            File file = new File(path);
            Desktop dt = Desktop.getDesktop();
            dt.open(file);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_jMenuItem5ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(TextEditor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TextEditor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TextEditor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TextEditor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TextEditor().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea console;
    private javax.swing.JLabel cursorInfo;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenu jMenu5;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem10;
    private javax.swing.JMenuItem jMenuItem11;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItem7;
    private javax.swing.JMenuItem jMenuItem8;
    private javax.swing.JMenuItem jMenuItem9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane tabs;
    // End of variables declaration//GEN-END:variables
}

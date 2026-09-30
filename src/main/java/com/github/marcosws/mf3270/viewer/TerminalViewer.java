package com.github.marcosws.mf3270.viewer;

import javax.swing.*;
import java.awt.*;

public class TerminalViewer {

    private JFrame frame;
    private JTextArea area;

    private String ultimaTela = "";

    public TerminalViewer() {
        frame = new JFrame("Terminal 3270 Viewer");

        area = new JTextArea(28, 84);
        area.setFont(new Font("Monospaced", Font.PLAIN, 14));
        area.setEditable(false);
        area.setFocusable(false);
        area.setBackground(Color.BLACK);
        area.setForeground(Color.GREEN);
        // simular o terminal real, mas o JScrollPane já cuida do tamanho
        //area.setRows(24);
        //area.setColumns(80);
        //area.setLineWrap(false);

        JScrollPane scroll = new JScrollPane(area);

        frame.add(scroll);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
    }

    public void show() {
        SwingUtilities.invokeLater(() -> frame.setVisible(true));
    }

    // método principal pra atualizar tela
    public void updateScreen(String tela) {
        if (tela == null) return;

        // evita atualizar sem mudança
        if (tela.equals(ultimaTela)) return;

        ultimaTela = tela;

        SwingUtilities.invokeLater(() -> {
            area.setText(tela);
            area.setCaretPosition(0); // mantém topo visível
        });
    }
    
    public void close() {
        SwingUtilities.invokeLater(() -> {
            if (frame != null) {
                frame.dispose();
            }
        });
    }
}

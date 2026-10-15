package com.github.marcosws.mf3270.viewer;

import javax.swing.*;
import java.awt.*;

public class TerminalViewer {

    private JFrame frame;
    private JTextArea area;
    private JButton continueButton;

    private String ultimaTela = "";

    public TerminalViewer(Runnable onContinue) {
        frame = new JFrame("Terminal 3270 Viewer");
        
        continueButton = new JButton("Continue");
        continueButton.setEnabled(false);
        continueButton.addActionListener(event -> onContinue.run());

        area = new JTextArea(28, 84);
        area.setFont(new Font("Monospaced", Font.PLAIN, 14));
        area.setEditable(false);
        area.setFocusable(false);
        area.setBackground(Color.BLACK);
        area.setForeground(Color.GREEN);

        JScrollPane scroll = new JScrollPane(area);
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(continueButton);

        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(scroll, BorderLayout.CENTER);
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
    
    public void setContinueEnabled(boolean enabled) {
    	SwingUtilities.invokeLater(() -> continueButton.setEnabled(enabled));
    }
    
    public void close() {
        SwingUtilities.invokeLater(() -> {
            if (frame != null) {
                frame.dispose();
            }
        });
    }
}

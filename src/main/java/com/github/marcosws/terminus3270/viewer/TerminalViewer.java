package com.github.marcosws.terminus3270.viewer;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

public class TerminalViewer {

    private JFrame frame;
    private JTextArea areaScreen;
    private JButton continueButton;
    private JButton copyButton;

    private String lastScreen = "";

    public TerminalViewer(Runnable onContinue) {
    	
        frame = new JFrame("Terminal 3270 Viewer");
        areaScreen = new JTextArea(28, 84);
        
        continueButton = new JButton("Continue");
        continueButton.setEnabled(false);
        continueButton.addActionListener(event -> onContinue.run());
        

        copyButton = new JButton("Copy Text");
        copyButton.setEnabled(true);
        copyButton.addActionListener(event -> {
        	
            String text = areaScreen.getText();
            StringSelection selection =new StringSelection(text);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(selection, null);
            
        });


        areaScreen.setFont(new Font("Monospaced", Font.PLAIN, 14));
        areaScreen.setEditable(false);
        areaScreen.setFocusable(false);
        areaScreen.setBackground(Color.BLACK);
        areaScreen.setForeground(Color.GREEN);

        JScrollPane scroll = new JScrollPane(areaScreen);
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(continueButton);
        topPanel.add(copyButton);

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

    public void updateScreen(String screen) {
    	
        if (screen == null) return;
        if (screen.equals(lastScreen)) return;  // evita atualizar sem mudança
        lastScreen = screen;

        SwingUtilities.invokeLater(() -> {
        	areaScreen.setText(screen);
        	areaScreen.setCaretPosition(0); // mantém topo visível
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

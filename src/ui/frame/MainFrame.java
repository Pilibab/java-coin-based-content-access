package ui.frame;

import javax.swing.*;
import java.awt.*;
import service.PurchaseService;
import ui.panel.StorePanel;

public class MainFrame extends JFrame {
    private PurchaseService purchaseService;

    public MainFrame(PurchaseService service) {
        this.purchaseService = service;
        
        setTitle("Manhwa Store - Manhwa.to");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Set light theme colors (Apple-style white)
        getContentPane().setBackground(new Color(248, 248, 248));
        
        // Add main content panel - NO SCROLL PANE
        StorePanel storePanel = new StorePanel(purchaseService);
        add(storePanel, BorderLayout.CENTER);
        
        // Set frame size to exactly 640x720 + window borders
        setSize(640, 720);
        setLocationRelativeTo(null);
        setResizable(false); // Prevent resizing
        
        // Make it visible
        setVisible(true);
    }
}
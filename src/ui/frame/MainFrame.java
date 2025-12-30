package ui.frame;

import domain.user.User;
import java.awt.*;
import javax.swing.*;
import service.PurchaseService;
import ui.panel.LibraryPanel;
import ui.panel.StorePanel;

public class MainFrame extends JFrame {
    private PurchaseService purchaseService;
    private domain.user.User currentUser;

    public MainFrame(PurchaseService service) {
        this.purchaseService = service;
        this.currentUser = new domain.user.User(1000);
        
        setTitle("Manhwa Store - Manhwa.to");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Set light theme colors (Apple-style white)
        getContentPane().setBackground(new Color(248, 248, 248));
        
        // Add main content panel - NO SCROLL PANE
        StorePanel storePanel = new StorePanel(purchaseService, currentUser);
        add(storePanel, BorderLayout.CENTER);
        
        // Set frame size to exactly 640x720 + window borders
        setSize(640, 720);
        setLocationRelativeTo(null);
        setResizable(false); // Prevent resizing
        
        // Make it visible
        setVisible(true);
    }

    public void showLibrary(User user){
        getContentPane().removeAll();
        LibraryPanel libraryPanel = new LibraryPanel(user);
        add(libraryPanel, BorderLayout.CENTER);

        revalidate();
        repaint();
    }

    public void showStore() {
        getContentPane().removeAll();
        StorePanel storePanel = new StorePanel(purchaseService, currentUser);
        add(storePanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }
}
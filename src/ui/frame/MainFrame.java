package ui.frame;

import javax.swing.*;
import java.awt.*;
import service.PurchaseService;
import ui.panel.ManhwaClicked;
import ui.panel.StorePanel;    // Added this back in
import domain.content.Manhwa;

public class MainFrame extends JFrame {
    private PurchaseService purchaseService;


    public MainFrame(PurchaseService service, Manhwa manhwa) {
        this.purchaseService = service;
        
        setTitle("Manhwa Store - Manhwa.to");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false);
        setSize(640, 720);
        setLocationRelativeTo(null);

        // Set light theme colors (Apple-style white)
        getContentPane().setBackground(new Color(248, 248, 248));

        // DECISION: Which panel to show? 
        // If you want the clicked Manhwa view:
        setContentPane(new ManhwaClicked(service, manhwa));
        
        // If you wanted the Store view instead, you would use:
        // setContentPane(new StorePanel(service));

        setVisible(true);
    }
}
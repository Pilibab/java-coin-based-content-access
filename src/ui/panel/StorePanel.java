package ui.panel;

import javax.swing.*;
import java.awt.*;

import service.PurchaseService;

public class StorePanel extends JPanel {

    private final PurchaseService purchaseService;

    public StorePanel(PurchaseService purchaseService) {
        this.purchaseService = purchaseService;

        initLayout();
        initComponents();
        registerListeners();
    }

    private void initLayout() {
        setLayout(new BorderLayout());
    }

    private void initComponents() {
        JLabel titleLabel = new JLabel("Manhwa Store", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));

        JButton buyButton = new JButton("Buy Chapter");

        add(titleLabel, BorderLayout.NORTH);
        add(buyButton, BorderLayout.CENTER);
    }

    private void registerListeners() {
        // listeners will go here
    }
}

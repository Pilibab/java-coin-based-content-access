package ui.frame;

import domain.user.User;
import java.awt.*;
import javax.swing.*;
import service.PurchaseService;
import ui.panel.ManhwaClicked;
import ui.panel.StorePanel;    // Added this back in
import domain.content.Manhwa;

public class MainFrame extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainPanel;

    private PurchaseService purchaseService;
    private StorePanel storePanel;

    public MainFrame(PurchaseService purchaseService) {
        this.purchaseService = purchaseService;

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        storePanel = new StorePanel(purchaseService, this);

        mainPanel.add(storePanel, "STORE");

        add(mainPanel);
        setTitle("Manhwa Store");
        setSize(640, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void showManhwaDetails(Manhwa manhwa) {
        ManhwaClicked detailsPanel =
                new ManhwaClicked(purchaseService, manhwa, this);

        mainPanel.add(detailsPanel, "DETAILS");
        cardLayout.show(mainPanel, "DETAILS");
    }

    public void showStore() {
        cardLayout.show(mainPanel, "STORE");
    }
}

package ui.frame;

// class imports 
import domain.user.User;


import java.awt.*;
import javax.swing.*;
import java.util.List;

// logics 
import service.PurchaseService;

// panel imports
import ui.panel.ManhwaClicked;
import ui.panel.StorePanel;    
import domain.content.Manhwa;
import ui.panel.LibraryPanel;


public class MainFrame extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainPanel;

    private PurchaseService purchaseService;
    private StorePanel storePanel;
    // private User user;

    public MainFrame(PurchaseService purchaseService, User user, List<Manhwa> manhwa) {
        this.purchaseService = purchaseService;

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        storePanel = new StorePanel(this, user, manhwa);

        mainPanel.add(storePanel, "STORE");

        add(mainPanel);
        setTitle("Manhwa Store");
        setSize(640, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }


    // ================== Nav methods ==============================
    public void showManhwaDetails(Manhwa manhwa, User user) {
        ManhwaClicked detailsPanel =
                new ManhwaClicked(purchaseService, manhwa, user, this);

        mainPanel.add(detailsPanel, "DETAILS");
        cardLayout.show(mainPanel, "DETAILS");
    }

    public void showUserLibrary(User user) {
        LibraryPanel libPanel = new LibraryPanel(user);

        mainPanel.add(libPanel, "LIBRARY");
        cardLayout.show(mainPanel, "LIBRARY");
    }

    public void showStore(User user) {
        storePanel.updateData(user);
        cardLayout.show(mainPanel, "STORE");
    }
}

package app;


import javax.swing.SwingUtilities;
import service.PurchaseService;
import ui.frame.MainFrame;


public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {

            // Create services
            PurchaseService purchaseService = new PurchaseService();

            // MainFrame 
            MainFrame frame = new MainFrame(purchaseService);
            frame.setVisible(true);
        });
    }
}

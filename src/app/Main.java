package app;

import java.util.List;

import javax.swing.SwingUtilities;
import service.PurchaseService;
import ui.frame.MainFrame;
import domain.content.Manhwa;


public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {

            // Create services
            PurchaseService purchaseService = new PurchaseService();

            // 1. Get the list
            List<Manhwa> list = CsvOpener.getDb();
            
            // 2. Get a specific item (e.g., the first one at index 0)
            Manhwa selectedManhwa = list.get(0);

            // MainFrame 
            MainFrame frame = new MainFrame(purchaseService, selectedManhwa);
            frame.setVisible(true);
        });
    }
}

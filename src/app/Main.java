package app;

import javax.swing.SwingUtilities;
import service.PurchaseService;
import ui.frame.MainFrame;
import java.util.List;

import domain.content.Manhwa;

// class import 
import domain.user.User;


public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {

            // Create services
            PurchaseService purchaseService = new PurchaseService();
            User user = new User("van",1000);

            List <Manhwa> manhwas = CsvOpener.getDb();

            // MainFrame 
            MainFrame frame = new MainFrame(purchaseService, user, manhwas);
            frame.setVisible(true);
        });
    }
}

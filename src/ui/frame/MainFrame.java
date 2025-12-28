package ui.frame;

import javax.swing.JFrame;
import service.PurchaseService;
import ui.panel.StorePanel;

public class MainFrame extends JFrame {

    public MainFrame(PurchaseService service) {
        setTitle("Manhwa Store");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        // Frame hosts panels
        // setContentPane(new StorePanel(service));

        pack(); // size based on content
        setLocationRelativeTo(null); // center screen
    }
}

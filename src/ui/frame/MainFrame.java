package ui.frame;

import javax.swing.JFrame;
import service.PurchaseService;
import ui.panel.ManhwaClicked; // Make sure to import your panel
import domain.content.Manhwa;   // Import the Manhwa model

public class MainFrame extends JFrame {

    public MainFrame(PurchaseService service, Manhwa manhwa) {
        setTitle("Manhwa Store");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setSize(640,720);

        // Frame hosts panels
        setContentPane(new ManhwaClicked(service, manhwa));

        // pack(); // size based on content
        setLocationRelativeTo(null); // center screen
    }
}

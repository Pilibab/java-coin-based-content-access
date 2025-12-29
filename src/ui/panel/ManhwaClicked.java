package ui.panel;

import javax.swing.*;
import java.awt.*;
import service.PurchaseService;
import domain.content.Manhwa;

public class ManhwaClicked extends JPanel {

    private final PurchaseService purchaseService;
    private final Manhwa manhwa;

    public ManhwaClicked(PurchaseService purchaseService, Manhwa manhwa) {
        this.purchaseService = purchaseService;
        this.manhwa = manhwa;

        initLayout();
        initComponents();
        registerListeners();
    }

    private void initLayout() {
        // BoxLayout.Y_AXIS stacks components vertically
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        // Optional: adds padding around the edges of the panel
        // setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    }

    private void initComponents() {
        // === TITLE ROW WITH BACK BUTTON ===
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.X_AXIS));
        titlePanel.setOpaque(false);
        titlePanel.setMaximumSize(new Dimension(640, 40));
        
        // Back button
        JButton backButton = new JButton("← Back");
        backButton.setPreferredSize(new Dimension(80, 30));
        backButton.setMaximumSize(new Dimension(80, 30));
        backButton.setBackground(new Color(220, 220, 220));
        backButton.setFocusPainted(false);
        
        // Title
        JLabel titleLabel = new JLabel(manhwa.getTitle());
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        
        // Layout: back button on left, title centered, space on right
        titlePanel.add(Box.createRigidArea(new Dimension(20, 0))); // Left padding
        titlePanel.add(backButton);
        titlePanel.add(Box.createHorizontalGlue());
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createHorizontalGlue());
        titlePanel.add(Box.createRigidArea(new Dimension(100, 0))); // Right space to balance
        
        add(titlePanel);
        add(Box.createRigidArea(new Dimension(0, 20)));


        // 2. Image
        try {
            String img_path = manhwa.getCoverImageUrl();
            java.net.URL url = new java.net.URL(img_path);
            ImageIcon imageIcon = new ImageIcon(url);
            Image originalImage = imageIcon.getImage();

            // 1. Get original dimensions
            int originWidth = originalImage.getWidth(null);
            int originHeight = originalImage.getHeight(null);

            // 2. Set your target height
            int targetHeight = 300;

            // 3. Calculate target width based on proportion
            int targetWidth = (originWidth * targetHeight) / originHeight;

            // 4. Scale using the calculated width
            Image scaledImage = originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
            
            JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
            imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            add(imageLabel);

        } catch (Exception e) {
            add(new JLabel("Image Preview Unavailable"));
        }

        add(Box.createRigidArea(new Dimension(0, 15)));

        // --- RATING & RANK INFO ---
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        infoPanel.setOpaque(false);
        
        JLabel ratingLabel = new JLabel("⭐ " + String.format("%.1f", manhwa.getRating()));
        ratingLabel.setFont(new Font("Arial", Font.BOLD, 14));
        ratingLabel.setForeground(new Color(255, 165, 0)); // Orange color
        
        JLabel rankLabel = new JLabel("Rank #" + manhwa.getRank());
        rankLabel.setFont(new Font("Arial", Font.BOLD, 14));
        rankLabel.setForeground(new Color(70, 130, 180)); // Steel blue
        
        String statusText;
        String chapters = manhwa.getChapters(); // Assuming this returns a String

        if ("unknown".equalsIgnoreCase(chapters)) {
            statusText = "Status: Ongoing";
        } else {
            statusText = "Status: Completed (" + chapters + " Chapters)";
        }

        JLabel chaptersLabel = new JLabel("📖 " + statusText);
        chaptersLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        
        infoPanel.add(ratingLabel);
        infoPanel.add(rankLabel);
        infoPanel.add(chaptersLabel);
        infoPanel.setMaximumSize(new Dimension(640, 30));
        add(infoPanel);
        
        add(Box.createRigidArea(new Dimension(0, 15)));

        // --- BUTTON PANEL (SIDE-BY-SIDE) ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setOpaque(false);
        
        JButton buyButton = new JButton("Buy");
        buyButton.setPreferredSize(new Dimension(100, 40));
        buyButton.setBackground(new Color(76, 175, 80)); // Green
        buyButton.setForeground(Color.WHITE);
        buyButton.setFocusPainted(false);
        
        JButton rentButton = new JButton("Rent");
        rentButton.setPreferredSize(new Dimension(100, 40));
        rentButton.setBackground(new Color(33, 150, 243)); // Blue
        rentButton.setForeground(Color.WHITE);
        rentButton.setFocusPainted(false);
        
        buttonPanel.add(buyButton);
        buttonPanel.add(rentButton);
        buttonPanel.setMaximumSize(new Dimension(640, 50));
        add(buttonPanel); 
        
        // Add this right after you've created the buttons in initComponents()
        buyButton.addActionListener(e -> {
            System.out.println("Buy button clicked for: " + manhwa.getTitle());
            // Call your service logic here
            // ! NULL for now 
            purchaseService.buyManhwa(null, manhwa);
        });

        rentButton.addActionListener(e -> {
            System.out.println("Rent button clicked for: " + manhwa.getTitle());
            // Call your rental logic here
            // ! NULL for now 
            purchaseService.rentManhwa(null, manhwa);
        });
        add(Box.createRigidArea(new Dimension(0, 15)));

        // 3. Synopsis
        String desc = cleanAndTruncate(manhwa.getSynopsis());
        JLabel descLabel = new JLabel("<html><body style='width: 500px'>" +
            "<b>Description:</b><br>" + desc + "</body></html>");

        JPanel descWrapper = new JPanel();
        descWrapper.setLayout(new BoxLayout(descWrapper, BoxLayout.X_AXIS));
        descWrapper.setOpaque(false);
        descWrapper.add(Box.createHorizontalGlue());
        descWrapper.add(descLabel);
        descWrapper.add(Box.createHorizontalGlue());
        descWrapper.setMaximumSize(new Dimension(600, 200)); // Control height
        add(descWrapper);

        add(Box.createRigidArea(new Dimension(0, 10)));

        // 4. Genre/Tags
        String genre = manhwa.getTags();
        JLabel genreLabel = new JLabel("<html><body style='width: 500px'>" +
            "<b>Genres:</b> " + genre + "</body></html>");
        genreLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        JPanel genreWrapper = new JPanel();
        genreWrapper.setLayout(new BoxLayout(genreWrapper, BoxLayout.X_AXIS));
        genreWrapper.setOpaque(false);
        genreWrapper.add(Box.createHorizontalGlue());
        genreWrapper.add(genreLabel);
        genreWrapper.add(Box.createHorizontalGlue());
        genreWrapper.setMaximumSize(new Dimension(600, 50));
        add(genreWrapper);

        add(Box.createRigidArea(new Dimension(0, 10)));

        // 5. Published Date
        JLabel dateLabel = new JLabel("<html><body style='width: 500px'>" +
            "<b>Published:</b> " + manhwa.getPublishedDate() + "</body></html>");
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        dateLabel.setForeground(new Color(100, 100, 100));

        JPanel dateWrapper = new JPanel();
        dateWrapper.setLayout(new BoxLayout(dateWrapper, BoxLayout.X_AXIS));
        dateWrapper.setOpaque(false);
        dateWrapper.add(Box.createHorizontalGlue());
        dateWrapper.add(dateLabel);
        dateWrapper.add(Box.createHorizontalGlue());
        dateWrapper.setMaximumSize(new Dimension(600, 30));
        add(dateWrapper);
    }

    private void registerListeners() {
        // listeners will go here
    }

    private String cleanAndTruncate(String text) {
        if (text == null) {
            return null;
        }

        // 1. Clean the string (keep alphanumeric, punctuation, and spaces)
        String cleaned = text.replaceAll("[^a-zA-Z0-9\\p{Punct}\\s]", "");

        // 2. Check length and truncate if necessary
        int limit = 200;
        if (cleaned.length() > limit) {
            return cleaned.substring(0, limit) + "...";
        }

        return cleaned;
    }
}
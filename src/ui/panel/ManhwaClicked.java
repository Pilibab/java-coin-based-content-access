package ui.panel;

import javax.swing.*;
import java.awt.*;
import service.PurchaseService;
import ui.frame.MainFrame;
import domain.content.Manhwa;


import domain.user.User;;

public class ManhwaClicked extends JPanel {

    private final PurchaseService purchaseService;
    private final Manhwa manhwa;
    private MainFrame frame;
    private User user;

    public ManhwaClicked(PurchaseService purchaseService, 
                            Manhwa manhwa, 
                            User user,
                            MainFrame frame) 
    {
        this.purchaseService = purchaseService;
        this.manhwa = manhwa;
        this.frame = frame;
        this.user = user;

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
        // Each method adds its components directly to 'this' panel
        addTitleRow();
        add(Box.createRigidArea(new Dimension(0, 20)));

        addCoverImage();
        add(Box.createRigidArea(new Dimension(0, 15)));

        addInfoPanel();
        add(Box.createRigidArea(new Dimension(0, 15)));

        addActionButtonPanel();
        add(Box.createRigidArea(new Dimension(0, 15)));

        addDescriptionPanel();
        add(Box.createRigidArea(new Dimension(0, 10)));

        addGenrePanel();
        add(Box.createRigidArea(new Dimension(0, 10)));

        addFooterDatePanel();
    }

    // 1. Title and Back Button
    private void addTitleRow() {
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.X_AXIS));
        titlePanel.setOpaque(false);
        titlePanel.setMaximumSize(new Dimension(640, 40));

        JButton backButton = new JButton("← Back");
        backButton.setPreferredSize(new Dimension(80, 30));
        backButton.setMaximumSize(new Dimension(80, 30));
        backButton.addActionListener(e -> frame.showStore());

        JLabel titleLabel = new JLabel(manhwa.getTitle());
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));

        titlePanel.add(Box.createRigidArea(new Dimension(20, 0)));
        titlePanel.add(backButton);
        titlePanel.add(Box.createHorizontalGlue());
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createHorizontalGlue());
        titlePanel.add(Box.createRigidArea(new Dimension(100, 0)));

        add(titlePanel);
    }

    // 2. Scaled Cover Image
    private void addCoverImage() {
        try {
            java.net.URL url = new java.net.URL(manhwa.getCoverImageUrl());
            ImageIcon imageIcon = new ImageIcon(url);
            Image img = imageIcon.getImage();

            int targetHeight = 300;
            int targetWidth = (img.getWidth(null) * targetHeight) / img.getHeight(null);

            Image scaled = img.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
            JLabel imageLabel = new JLabel(new ImageIcon(scaled));
            imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            add(imageLabel);
        } catch (Exception e) {
            JLabel errorLabel = new JLabel("Image Preview Unavailable");
            errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            add(errorLabel);
        }
    }

    // 3. Rating, Rank, and Status
    private void addInfoPanel() {
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        infoPanel.setOpaque(false);
        infoPanel.setMaximumSize(new Dimension(640, 30));

        JLabel ratingLabel = new JLabel("⭐ " + String.format("%.1f", manhwa.getRating()));
        ratingLabel.setFont(new Font("Arial", Font.BOLD, 14));
        ratingLabel.setForeground(new Color(255, 165, 0));

        JLabel rankLabel = new JLabel("Rank #" + manhwa.getRank());
        rankLabel.setFont(new Font("Arial", Font.BOLD, 14));
        rankLabel.setForeground(new Color(70, 130, 180));

        String chapters = manhwa.getChapters();
        String statusText = "unknown".equalsIgnoreCase(chapters) ? "Ongoing" : "Completed (" + chapters + " Chapters)";
        JLabel chaptersLabel = new JLabel("📖 Status: " + statusText);

        infoPanel.add(ratingLabel);
        infoPanel.add(rankLabel);
        infoPanel.add(chaptersLabel);
        add(infoPanel);
    }

    // 4. Buy and Rent Buttons

    // 4. Buy and Rent Buttons (with conditional display)
    private void addActionButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setMaximumSize(new Dimension(640, 50));

        // Check ownership status
        boolean hasPermanent = user != null && purchaseService.hasPermanentAccess(user, manhwa);
        boolean hasAnyAccess = user != null && purchaseService.hasAnyAccess(user, manhwa);

        if (hasPermanent) {
            // User owns it permanently - show "Owned" label instead of buttons
            JLabel ownedLabel = new JLabel("✓ Owned");
            ownedLabel.setFont(new Font("Arial", Font.BOLD, 16));
            ownedLabel.setForeground(new Color(76, 175, 80));
            buttonPanel.add(ownedLabel);
        } else if (hasAnyAccess) {
            // User has rental access - show only Buy button
            JButton buyButton = createActionButton("Buy", new Color(76, 175, 80));
            buyButton.addActionListener(e -> {
                if (user != null) purchaseService.buyManhwa(user, manhwa);
            });
            buttonPanel.add(buyButton);
        } else {
            // User has no access - show both buttons
            JButton buyButton = createActionButton("Buy", new Color(76, 175, 80));
            JButton rentButton = createActionButton("Rent", new Color(33, 150, 243));

            buyButton.addActionListener(e -> {
                if (user != null) purchaseService.buyManhwa(user, manhwa);
            });
            
            rentButton.addActionListener(e -> {
                if (user != null) purchaseService.rentManhwa(user, manhwa);
            });

            buttonPanel.add(buyButton);
            buttonPanel.add(rentButton);
        }

        add(buttonPanel);
    }
    // Helper for button styling
    private JButton createActionButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(100, 40));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        return btn;
    }

    // 5. Synopsis
    private void addDescriptionPanel() {
        String desc = cleanAndTruncate(manhwa.getSynopsis());
        JLabel descLabel = new JLabel("<html><body style='width: 500px'><b>Description:</b><br>" + desc + "</body></html>");
        add(createCenteredWrapper(descLabel, 200));
    }

    // 6. Genres
    private void addGenrePanel() {
        JLabel genreLabel = new JLabel("<html><body style='width: 500px'><b>Genres:</b> " + manhwa.getTags() + "</body></html>");
        genreLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        add(createCenteredWrapper(genreLabel, 50));
    }

    // 7. Published Date
    private void addFooterDatePanel() {
        JLabel dateLabel = new JLabel("<html><body style='width: 500px'><b>Published:</b> " + manhwa.getPublishedDate() + "</body></html>");
        dateLabel.setForeground(new Color(100, 100, 100));
        add(createCenteredWrapper(dateLabel, 30));
    }

    // REUSABLE WRAPPER: To reduce repetitive code for centering
    private JPanel createCenteredWrapper(Component c, int maxHeight) {
        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.X_AXIS));
        wrapper.setOpaque(false);
        wrapper.add(Box.createHorizontalGlue());
        wrapper.add(c);
        wrapper.add(Box.createHorizontalGlue());
        wrapper.setMaximumSize(new Dimension(600, maxHeight));
        return wrapper;
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
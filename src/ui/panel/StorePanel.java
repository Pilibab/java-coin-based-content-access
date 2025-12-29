package ui.panel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URI;
import java.net.URL;
import java.util.List;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import service.PurchaseService;
import domain.content.Manhwa;
import domain.user.User;
import app.CsvOpener;

public class StorePanel extends JPanel {
    private final PurchaseService purchaseService;
    private List<Manhwa> manhwaList;
    private User currentUser;
    private JLabel coinsLabel;
    private int currentTopIndex = 0;
    private JPanel topTenImagePanel;
    private JPanel gridPanel;
    private int currentGridPage = 0;
    private final int ITEMS_PER_PAGE = 4;
    
    // Cache for loaded images
    private java.util.Map<String, ImageIcon> imageCache = new java.util.HashMap<>();

    public StorePanel(PurchaseService purchaseService) {
        this.purchaseService = purchaseService;
        this.currentUser = new User(1000);
        
        setLayout(new BorderLayout());
        setBackground(new Color(248, 248, 248));
        setPreferredSize(new Dimension(640, 720)); // Set exact size
        
        CsvOpener opener = new CsvOpener();
        manhwaList = opener.getDb();
        
        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(new Color(248, 248, 248));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        // Set exact size for main panel - calculated to fit exactly 640x720
        mainPanel.setPreferredSize(new Dimension(600, 650));
        
        // Top bar with app name and navigation
        mainPanel.add(createTopBar());
        mainPanel.add(Box.createVerticalStrut(15));
        
        // Top 10 section
        mainPanel.add(createSectionHeader("Display top 10"));
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(createTopTenCarousel());
        mainPanel.add(Box.createVerticalStrut(15));
        
        // Search bar
        mainPanel.add(createSearchBar());
        mainPanel.add(Box.createVerticalStrut(15));
        
        // 2x2 Grid with images and titles
        mainPanel.add(createManhwaGrid());
        mainPanel.add(Box.createVerticalStrut(15));
        
        // Navigation buttons
        mainPanel.add(createNavigationPanel());
        
        // Set exact sizes for all components to fit within 640x720
        mainPanel.setMaximumSize(new Dimension(600, 650));
        
        // Use a JPanel wrapper instead of JScrollPane to show everything at once
        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setBackground(new Color(248, 248, 248));
        wrapperPanel.add(mainPanel, BorderLayout.NORTH);
        
        // Add empty space at the bottom if needed
        wrapperPanel.add(Box.createVerticalGlue(), BorderLayout.CENTER);
        
        add(wrapperPanel, BorderLayout.CENTER);
    }
    
    private JPanel createTopBar() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(20, 108, 148));
        panel.setMaximumSize(new Dimension(600, 50)); // Fixed size
        panel.setPreferredSize(new Dimension(600, 50)); // Fixed size
        panel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        // Left side - App name
        JLabel appName = new JLabel("Manhwa db");
        appName.setFont(new Font("Segoe UI", Font.BOLD, 20));
        appName.setForeground(Color.WHITE);
        
        // Right side - Navigation buttons and wallet
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setOpaque(false);
        
        JButton storeBtn = createTopBarButton("Store");
        JButton libraryBtn = createTopBarButton("Library");
        JButton cartBtn = createTopBarButton("Cart");
        
        coinsLabel = new JLabel("💰 " + (int)currentUser.getWallet().getCoins());
        coinsLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        coinsLabel.setForeground(Color.WHITE);
        
        storeBtn.setEnabled(false); // Current page
        
        libraryBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Navigate to Library Panel", "Library", JOptionPane.INFORMATION_MESSAGE);
        });
        
        cartBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Navigate to Cart", "Cart", JOptionPane.INFORMATION_MESSAGE);
        });
        
        rightPanel.add(storeBtn);
        rightPanel.add(libraryBtn);
        rightPanel.add(cartBtn);
        rightPanel.add(Box.createHorizontalStrut(10));
        rightPanel.add(coinsLabel);
        
        panel.add(appName, BorderLayout.WEST);
        panel.add(rightPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    private JButton createTopBarButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(30, 118, 158));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(70, 30));
        btn.setMaximumSize(new Dimension(70, 30)); // Fixed size
        
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (btn.isEnabled()) {
                    btn.setBackground(new Color(40, 128, 168));
                }
            }
            public void mouseExited(MouseEvent e) {
                if (btn.isEnabled()) {
                    btn.setBackground(new Color(30, 118, 158));
                }
            }
        });
        
        return btn;
    }
    
    private JPanel createSectionHeader(String title) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(600, 30)); // Fixed size
        panel.setPreferredSize(new Dimension(600, 30)); // Fixed size
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(33, 33, 33));
        
        panel.add(titleLabel);
        return panel;
    }
    
    private JPanel createTopTenCarousel() {
        JPanel carouselPanel = new JPanel(new BorderLayout());
        carouselPanel.setOpaque(false);
        carouselPanel.setMaximumSize(new Dimension(600, 200)); // Fixed size
        carouselPanel.setPreferredSize(new Dimension(600, 200)); // Fixed size
        
        // Image display panel
        topTenImagePanel = new JPanel(new BorderLayout());
        topTenImagePanel.setBackground(Color.WHITE);
        topTenImagePanel.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 2));
        topTenImagePanel.setPreferredSize(new Dimension(500, 180));
        topTenImagePanel.setMaximumSize(new Dimension(500, 180)); // Fixed size
        
        updateTopTenImage();
        
        // Navigation buttons
        JButton prevBtn = new JButton("<");
        JButton nextBtn = new JButton(">");
        
        styleCarouselButton(prevBtn);
        styleCarouselButton(nextBtn);
        
        prevBtn.addActionListener(e -> {
            currentTopIndex = (currentTopIndex - 1 + Math.min(10, manhwaList.size())) % Math.min(10, manhwaList.size());
            updateTopTenImage();
        });
        
        nextBtn.addActionListener(e -> {
            currentTopIndex = (currentTopIndex + 1) % Math.min(10, manhwaList.size());
            updateTopTenImage();
        });
        
        carouselPanel.add(prevBtn, BorderLayout.WEST);
        carouselPanel.add(topTenImagePanel, BorderLayout.CENTER);
        carouselPanel.add(nextBtn, BorderLayout.EAST);
        
        return carouselPanel;
    }
    
    private void styleCarouselButton(JButton btn) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 20));
        btn.setBackground(new Color(20, 108, 148));
        btn.setForeground(Color.WHITE);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(50, 180));
        btn.setMaximumSize(new Dimension(50, 180)); // Fixed size
    }
    
    private void updateTopTenImage() {
        topTenImagePanel.removeAll();
        
        if (currentTopIndex < manhwaList.size()) {
            Manhwa manhwa = manhwaList.get(currentTopIndex);
            
            JPanel contentPanel = new JPanel(new BorderLayout());
            contentPanel.setBackground(Color.WHITE);
            contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            contentPanel.setPreferredSize(new Dimension(500, 180));
            
            // Load and display the cover image
            JLabel imageLabel = new JLabel();
            imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
            
            try {
                ImageIcon coverImage = loadImage(manhwa.getCoverImageUrl());
                if (coverImage != null) {
                    // Scale the image to fit
                    Image scaledImage = coverImage.getImage().getScaledInstance(120, 160, Image.SCALE_SMOOTH);
                    imageLabel.setIcon(new ImageIcon(scaledImage));
                } else {
                    // Fallback placeholder
                    imageLabel.setText("📖");
                    imageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 48));
                }
            } catch (Exception e) {
                imageLabel.setText("📖");
                imageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 48));
            }
            
            JLabel rankLabel = new JLabel("#" + (currentTopIndex + 1), SwingConstants.CENTER);
            rankLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
            rankLabel.setForeground(new Color(20, 108, 148));
            
            JLabel titleLabel = new JLabel("<html><center>" + truncateText(manhwa.getTitle(), 30) + "</center></html>", SwingConstants.CENTER);
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
            titleLabel.setForeground(new Color(33, 33, 33));
            
            JLabel ratingLabel = new JLabel("⭐ " + manhwa.getRating() + "  |  📖 " + manhwa.getChapters(), SwingConstants.CENTER);
            ratingLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            ratingLabel.setForeground(new Color(120, 120, 120));
            
            JPanel leftPanel = new JPanel(new BorderLayout());
            leftPanel.setBackground(Color.WHITE);
            leftPanel.setPreferredSize(new Dimension(150, 150));
            leftPanel.add(imageLabel, BorderLayout.CENTER);
            
            JPanel textPanel = new JPanel();
            textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
            textPanel.setBackground(Color.WHITE);
            textPanel.setPreferredSize(new Dimension(300, 150));
            
            rankLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            ratingLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            textPanel.add(rankLabel);
            textPanel.add(Box.createVerticalStrut(8));
            textPanel.add(titleLabel);
            textPanel.add(Box.createVerticalStrut(5));
            textPanel.add(ratingLabel);
            
            contentPanel.add(leftPanel, BorderLayout.WEST);
            contentPanel.add(textPanel, BorderLayout.CENTER);
            topTenImagePanel.add(contentPanel);
        }
        
        topTenImagePanel.revalidate();
        topTenImagePanel.repaint();
    }
    
    private JPanel createSearchBar() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(600, 50)); // Fixed size
        panel.setPreferredSize(new Dimension(600, 50)); // Fixed size
        
        JTextField searchField = new JTextField("Search manhwa...");
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setForeground(new Color(150, 150, 150));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        
        searchField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (searchField.getText().equals("Search manhwa...")) {
                    searchField.setText("");
                    searchField.setForeground(new Color(33, 33, 33));
                }
            }
            public void focusLost(FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText("Search manhwa...");
                    searchField.setForeground(new Color(150, 150, 150));
                }
            }
        });
        
        panel.add(searchField, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createManhwaGrid() {
        JPanel container = new JPanel(new BorderLayout());
        container.setOpaque(false);
        container.setMaximumSize(new Dimension(600, 220)); // Reduced height to fit
        container.setPreferredSize(new Dimension(600, 220)); // Reduced height to fit
        
        gridPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        gridPanel.setOpaque(false);
        gridPanel.setMaximumSize(new Dimension(600, 220));
        gridPanel.setPreferredSize(new Dimension(600, 220));
        
        updateGrid();
        
        container.add(gridPanel, BorderLayout.CENTER);
        return container;
    }
    
    private void updateGrid() {
        gridPanel.removeAll();
        
        int startIdx = currentGridPage * ITEMS_PER_PAGE;
        int endIdx = Math.min(startIdx + ITEMS_PER_PAGE, manhwaList.size());
        
        for (int i = startIdx; i < endIdx; i++) {
            gridPanel.add(createManhwaCard(manhwaList.get(i)));
        }
        
        gridPanel.revalidate();
        gridPanel.repaint();
    }
    
    private JPanel createManhwaCard(Manhwa manhwa) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.setPreferredSize(new Dimension(140, 200)); // Fixed size
        card.setMaximumSize(new Dimension(140, 200)); // Fixed size
        
        // Image panel with cover image
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.setBackground(new Color(240, 240, 240));
        imagePanel.setPreferredSize(new Dimension(120, 160));
        imagePanel.setMaximumSize(new Dimension(120, 160));
        imagePanel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        
        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        try {
            ImageIcon coverImage = loadImage(manhwa.getCoverImageUrl());
            if (coverImage != null) {
                // Scale the image to fit
                Image scaledImage = coverImage.getImage().getScaledInstance(120, 160, Image.SCALE_SMOOTH);
                imageLabel.setIcon(new ImageIcon(scaledImage));
            } else {
                // Fallback placeholder
                imageLabel.setText("📖");
                imageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 48));
            }
        } catch (Exception e) {
            imageLabel.setText("📖");
            imageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 48));
        }
        
        imagePanel.add(imageLabel, BorderLayout.CENTER);
        imagePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Title
        JLabel titleLabel = new JLabel("<html><center>" + truncateText(manhwa.getTitle(), 40) + "</center></html>");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        titleLabel.setForeground(new Color(33, 33, 33));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        card.add(imagePanel);
        card.add(Box.createVerticalStrut(8));
        card.add(titleLabel);
        
        card.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                showManhwaDetails(manhwa);
            }
            public void mouseEntered(MouseEvent e) {
                card.setBackground(new Color(250, 250, 250));
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(20, 108, 148), 2),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)
                ));
            }
            public void mouseExited(MouseEvent e) {
                card.setBackground(Color.WHITE);
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)
                ));
            }
        });
        
        return card;
    }
    
    private JPanel createNavigationPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(600, 40)); // Fixed size
        panel.setPreferredSize(new Dimension(600, 40)); // Fixed size
        
        JButton prevBtn = new JButton("◄ Previous");
        JButton nextBtn = new JButton("Next ►");
        
        styleNavButton(prevBtn);
        styleNavButton(nextBtn);
        
        prevBtn.addActionListener(e -> {
            if (currentGridPage > 0) {
                currentGridPage--;
                updateGrid();
            }
        });
        
        nextBtn.addActionListener(e -> {
            if ((currentGridPage + 1) * ITEMS_PER_PAGE < manhwaList.size()) {
                currentGridPage++;
                updateGrid();
            }
        });
        
        panel.add(prevBtn);
        panel.add(nextBtn);
        
        return panel;
    }
    
    private void styleNavButton(JButton btn) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(new Color(20, 108, 148));
        btn.setForeground(Color.WHITE);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(100, 35));
    }
    
    private ImageIcon loadImage(String imageUrl) {
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            return null;
        }
        
        // Check cache first
        if (imageCache.containsKey(imageUrl)) {
            return imageCache.get(imageUrl);
        }
        
        try {
            // Use the non-deprecated way to create URL
            URI uri = new URI(imageUrl);
            URL url = uri.toURL();
            BufferedImage img = ImageIO.read(url);
            if (img != null) {
                ImageIcon icon = new ImageIcon(img);
                imageCache.put(imageUrl, icon);
                return icon;
            }
        } catch (Exception e) {
            System.err.println("Failed to load image from URL: " + imageUrl);
        }
        
        return null;
    }
    
    private void showManhwaDetails(Manhwa manhwa) {
        JDialog dialog = new JDialog((Frame)SwingUtilities.getWindowAncestor(this), manhwa.getTitle(), true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(600, 450);
        dialog.setLocationRelativeTo(this);
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        // Add cover image to details
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.setBackground(new Color(240, 240, 240));
        imagePanel.setPreferredSize(new Dimension(150, 200));
        imagePanel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        
        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        try {
            ImageIcon coverImage = loadImage(manhwa.getCoverImageUrl());
            if (coverImage != null) {
                Image scaledImage = coverImage.getImage().getScaledInstance(150, 200, Image.SCALE_SMOOTH);
                imageLabel.setIcon(new ImageIcon(scaledImage));
            } else {
                imageLabel.setText("📖");
                imageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 48));
            }
        } catch (Exception e) {
            imageLabel.setText("📖");
            imageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 48));
        }
        
        imagePanel.add(imageLabel, BorderLayout.CENTER);
        
        JLabel titleLabel = new JLabel(manhwa.getTitle());
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel infoLabel = new JLabel("Rank #" + manhwa.getRank() + "  |  ⭐ " + manhwa.getRating() + "  |  📖 " + manhwa.getChapters());
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        infoLabel.setForeground(new Color(120, 120, 120));
        infoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JTextArea synopsisArea = new JTextArea(manhwa.getSynopsis());
        synopsisArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        synopsisArea.setLineWrap(true);
        synopsisArea.setWrapStyleWord(true);
        synopsisArea.setEditable(false);
        synopsisArea.setBackground(new Color(250, 250, 250));
        synopsisArea.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        
        JScrollPane scrollPane = new JScrollPane(synopsisArea);
        scrollPane.setPreferredSize(new Dimension(550, 180));
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        double price = purchaseService.calculateCoinValue(manhwa);
        JLabel priceLabel = new JLabel("💰 " + (int)price + " coins");
        priceLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        priceLabel.setForeground(new Color(255, 152, 0));
        priceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JButton buyBtn = new JButton("Buy");
        JButton rentBtn = new JButton("Rent");
        
        buyBtn.setBackground(new Color(76, 175, 80));
        buyBtn.setForeground(Color.WHITE);
        buyBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        buyBtn.setBorderPainted(false);
        buyBtn.setFocusPainted(false);
        buyBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        buyBtn.setPreferredSize(new Dimension(80, 32));
        
        rentBtn.setBackground(new Color(33, 150, 243));
        rentBtn.setForeground(Color.WHITE);
        rentBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        rentBtn.setBorderPainted(false);
        rentBtn.setFocusPainted(false);
        rentBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        rentBtn.setPreferredSize(new Dimension(80, 32));
        
        buyBtn.addActionListener(e -> {
            handlePurchase(manhwa, true);
            dialog.dispose();
        });
        
        rentBtn.addActionListener(e -> {
            handlePurchase(manhwa, false);
            dialog.dispose();
        });
        
        buttonPanel.add(buyBtn);
        buttonPanel.add(rentBtn);
        
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(8));
        contentPanel.add(infoLabel);
        contentPanel.add(Box.createVerticalStrut(12));
        contentPanel.add(imagePanel);
        contentPanel.add(Box.createVerticalStrut(12));
        contentPanel.add(scrollPane);
        contentPanel.add(Box.createVerticalStrut(12));
        contentPanel.add(priceLabel);
        contentPanel.add(Box.createVerticalStrut(8));
        contentPanel.add(buttonPanel);
        
        dialog.add(contentPanel);
        dialog.setVisible(true);
    }
    
    private void handlePurchase(Manhwa manhwa, boolean permanent) {
        boolean success = permanent ? 
            purchaseService.buyManhwa(currentUser, manhwa) :
            purchaseService.rentManhwa(currentUser, manhwa);
        
        if (success) {
            updateCoinsDisplay();
            JOptionPane.showMessageDialog(this,
                "Successfully " + (permanent ? "purchased" : "rented") + " " + manhwa.getTitle(),
                "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                "Purchase failed. Check your coins or ownership status.",
                "Failed", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateCoinsDisplay() {
        coinsLabel.setText("💰 " + (int)currentUser.getWallet().getCoins());
    }
    
    private String truncateText(String text, int length) {
        if (text == null) return "";
        if (text.length() <= length) return text;
        return text.substring(0, length) + "...";
    }
}
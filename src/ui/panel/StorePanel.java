package ui.panel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import service.PurchaseService;
import domain.content.Manhwa;
import domain.user.User;
import app.CsvOpener;
import ui.frame.MainFrame;

public class StorePanel extends JPanel {
    private final PurchaseService purchaseService;
    private List<Manhwa> manhwaList;
    private List<Manhwa> filteredManhwaList;
    private User currentUser;
    private JLabel coinsLabel;
    private int currentTopIndex = 0;
    private JPanel topTenImagePanel;
    private JPanel gridPanel;
    private int currentGridPage = 0;
    private final int ITEMS_PER_PAGE = 4;
    
    // Cache for loaded images
    private java.util.Map<String, ImageIcon> imageCache = new java.util.HashMap<>();
    private JTextField searchField;

    public StorePanel(PurchaseService purchaseService, User user) {
        this.purchaseService = purchaseService;
        this.currentUser = user;
        
        setLayout(new BorderLayout());
        setBackground(new Color(248, 248, 248));
        setPreferredSize(new Dimension(640, 720));
        
        CsvOpener opener = new CsvOpener();
        manhwaList = opener.getDb();
        filteredManhwaList = new ArrayList<>(manhwaList);
        
        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(new Color(248, 248, 248));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        // Top bar - 45px
        mainPanel.add(createTopBar());
        mainPanel.add(Box.createVerticalStrut(8));
        
        // Top 10 section header - 25px
        mainPanel.add(createSectionHeader("Top 10"));
        mainPanel.add(Box.createVerticalStrut(5));
        
        // Top 10 carousel - 150px
        mainPanel.add(createTopTenCarousel());
        mainPanel.add(Box.createVerticalStrut(8));
        
        // Search bar - 40px
        mainPanel.add(createSearchBar());
        mainPanel.add(Box.createVerticalStrut(8));
        
        // 2x2 Grid - 340px
        mainPanel.add(createManhwaGrid());
        mainPanel.add(Box.createVerticalStrut(8));
        
        // Navigation buttons - 35px
        mainPanel.add(createNavigationPanel());
        
        // Wrap in a fixed-size container
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(new Color(248, 248, 248));
        container.add(mainPanel, BorderLayout.NORTH);
        
        add(container, BorderLayout.CENTER);
    }
    
    private JPanel createTopBar() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(20, 108, 148));
        panel.setMaximumSize(new Dimension(610, 45));
        panel.setPreferredSize(new Dimension(610, 45));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        
        // Left side - App name
        JLabel appName = new JLabel("Manhwa db");
        appName.setFont(new Font("Segoe UI", Font.BOLD, 18));
        appName.setForeground(Color.WHITE);
        
        // Right side - Navigation buttons and wallet
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightPanel.setOpaque(false);
        
        JButton storeBtn = createTopBarButton("Store");
        JButton libraryBtn = createTopBarButton("Library");
        
        coinsLabel = new JLabel("💰 " + (int)currentUser.getWallet().getCoins());
        coinsLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        coinsLabel.setForeground(Color.WHITE);
        
        storeBtn.setEnabled(false);
        
        libraryBtn.addActionListener(e -> {
            MainFrame frame = (MainFrame) SwingUtilities.getWindowAncestor(this);
            frame.showLibrary(currentUser);
        });
    
        rightPanel.add(storeBtn);
        rightPanel.add(libraryBtn);
        rightPanel.add(Box.createHorizontalStrut(8));
        rightPanel.add(coinsLabel);
        
        panel.add(appName, BorderLayout.WEST);
        panel.add(rightPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    private JButton createTopBarButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(30, 118, 158));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(74, 28));
        
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
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(610, 25));
        panel.setPreferredSize(new Dimension(610, 25));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(new Color(33, 33, 33));
        
        panel.add(titleLabel);
        return panel;
    }
    
    private JPanel createTopTenCarousel() {
        JPanel carouselPanel = new JPanel(new BorderLayout());
        carouselPanel.setOpaque(false);
        carouselPanel.setMaximumSize(new Dimension(610, 150));
        carouselPanel.setPreferredSize(new Dimension(610, 150));
        
        // Image display panel
        topTenImagePanel = new JPanel(new BorderLayout());
        topTenImagePanel.setBackground(Color.WHITE);
        topTenImagePanel.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 2));
        topTenImagePanel.setPreferredSize(new Dimension(520, 150));
        
        updateTopTenImage();
        
        // Navigation buttons
        JButton prevBtn = new JButton("‹");
        JButton nextBtn = new JButton("›");
        
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
        btn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btn.setBackground(new Color(20, 108, 148));
        btn.setForeground(Color.WHITE);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(45, 150));
    }
    
    private JPanel createSearchBar() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(610, 40));
        panel.setPreferredSize(new Dimension(610, 40));
        
        searchField = new JTextField("Search manhwa...");
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchField.setForeground(new Color(150, 150, 150));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
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
        
        // Search as user types (real-time search)
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                performSearch();
            }
        });
        
        panel.add(searchField, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createManhwaGrid() {
        JPanel container = new JPanel(new BorderLayout());
        container.setOpaque(false);
        container.setMaximumSize(new Dimension(610, 340));
        container.setPreferredSize(new Dimension(610, 340));
        
        gridPanel = new JPanel(new GridLayout(2, 2, 12, 12));
        gridPanel.setOpaque(false);
        
        updateGrid();
        
        container.add(gridPanel, BorderLayout.CENTER);
        return container;
    }
    
    private JPanel createManhwaCard(Manhwa manhwa) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Image panel with cover image
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.setBackground(new Color(240, 240, 240));
        imagePanel.setPreferredSize(new Dimension(70, 90));
        imagePanel.setMaximumSize(new Dimension(70, 90));
        imagePanel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        
        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        try {
            ImageIcon coverImage = loadImage(manhwa.getCoverImageUrl());
            if (coverImage != null) {
                Image scaledImage = coverImage.getImage().getScaledInstance(70, 90, Image.SCALE_SMOOTH);
                imageLabel.setIcon(new ImageIcon(scaledImage));
            } else {
                imageLabel.setText("📖");
                imageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 32));
            }
        } catch (Exception e) {
            imageLabel.setText("📖");
            imageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 32));
        }
        
        imagePanel.add(imageLabel, BorderLayout.CENTER);
        imagePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Title with more space
        JLabel titleLabel = new JLabel("<html><center>" + truncateText(manhwa.getTitle(), 35) + "</center></html>");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        titleLabel.setForeground(new Color(33, 33, 33));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setPreferredSize(new Dimension(100, 40));
        
        // Add rating/chapter info below title
        JLabel infoLabel = new JLabel("<html><center>⭐ " + manhwa.getRating() + "<br>📖 " + manhwa.getChapters() + "</center></html>");
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 9));
        infoLabel.setForeground(new Color(120, 120, 120));
        infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        infoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        infoLabel.setPreferredSize(new Dimension(100, 30));
        
        card.add(imagePanel);
        card.add(Box.createVerticalStrut(6));
        card.add(titleLabel);
        card.add(Box.createVerticalStrut(2));
        card.add(infoLabel);
        
        card.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                showManhwaDetails(manhwa);
            }
            public void mouseEntered(MouseEvent e) {
                card.setBackground(new Color(250, 250, 250));
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(20, 108, 148), 2),
                    BorderFactory.createEmptyBorder(8, 8, 8, 8)
                ));
            }
            public void mouseExited(MouseEvent e) {
                card.setBackground(Color.WHITE);
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                    BorderFactory.createEmptyBorder(8, 8, 8, 8)
                ));
            }
        });
        
        return card;
    }
    
    private JPanel createEmptyCard() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        
        // Empty image panel
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.setBackground(new Color(240, 240, 240));
        imagePanel.setPreferredSize(new Dimension(70, 90));
        imagePanel.setMaximumSize(new Dimension(70, 90));
        imagePanel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        
        JLabel emptyLabel = new JLabel("—", SwingConstants.CENTER);
        emptyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 32));
        emptyLabel.setForeground(new Color(180, 180, 180));
        imagePanel.add(emptyLabel, BorderLayout.CENTER);
        
        card.add(imagePanel);
        return card;
    }
    
    private JPanel createNavigationPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(610, 35));
        panel.setPreferredSize(new Dimension(610, 35));
        
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
            if ((currentGridPage + 1) * ITEMS_PER_PAGE < filteredManhwaList.size()) {
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
        btn.setPreferredSize(new Dimension(100, 32));
    }
    
    private void performSearch() {
        String searchText = searchField.getText().trim().toLowerCase();
        
        if (searchText.isEmpty() || searchText.equals("search manhwa...")) {
            filteredManhwaList = new ArrayList<>(manhwaList);
        } else {
            filteredManhwaList = new ArrayList<>();
            for (Manhwa manhwa : manhwaList) {
                if (manhwa.getTitle().toLowerCase().contains(searchText) ||
                    (manhwa.getSynopsis() != null && manhwa.getSynopsis().toLowerCase().contains(searchText))) {
                    filteredManhwaList.add(manhwa);
                }
            }
        }
        
        currentGridPage = 0;
        updateGrid();
    }
    
    private void updateGrid() {
        gridPanel.removeAll();
        
        int startIdx = currentGridPage * ITEMS_PER_PAGE;
        int endIdx = Math.min(startIdx + ITEMS_PER_PAGE, filteredManhwaList.size());
        
        for (int i = startIdx; i < endIdx; i++) {
            gridPanel.add(createManhwaCard(filteredManhwaList.get(i)));
        }
        
        int itemsToShow = endIdx - startIdx;
        if (itemsToShow < ITEMS_PER_PAGE) {
            for (int i = itemsToShow; i < ITEMS_PER_PAGE; i++) {
                gridPanel.add(createEmptyCard());
            }
        }
        
        gridPanel.revalidate();
        gridPanel.repaint();
    }
    
    private void updateTopTenImage() {
        topTenImagePanel.removeAll();
        
        if (currentTopIndex < manhwaList.size()) {
            Manhwa manhwa = manhwaList.get(currentTopIndex);
            
            JPanel contentPanel = new JPanel(new BorderLayout());
            contentPanel.setBackground(Color.WHITE);
            contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            
            JLabel imageLabel = new JLabel();
            imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
            
            try {
                ImageIcon coverImage = loadImage(manhwa.getCoverImageUrl());
                if (coverImage != null) {
                    Image scaledImage = coverImage.getImage().getScaledInstance(100, 130, Image.SCALE_SMOOTH);
                    imageLabel.setIcon(new ImageIcon(scaledImage));
                } else {
                    imageLabel.setText("📖");
                    imageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 40));
                }
            } catch (Exception e) {
                imageLabel.setText("📖");
                imageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 40));
            }
            
            JLabel rankLabel = new JLabel("#" + (currentTopIndex + 1), SwingConstants.CENTER);
            rankLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
            rankLabel.setForeground(new Color(20, 108, 148));
            
            JLabel titleLabel = new JLabel("<html><center>" + truncateText(manhwa.getTitle(), 30) + "</center></html>", SwingConstants.CENTER);
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            titleLabel.setForeground(new Color(33, 33, 33));
            
            JLabel ratingLabel = new JLabel("⭐ " + manhwa.getRating() + "  |  📖 " + manhwa.getChapters(), SwingConstants.CENTER);
            ratingLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            ratingLabel.setForeground(new Color(120, 120, 120));
            
            JPanel leftPanel = new JPanel(new BorderLayout());
            leftPanel.setBackground(Color.WHITE);
            leftPanel.setPreferredSize(new Dimension(90, 130));
            leftPanel.add(imageLabel, BorderLayout.CENTER);
            
            JPanel textPanel = new JPanel();
            textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
            textPanel.setBackground(Color.WHITE);
            
            rankLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            ratingLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            textPanel.add(Box.createVerticalStrut(10));
            textPanel.add(rankLabel);
            textPanel.add(Box.createVerticalStrut(5));
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
    
    private ImageIcon loadImage(String imageUrl) {
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            return null;
        }
        
        if (imageCache.containsKey(imageUrl)) {
            return imageCache.get(imageUrl);
        }
        
        try {
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
        if (currentUser.getLibrary().hasValidAccess(manhwa)) {
            JOptionPane.showMessageDialog(this,
                "You already own this manhwa.",
                "Already Owned", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

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
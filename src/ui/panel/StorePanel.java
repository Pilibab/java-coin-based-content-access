package ui.panel;


import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import ui.frame.MainFrame;
import domain.content.Manhwa;
import domain.user.User;


public class StorePanel extends JPanel {
    private final MainFrame frame;

    private List<Manhwa> manhwaList;
    private List<Manhwa> filteredManhwaList; // Filtered list based on search
    private User currentUser;
    private JLabel coinsLabel;
    private int currentTopIndex = 0;
    private JPanel topTenImagePanel;
    private JPanel gridPanel;
    private int currentGridPage = 0;
    private final int ITEMS_PER_PAGE = 4;
    private JTextField searchField;
    private static final String SEARCH_PLACEHOLDER = "Search manhwa...";

    // Cache for loaded images
    private java.util.Map<String, ImageIcon> imageCache = new java.util.HashMap<>();


    public StorePanel(MainFrame frame, User user, List <Manhwa> manhwas) {

        // ! move this to purchase service
        this.currentUser = user;
        this.frame = frame;

        setLayout(new BorderLayout());
        setBackground(new Color(248, 248, 248));
        setPreferredSize(new Dimension(640, 720));

        // ! should the store panel really be concerned with getting the manhwa?
        manhwaList = manhwas;
        filteredManhwaList = new ArrayList<>(manhwas); // Initialize with all manhwa

    
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
            frame.showUserLibrary(currentUser);
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

    private void updateTopTenImage() {
        topTenImagePanel.removeAll();
       
        if (currentTopIndex < manhwaList.size()) {
            Manhwa manhwa = manhwaList.get(currentTopIndex);
           
            JPanel contentPanel = new JPanel(new BorderLayout());
            contentPanel.setBackground(Color.WHITE);
            contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
           
            // Load and display the cover image - COMPRESSED from 90x130 to 70x110
            JLabel imageLabel = new JLabel();
            imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
           
            try {
                ImageIcon coverImage = loadImage(manhwa.getCoverImageUrl());
                if (coverImage != null) {
                    Image scaledImage = coverImage.getImage().getScaledInstance(100,130, Image.SCALE_SMOOTH);
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
            leftPanel.setPreferredSize(new Dimension(90, 130)); // Reduced from 110 to 90
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

    private JPanel createSearchBar() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(610, 40));
        panel.setPreferredSize(new Dimension(610, 40));
    
        searchField = new JTextField(SEARCH_PLACEHOLDER);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchField.setForeground(new Color(150, 150, 150));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
    
        // Placeholder behavior
        searchField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (searchField.getText().equals(SEARCH_PLACEHOLDER)) {
                    searchField.setText("");
                    searchField.setForeground(new Color(33, 33, 33));
                }
            }
            public void focusLost(FocusEvent e) {
                if (searchField.getText().trim().isEmpty()) {
                    searchField.setText(SEARCH_PLACEHOLDER);
                    searchField.setForeground(new Color(150, 150, 150));
                }
            }
        });

        // Live search functionality
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                performSearch();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                performSearch();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                performSearch();
            }
        });
    
        panel.add(searchField, BorderLayout.CENTER);
        return panel;
    }

    private void performSearch() {
        String query = searchField.getText();
        if (query == null) query = "";
        query = query.trim();

        // If placeholder or empty, show all manhwa
        if (query.isEmpty() || SEARCH_PLACEHOLDER.equals(query)) {
            filteredManhwaList = new ArrayList<>(manhwaList);
        } else {
            filteredManhwaList = filterManhwa(query);
        }

        currentGridPage = 0; // Reset to first page when searching
        updateGrid();
    }

    private List<Manhwa> filterManhwa(String query) {
        List<Manhwa> filtered = new ArrayList<>();
        String lowQuery = query.toLowerCase();

        for (Manhwa manhwa : manhwaList) {
            if (matchesSearch(manhwa, lowQuery)) {
                filtered.add(manhwa);
            }
        }

        return filtered;
    }

    private boolean matchesSearch(Manhwa manhwa, String query) {
        String title = manhwa.getTitle() == null ? "" : manhwa.getTitle().toLowerCase();
        String tags = manhwa.getTags() == null ? "" : manhwa.getTags().toLowerCase();
        
        return title.contains(query) || tags.contains(query);
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

    private void updateGrid() {
        gridPanel.removeAll();
       
        if (filteredManhwaList.isEmpty()) {
            displayNoResultsMessage();
        } else {
            displayManhwaCards();
        }
       
        gridPanel.revalidate();
        gridPanel.repaint();
    }

    private void displayNoResultsMessage() {
        gridPanel.setLayout(new BorderLayout());
        JLabel noResults = new JLabel("No manhwa found", SwingConstants.CENTER);
        noResults.setFont(new Font("Segoe UI", Font.BOLD, 16));
        noResults.setForeground(new Color(120, 120, 120));
        gridPanel.add(noResults, BorderLayout.CENTER);
    }

    private void displayManhwaCards() {
        gridPanel.setLayout(new GridLayout(2, 2, 12, 12));
        
        int startIdx = currentGridPage * ITEMS_PER_PAGE;
        int endIdx = Math.min(startIdx + ITEMS_PER_PAGE, filteredManhwaList.size());
       
        for (int i = startIdx; i < endIdx; i++) {
            gridPanel.add(createManhwaCard(filteredManhwaList.get(i)));
        }

        // Fill empty slots with blank panels to maintain grid structure
        int displayed = endIdx - startIdx;
        for (int i = displayed; i < ITEMS_PER_PAGE; i++) {
            JPanel emptyPanel = new JPanel();
            emptyPanel.setBackground(new Color(248, 248, 248));
            gridPanel.add(emptyPanel);
        }
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
       
        // Image panel with cover image - COMPRESSED from 130x140 to 100x120
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.setBackground(new Color(240, 240, 240));
        imagePanel.setPreferredSize(new Dimension(70, 90)); // Reduced from 130x140
        imagePanel.setMaximumSize(new Dimension(70, 90));   // Reduced from 130x140
        imagePanel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
       
        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
       
        try {
            ImageIcon coverImage = loadImage(manhwa.getCoverImageUrl());
            if (coverImage != null) {
                // Compressed image size from 130x140 to 100x120
                Image scaledImage = coverImage.getImage().getScaledInstance(70, 90, Image.SCALE_SMOOTH);
                imageLabel.setIcon(new ImageIcon(scaledImage));
            } else {
                imageLabel.setText("📖");
                imageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 32)); // Smaller font
            }
        } catch (Exception e) {
            imageLabel.setText("📖");
            imageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 32)); // Smaller font
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
                frame.showManhwaDetails(manhwa, currentUser);
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
   
    private String truncateText(String text, int length) {
        if (text == null) return "";
        if (text.length() <= length) return text;
        return text.substring(0, length) + "...";
    }

    public void updateData(User user) {
        // updates the user data, for updating coin value display 
        this.currentUser = user;

        coinsLabel.setText("Coins: " + user.getWallet().getCoins());

        revalidate();
        repaint();
    }
}
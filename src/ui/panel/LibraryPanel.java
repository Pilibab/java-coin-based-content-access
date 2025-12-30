package ui.panel;

import domain.content.Manhwa;
import domain.content.access.Access;
import domain.content.access.PermanentAccess;
import domain.user.User;
import ui.frame.MainFrame;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.image.BufferedImage;
import java.net.URI;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

public class LibraryPanel extends JPanel {
    private User currentUser;
    private JLabel coinsLabel;
    private JPanel gridPanel;
    private java.util.Map<String, ImageIcon> imageCache = new java.util.HashMap<>();
    private JTextField searchField;
    private static final String SEARCH_PLACEHOLDER = "Search Your Library";
    private int currentGridPage = 0;
    private final int ITEMS_PER_PAGE = 4;
    private JPanel navPanel;

    // Color constants
    private static final Color PRIMARY_BLUE = new Color(20, 108, 148);
    private static final Color SECONDARY_BLUE = new Color(30, 118, 158);
    private static final Color BACKGROUND_GRAY = new Color(248, 248, 248);
    private static final Color CARD_BACKGROUND = Color.WHITE;
    private static final Color BORDER_GRAY = new Color(230, 230, 230);
    private static final Color TEXT_PRIMARY = new Color(33, 33, 33);
    private static final Color TEXT_SECONDARY = new Color(120, 120, 120);

    public LibraryPanel(User user) {
        this.currentUser = user;
        initializePanel();
        initComponents();
    }

    private void initializePanel() {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_GRAY);
        setPreferredSize(new Dimension(640, 720));
    }

    private void initComponents() {
        JPanel mainPanel = createMainPanel();
        
        mainPanel.add(createTopBar());
        mainPanel.add(Box.createVerticalStrut(8));
        mainPanel.add(createHeaderPanel());
        mainPanel.add(Box.createVerticalStrut(8));
        mainPanel.add(createSearchPanel());
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(createLibraryGrid());
        mainPanel.add(Box.createVerticalStrut(15));
        
        navPanel = createNavigationPanel();
        mainPanel.add(navPanel);

        JPanel wrapper = createWrapperPanel();
        wrapper.add(mainPanel, BorderLayout.CENTER);
        add(wrapper, BorderLayout.CENTER);
    }

    private JPanel createMainPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BACKGROUND_GRAY);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        panel.setPreferredSize(new Dimension(640, 720));
        panel.setMaximumSize(new Dimension(640, 720));
        return panel;
    }

    private JPanel createWrapperPanel() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(BACKGROUND_GRAY);
        wrapper.setPreferredSize(new Dimension(640, 720));
        wrapper.setMaximumSize(new Dimension(640, 720));
        return wrapper;
    }

    private JPanel createTopBar() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PRIMARY_BLUE);
        panel.setMaximumSize(new Dimension(610, 45));
        panel.setPreferredSize(new Dimension(610, 45));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        panel.add(createAppNameLabel(), BorderLayout.WEST);
        panel.add(createTopBarRightPanel(), BorderLayout.EAST);

        return panel;
    }

    private JLabel createAppNameLabel() {
        JLabel appName = new JLabel("Manhwa db");
        appName.setFont(new Font("Segoe UI", Font.BOLD, 18));
        appName.setForeground(Color.WHITE);
        return appName;
    }

    private JPanel createTopBarRightPanel() {
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightPanel.setOpaque(false);

        JButton storeBtn = createTopBarButton("Store");
        JButton libraryBtn = createTopBarButton("Library");
        
        libraryBtn.setEnabled(false);
        storeBtn.addActionListener(e -> navigateToStore());

        coinsLabel = createCoinsLabel();

        rightPanel.add(storeBtn);
        rightPanel.add(libraryBtn);
        rightPanel.add(Box.createHorizontalStrut(10));
        rightPanel.add(coinsLabel);

        return rightPanel;
    }

    private JLabel createCoinsLabel() {
        JLabel label = new JLabel("💰 " + (int)currentUser.getWallet().getCoins());
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(Color.WHITE);
        return label;
    }

    private void navigateToStore() {
        MainFrame frame = (MainFrame) SwingUtilities.getWindowAncestor(this);
        frame.showStore();
    }

    private JButton createTopBarButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btn.setForeground(Color.WHITE);
        btn.setBackground(SECONDARY_BLUE);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(74, 28));
        btn.setMaximumSize(new Dimension(70, 30));
        return btn;
    }

    private JPanel createHeaderPanel() {
        JLabel header = new JLabel("Your Library");
        header.setFont(new Font("Segoe UI", Font.BOLD, 18));
        header.setForeground(TEXT_PRIMARY);
        
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        headerPanel.setOpaque(false);
        headerPanel.add(header);
        headerPanel.setMaximumSize(new Dimension(600, 30));
        
        return headerPanel;
    }

    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        panel.setOpaque(false);

        searchField = createSearchField();
        panel.add(searchField);
        
        return panel;
    }

    private JTextField createSearchField() {
        JTextField field = new JTextField(20);
        field.setPreferredSize(new Dimension(320, 32));
        field.setBackground(new Color(240, 240, 240));
        field.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        field.setText(SEARCH_PLACEHOLDER);
        field.setForeground(Color.GRAY);

        addSearchFieldListeners(field);
        
        return field;
    }

    private void addSearchFieldListeners(JTextField field) {
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (SEARCH_PLACEHOLDER.equals(field.getText())) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().trim().isEmpty()) {
                    field.setText(SEARCH_PLACEHOLDER);
                    field.setForeground(Color.GRAY);
                    updateGrid();
                }
            }
        });

        field.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { performSearch(); }
            
            @Override
            public void removeUpdate(DocumentEvent e) { performSearch(); }
            
            @Override
            public void changedUpdate(DocumentEvent e) { performSearch(); }
        });
    }

    private void performSearch() {
        String query = searchField.getText();
        if (query == null) query = "";
        query = query.trim();
        
        if (query.isEmpty() || SEARCH_PLACEHOLDER.equals(query)) {
            updateGrid();
            return;
        }

        List<Manhwa> filtered = filterOwnedManhwa(query);
        refreshLibraryGrid(filtered);
    }

    private List<Manhwa> filterOwnedManhwa(String query) {
        List<Manhwa> owned = currentUser.getLibrary().getOwnedContent();
        List<Manhwa> filtered = new ArrayList<>();
        
        if (owned != null) {
            String lowQuery = query.toLowerCase();
            for (Manhwa m : owned) {
                if (matchesSearch(m, lowQuery)) {
                    filtered.add(m);
                }
            }
        }
        
        return filtered;
    }

    private boolean matchesSearch(Manhwa manhwa, String query) {
        String title = manhwa.getTitle() == null ? "" : manhwa.getTitle();
        String tags = manhwa.getTags() == null ? "" : manhwa.getTags();
        return title.toLowerCase().contains(query) || tags.toLowerCase().contains(query);
    }

    private JPanel createLibraryGrid() {
        JPanel container = new JPanel(new BorderLayout());
        container.setOpaque(false);
        container.setMaximumSize(new Dimension(600, 460));
        container.setPreferredSize(new Dimension(600, 460));

        gridPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        gridPanel.setOpaque(false);
        gridPanel.setMaximumSize(new Dimension(600, 460));
        gridPanel.setPreferredSize(new Dimension(600, 460));

        updateGrid();

        container.add(gridPanel, BorderLayout.CENTER);
        return container;
    }

    private void updateGrid() {
        gridPanel.removeAll();

        List<Manhwa> owned = currentUser.getLibrary().getOwnedContent();

        if (owned == null || owned.isEmpty()) {
            displayEmptyLibraryState();
        } else {
            displayLibraryContent(owned);
        }

        gridPanel.revalidate();
        gridPanel.repaint();
        updateNavigationVisibility(owned);
    }

    private void displayEmptyLibraryState() {
        gridPanel.setLayout(new BorderLayout());
        JPanel emptyPanel = createEmptyStatePanel("Your library is empty");
        gridPanel.add(emptyPanel, BorderLayout.CENTER);
    }

    private JPanel createEmptyStatePanel(String message) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        
        JLabel label = new JLabel(message, SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 20));
        label.setForeground(TEXT_SECONDARY);
        
        panel.add(label, BorderLayout.CENTER);
        return panel;
    }

    private void displayLibraryContent(List<Manhwa> owned) {
        gridPanel.setLayout(new GridLayout(2, 2, 15, 15));

        int startIdx = currentGridPage * ITEMS_PER_PAGE;
        int endIdx = Math.min(startIdx + ITEMS_PER_PAGE, owned.size());

        int shown = 0;
        for (int i = startIdx; i < endIdx; i++) {
            gridPanel.add(createManhwaCard(owned.get(i)));
            shown++;
        }

        fillEmptySlots(shown);
    }

    private void fillEmptySlots(int filledCount) {
        for (int i = filledCount; i < ITEMS_PER_PAGE; i++) {
            gridPanel.add(createEmptySlot());
        }
    }

    private JPanel createEmptySlot() {
        JPanel empty = new JPanel();
        empty.setBackground(new Color(250, 250, 250));
        empty.setBorder(BorderFactory.createLineBorder(BORDER_GRAY, 1));
        return empty;
    }

    private void updateNavigationVisibility(List<Manhwa> owned) {
        if (navPanel != null) {
            navPanel.setVisible(owned != null && owned.size() > ITEMS_PER_PAGE);
        }
    }

    private void refreshLibraryGrid(List<Manhwa> filteredList) {
        gridPanel.removeAll();

        if (filteredList == null || filteredList.isEmpty()) {
            displayFilteredEmptyState();
        } else {
            displayFilteredContent(filteredList);
        }

        gridPanel.revalidate();
        gridPanel.repaint();
    }

    private void displayFilteredEmptyState() {
        gridPanel.setLayout(new BorderLayout());
        JPanel emptyPanel = createEmptyStatePanel("No manhwa found");
        emptyPanel.getComponent(0).setFont(new Font("Segoe UI", Font.BOLD, 18));
        gridPanel.add(emptyPanel, BorderLayout.CENTER);
        
        if (navPanel != null) navPanel.setVisible(false);
    }

    private void displayFilteredContent(List<Manhwa> filteredList) {
        gridPanel.setLayout(new GridLayout(2, 2, 15, 15));
        
        int shown = 0;
        for (Manhwa m : filteredList) {
            if (shown >= ITEMS_PER_PAGE) break;
            gridPanel.add(createManhwaCard(m));
            shown++;
        }
        
        fillEmptySlots(shown);
        
        if (navPanel != null) navPanel.setVisible(false);
    }

    private JPanel createManhwaCard(Manhwa manhwa) {
        JPanel card = new JPanel(new BorderLayout(10, 0));
        card.setBackground(CARD_BACKGROUND);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_GRAY, 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        card.setPreferredSize(new Dimension(280, 220));
        card.setMaximumSize(new Dimension(280, 220));

        card.add(createCoverImagePanel(manhwa), BorderLayout.WEST);
        card.add(createInfoPanel(manhwa), BorderLayout.CENTER);

        return card;
    }

    private JPanel createCoverImagePanel(Manhwa manhwa) {
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.setBackground(new Color(240, 240, 240));
        imagePanel.setPreferredSize(new Dimension(100, 200));
        imagePanel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));

        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);

        try {
            ImageIcon coverImage = loadImage(manhwa.getCoverImageUrl());
            if (coverImage != null) {
                Image scaledImage = coverImage.getImage().getScaledInstance(100, 200, Image.SCALE_SMOOTH);
                imageLabel.setIcon(new ImageIcon(scaledImage));
            } else {
                setFallbackImage(imageLabel);
            }
        } catch (Exception e) {
            setFallbackImage(imageLabel);
        }

        imagePanel.add(imageLabel, BorderLayout.CENTER);
        return imagePanel;
    }

    private void setFallbackImage(JLabel label) {
        label.setText("📖");
        label.setFont(new Font("Segoe UI", Font.PLAIN, 48));
    }

    private JPanel createInfoPanel(Manhwa manhwa) {
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);

        infoPanel.add(createTitleLabel(manhwa));
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(createStatusLabel(manhwa));
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(createRatingLabel(manhwa));
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(createRankLabel(manhwa));
        infoPanel.add(Box.createVerticalGlue());
        infoPanel.add(createAccessLabel(manhwa));

        return infoPanel;
    }

    private JLabel createTitleLabel(Manhwa manhwa) {
        JLabel titleLabel = new JLabel("<html>" + manhwa.getTitle() + "</html>");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleLabel.setMaximumSize(new Dimension(150, 60));
        return titleLabel;
    }

    private JLabel createStatusLabel(Manhwa manhwa) {
        String chapters = manhwa.getChapters();
        String statusText;
        
        if ("unknown".equalsIgnoreCase(chapters)) {
            statusText = "📖 Status: Ongoing";
        } else {
            statusText = "📖 Status: Completed (" + chapters + " ch)";
        }
        
        JLabel statusLabel = new JLabel(statusText);
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        statusLabel.setForeground(TEXT_SECONDARY);
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        return statusLabel;
    }

    private JLabel createRatingLabel(Manhwa manhwa) {
        JLabel ratingLabel = new JLabel("⭐ " + String.format("%.1f", manhwa.getRating()));
        ratingLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        ratingLabel.setForeground(new Color(255, 165, 0));
        ratingLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        return ratingLabel;
    }

    private JLabel createRankLabel(Manhwa manhwa) {
        JLabel rankLabel = new JLabel("🏆 Rank #" + manhwa.getRank());
        rankLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        rankLabel.setForeground(new Color(70, 130, 180));
        rankLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        return rankLabel;
    }

    private JLabel createAccessLabel(Manhwa manhwa) {
        Access access = findManhwaAccess(manhwa);
        JLabel accessLabel = new JLabel();
        accessLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        accessLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        if (access instanceof PermanentAccess) {
            accessLabel.setText("✓ Owned");
            accessLabel.setForeground(new Color(76, 175, 80));
        } else if (access != null) {
            String timeRemaining = formatTimeRemaining(access);
            accessLabel.setText("<html>⏱ Available<br>" + timeRemaining + "</html>");
            accessLabel.setForeground(new Color(255, 152, 0));
        } else {
            accessLabel.setText("❌ Access Expired");
            accessLabel.setForeground(new Color(244, 67, 54));
        }

        return accessLabel;
    }

    private Access findManhwaAccess(Manhwa manhwa) {
        // Simply iterate through all accesses to find matching manhwa
        for (Access a : currentUser.getLibrary().getAccesses()) {
            if (a != null && a.getManhwa() != null && a.getManhwa().equals(manhwa) && a.isValid()) {
                return a;
            }
        }
        return null;
    }

    private String formatTimeRemaining(Access access) {
        try {
            // Assuming Access has an expiry date method
            LocalDateTime expiry = access.getExpiryDate();
            if (expiry == null) return "Until: Unknown";
            
            LocalDateTime now = LocalDateTime.now();
            Duration duration = Duration.between(now, expiry);
            
            long days = duration.toDays();
            long hours = duration.toHours() % 24;
            
            if (days > 0) {
                return "Until: " + days + "d " + hours + "h";
            } else if (hours > 0) {
                return "Until: " + hours + "h";
            } else {
                long minutes = duration.toMinutes();
                return "Until: " + minutes + "m";
            }
        } catch (Exception e) {
            return "Until: Unknown";
        }
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

    private JPanel createNavigationPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(600, 40));
        panel.setPreferredSize(new Dimension(600, 40));

        JButton prevBtn = createNavButton("◄ Previous");
        JButton nextBtn = createNavButton("Next ►");

        prevBtn.addActionListener(e -> navigatePrevious());
        nextBtn.addActionListener(e -> navigateNext());

        panel.add(prevBtn);
        panel.add(nextBtn);
        return panel;
    }

    private JButton createNavButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(PRIMARY_BLUE);
        btn.setForeground(Color.WHITE);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(130, 36));
        btn.setMargin(new Insets(4, 10, 4, 10));
        return btn;
    }

    private void navigatePrevious() {
        if (currentGridPage > 0) {
            currentGridPage--;
            updateGrid();
        }
    }

    private void navigateNext() {
        List<Manhwa> owned = currentUser.getLibrary().getOwnedContent();
        if ((currentGridPage + 1) * ITEMS_PER_PAGE < owned.size()) {
            currentGridPage++;
            updateGrid();
        }
    }
}
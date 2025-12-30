package ui.panel;

import domain.content.Manhwa;
import domain.user.User;
import ui.frame.MainFrame;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.net.URI;
import java.net.URL;
import java.util.List;

public class LibraryPanel extends JPanel {
    private User currentUser;
    private JLabel coinsLabel;
    private JPanel gridPanel;
    // Cache for loaded images (copied from StorePanel)
    private java.util.Map<String, ImageIcon> imageCache = new java.util.HashMap<>();
    private int currentGridPage = 0;
    private final int ITEMS_PER_PAGE = 4;
    private JPanel navPanel;

    public LibraryPanel(User user) {
        this.currentUser = user;

        setLayout(new BorderLayout());
        setBackground(new Color(248, 248, 248));
        setPreferredSize(new Dimension(640, 720));

        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(new Color(248, 248, 248));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        mainPanel.setPreferredSize(new Dimension(640, 720));
        mainPanel.setMaximumSize(new Dimension(640, 720));

        mainPanel.add(createTopBar());
        mainPanel.add(Box.createVerticalStrut(15));

        JLabel header = new JLabel("Your Library");
        header.setFont(new Font("Segoe UI", Font.BOLD, 18));
        header.setForeground(new Color(33, 33, 33));
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        headerPanel.setOpaque(false);
        headerPanel.add(header);
        headerPanel.setMaximumSize(new Dimension(600, 30));
        mainPanel.add(headerPanel);
        mainPanel.add(Box.createVerticalStrut(10));

        mainPanel.add(createLibraryGrid());
        mainPanel.add(Box.createVerticalStrut(15));
        navPanel = createNavigationPanel();
        mainPanel.add(navPanel);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(new Color(248, 248, 248));
        wrapper.setPreferredSize(new Dimension(640, 720));
        wrapper.setMaximumSize(new Dimension(640, 720));
        wrapper.add(mainPanel, BorderLayout.CENTER);

        add(wrapper, BorderLayout.CENTER);
    }

    private JPanel createTopBar() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(20, 108, 148));
        panel.setMaximumSize(new Dimension(600, 50));
        panel.setPreferredSize(new Dimension(600, 50));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel appName = new JLabel("Manhwa db");
        appName.setFont(new Font("Segoe UI", Font.BOLD, 20));
        appName.setForeground(Color.WHITE);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setOpaque(false);

        JButton storeBtn = createTopBarButton("Store");
        JButton libraryBtn = createTopBarButton("Library");
        JButton cartBtn = createTopBarButton("Cart");

        // In Library panel, the Library button is disabled
        libraryBtn.setEnabled(false);

        // Store button should be enabled and navigate back to Store (use MainFrame to preserve user)
        storeBtn.addActionListener(e -> {
            MainFrame frame = (MainFrame) SwingUtilities.getWindowAncestor(this);
            frame.showStore();
        });

        coinsLabel = new JLabel("💰 " + (int)currentUser.getWallet().getCoins());
        coinsLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        coinsLabel.setForeground(Color.WHITE);

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
        btn.setMaximumSize(new Dimension(70, 30));
        return btn;
    }

    private JPanel createLibraryGrid() {
        JPanel container = new JPanel(new BorderLayout());
        container.setOpaque(false);
        container.setMaximumSize(new Dimension(600, 220));
        container.setPreferredSize(new Dimension(600, 220));

        gridPanel = new JPanel(new GridLayout(1, 4, 15, 15));
        gridPanel.setOpaque(false);
        gridPanel.setMaximumSize(new Dimension(600, 220));
        gridPanel.setPreferredSize(new Dimension(600, 220));

        updateGrid();

        container.add(gridPanel, BorderLayout.CENTER);
        return container;
    }

    private void updateGrid() {
        gridPanel.removeAll();

        List<Manhwa> owned = currentUser.getLibrary().getOwnedContent();

        if (owned == null || owned.isEmpty()) {
            // Show a single full-area empty state
            gridPanel.setLayout(new BorderLayout());
            JPanel emptyPanel = new JPanel(new BorderLayout());
            emptyPanel.setOpaque(false);
            JLabel emptyLabel = new JLabel("Your library is empty", SwingConstants.CENTER);
            emptyLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
            emptyLabel.setForeground(new Color(120, 120, 120));
            emptyPanel.add(emptyLabel, BorderLayout.CENTER);
            gridPanel.add(emptyPanel, BorderLayout.CENTER);
        } else {
            // Ensure grid layout for items
            gridPanel.setLayout(new GridLayout(1, 4, 15, 15));

            int startIdx = currentGridPage * ITEMS_PER_PAGE;
            int endIdx = Math.min(startIdx + ITEMS_PER_PAGE, owned.size());

            int shown = 0;
            for (int i = startIdx; i < endIdx; i++) {
                gridPanel.add(createOwnedCard(owned.get(i)));
                shown++;
            }

            // Fill remaining slots with empty panels
            for (int i = shown; i < ITEMS_PER_PAGE; i++) {
                JPanel empty = new JPanel();
                empty.setBackground(new Color(250, 250, 250));
                empty.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 1));
                gridPanel.add(empty);
            }
        }

        gridPanel.revalidate();
        gridPanel.repaint();

        // Show/hide navigation depending on number of owned items
        if (navPanel != null) {
            if (owned == null || owned.size() <= ITEMS_PER_PAGE) {
                navPanel.setVisible(false);
            } else {
                navPanel.setVisible(true);
            }
        }
    }

    private JPanel createOwnedCard(Manhwa manhwa) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        card.setPreferredSize(new Dimension(140, 220));
        card.setMaximumSize(new Dimension(140, 220));

        // Image panel with cover image (120x160)
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
                Image scaledImage = coverImage.getImage().getScaledInstance(120, 160, Image.SCALE_SMOOTH);
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
        imagePanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel("<html><center>" + manhwa.getTitle() + "</center></html>");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(new Color(33, 33, 33));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        // Constrain title height so it doesn't push the button out of view
        titleLabel.setPreferredSize(new Dimension(120, 44));
        titleLabel.setMaximumSize(new Dimension(120, 44));
        titleLabel.setVerticalAlignment(SwingConstants.TOP);

        card.add(imagePanel);
        card.add(Box.createVerticalStrut(8));
        card.add(titleLabel);
        card.add(Box.createVerticalStrut(10));

        return card;
    }

    private void updateCoinsDisplay() {
        coinsLabel.setText("💰 " + (int)currentUser.getWallet().getCoins());
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

    private JPanel createNavigationPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(600, 40));
        panel.setPreferredSize(new Dimension(600, 40));

        JButton prevBtn = new JButton("< Previous");
        JButton nextBtn = new JButton("Next >");

        styleNavButton(prevBtn);
        styleNavButton(nextBtn);

        prevBtn.addActionListener(e -> {
            if (currentGridPage > 0) {
                currentGridPage--;
                updateGrid();
            }
        });

        nextBtn.addActionListener(e -> {
            List<Manhwa> owned = currentUser.getLibrary().getOwnedContent();
            if ((currentGridPage + 1) * ITEMS_PER_PAGE < owned.size()) {
                currentGridPage++;
                updateGrid();
            }
        });

        panel.add(prevBtn);
        panel.add(nextBtn);
        return panel;
    }

    private void styleNavButton(JButton btn) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(new Color(20, 108, 148));
        btn.setForeground(Color.WHITE);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(130, 36));
        btn.setMargin(new Insets(4, 10, 4, 10));
    }
}

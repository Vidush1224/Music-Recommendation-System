package main.java.ui;

import main.java.model.User;
import main.java.model.Song;
import main.java.model.Mood;
import main.java.model.Activity;
import main.java.model.ActivityLog;
import main.java.dao.ActivityLogDAO;
import main.java.service.RecommendationService;
import main.java.dao.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class UserDashboard extends JFrame {

    private User currentUser;
    private RecommendationService recommendationService;
    private SongDAO songDAO;
    private MoodDAO moodDAO;
    private ActivityDAO activityDAO;
    private UserRatingDAO ratingDAO;
    private UserFavoriteDAO favoriteDAO;

    // Modern Color Palette
    private static final Color PRIMARY = new Color(99, 102, 241); // Indigo
    private static final Color SECONDARY = new Color(139, 92, 246); // Purple
    private static final Color SUCCESS = new Color(16, 185, 129); // Green
    private static final Color DANGER = new Color(239, 68, 68); // Red
    private static final Color WARNING = new Color(245, 158, 11); // Amber
    private static final Color INFO = new Color(59, 130, 246); // Blue
    private static final Color BG_LIGHT = new Color(249, 250, 251); // Gray-50
    private static final Color BG_WHITE = Color.WHITE;
    private static final Color TEXT_DARK = new Color(17, 24, 39); // Gray-900
    private static final Color TEXT_MUTED = new Color(107, 114, 128); // Gray-500
    private static final Color BORDER_COLOR = new Color(229, 231, 235); // Gray-200

    // UI Components
    private JTabbedPane tabbedPane;
    private JLabel welcomeLabel;

    public UserDashboard(User user) {
        this.currentUser = user;
        this.recommendationService = new RecommendationService();
        this.songDAO = new SongDAO();
        this.moodDAO = new MoodDAO();
        this.activityDAO = new ActivityDAO();
        this.ratingDAO = new UserRatingDAO();
        this.favoriteDAO = new UserFavoriteDAO();

        initializeUI();
    }

    private void initializeUI() {
        setTitle("Music Recommender - Dashboard");
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main panel with modern background
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BG_LIGHT);

        // Modern header panel
        JPanel headerPanel = createModernHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Tabbed pane with modern styling
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabbedPane.setBackground(BG_WHITE);
        tabbedPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Add tabs with icons
        tabbedPane.addTab("  Recommendations  ", createRecommendationsPanel());
        tabbedPane.addTab("  Mood Shift  ", createMoodShiftPanel());
        tabbedPane.addTab("  Search  ", createSearchPanel());
        tabbedPane.addTab("  Favorites  ", createFavoritesPanel());
        tabbedPane.addTab("  Top Rated  ", createTopRatedPanel());
        tabbedPane.addTab("  Music Player  ", createMusicPlayerPanel());
        tabbedPane.addTab("  Profile  ", createProfilePanel());

        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        add(mainPanel);
        setVisible(true);
    }

    /**
     * Create modern header panel with gradient
     */
    private JPanel createModernHeaderPanel() {
        JPanel headerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                int w = getWidth();
                int h = getHeight();
                GradientPaint gp = new GradientPaint(0, 0, PRIMARY, w, h, SECONDARY);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
            }
        };
        headerPanel.setPreferredSize(new Dimension(1100, 80));
        headerPanel.setLayout(null);

        welcomeLabel = new JLabel("Welcome back, " + currentUser.getUsername() + "!");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setBounds(40, 20, 600, 40);
        headerPanel.add(welcomeLabel);

        JButton logoutButton = createModernButton("Logout", DANGER);
        logoutButton.setBounds(950, 22, 110, 36);
        logoutButton.addActionListener(e -> logout());
        headerPanel.add(logoutButton);

        return headerPanel;
    }

    /**
     * Create modern styled button
     */
    private JButton createModernButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));

        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.darker());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    /**
     * Create modern combo box
     */
    private JComboBox<String> createModernComboBox() {
        JComboBox<String> combo = new JComboBox<>();
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        combo.setPreferredSize(new Dimension(160, 36));
        combo.setBackground(BG_WHITE);
        combo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        return combo;
    }

    /**
     * Style modern table
     */
    private void styleModernTable(JTable table, Color headerColor) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(42);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(224, 231, 255));
        table.setSelectionForeground(TEXT_DARK);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(headerColor);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getWidth(), 45));
        header.setBorder(BorderFactory.createEmptyBorder());

        // Alternate row colors
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? BG_WHITE : BG_LIGHT);
                }
                setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
                return c;
            }
        });
    }

    /**
     * Create modern card panel
     */
    private JPanel createCardPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(BG_WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(25, 25, 25, 25)));
        return panel;
    }

    /**
     * Create Recommendations Panel
     */
    private JPanel createRecommendationsPanel() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(BG_LIGHT);
        wrapper.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel panel = createCardPanel();

        // Controls panel
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        controlsPanel.setBackground(BG_WHITE);

        JLabel moodLabel = new JLabel("Select Mood:");
        moodLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        moodLabel.setForeground(TEXT_DARK);
        controlsPanel.add(moodLabel);

        JComboBox<String> moodCombo = createModernComboBox();
        List<Mood> moods = moodDAO.getAllMoods();
        for (Mood mood : moods) {
            moodCombo.addItem(mood.getMoodName());
        }
        controlsPanel.add(moodCombo);

        JLabel activityLabel = new JLabel("Select Activity:");
        activityLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        activityLabel.setForeground(TEXT_DARK);
        controlsPanel.add(activityLabel);

        JComboBox<String> activityCombo = createModernComboBox();
        List<Activity> activities = activityDAO.getAllActivities();
        for (Activity activity : activities) {
            activityCombo.addItem(activity.getActivityName());
        }
        controlsPanel.add(activityCombo);

        JButton getRecommendationsBtn = createModernButton("Get Recommendations", PRIMARY);
        controlsPanel.add(getRecommendationsBtn);

        panel.add(controlsPanel, BorderLayout.NORTH);

        // Results table
        String[] columns = { "Title", "Artist", "Genre", "Duration", "Actions" };
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4;
            }
        };
        JTable resultsTable = new JTable(tableModel);
        styleModernTable(resultsTable, PRIMARY);

        JScrollPane scrollPane = new JScrollPane(resultsTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        panel.add(scrollPane, BorderLayout.CENTER);

        // Button action
        getRecommendationsBtn.addActionListener(e -> {
            String selectedMood = (String) moodCombo.getSelectedItem();
            String selectedActivity = (String) activityCombo.getSelectedItem();

            if (selectedMood != null && selectedActivity != null) {
                List<Song> recommendations = recommendationService.getRecommendations(
                        selectedMood, selectedActivity, currentUser.getUserId());

                tableModel.setRowCount(0);

                if (recommendations.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            "No songs found for this mood and activity combination.",
                            "No Results", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    for (Song song : recommendations) {
                        String duration = String.format("%d:%02d",
                                song.getDuration() / 60, song.getDuration() % 60);
                        tableModel.addRow(new Object[] {
                                song.getTitle(),
                                song.getArtist(),
                                song.getGenre(),
                                duration,
                                "View Details"
                        });
                    }
                }
            }
        });

        resultsTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = resultsTable.rowAtPoint(evt.getPoint());
                int col = resultsTable.columnAtPoint(evt.getPoint());

                if (col == 4 && row >= 0) {
                    String title = (String) resultsTable.getValueAt(row, 0);
                    showSongActions(title);
                }
            }
        });

        wrapper.add(panel);
        return wrapper;
    }

    /**
     * Create Mood Shift Panel
     */
    private JPanel createMoodShiftPanel() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(BG_LIGHT);
        wrapper.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel panel = createCardPanel();

        // Info banner
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBackground(new Color(254, 243, 199)); // Amber-100
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(WARNING, 2),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        JLabel infoLabel = new JLabel("<html><b style='font-size:14px'>🔄 Reverse Mood Intelligence</b><br>" +
                "Feeling down? Select your current mood and we'll recommend uplifting music to improve your mood!</html>");
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        infoLabel.setForeground(TEXT_DARK);
        infoPanel.add(infoLabel);

        // Controls
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        controlsPanel.setBackground(BG_WHITE);

        JLabel moodLabel = new JLabel("Current Mood:");
        moodLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        controlsPanel.add(moodLabel);

        JComboBox<String> moodCombo = createModernComboBox();
        List<Mood> moods = moodDAO.getAllMoods();
        for (Mood mood : moods) {
            moodCombo.addItem(mood.getMoodName());
        }
        controlsPanel.add(moodCombo);

        JLabel activityLabel = new JLabel("Activity:");
        activityLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        controlsPanel.add(activityLabel);

        JComboBox<String> activityCombo = createModernComboBox();
        List<Activity> activities = activityDAO.getAllActivities();
        for (Activity activity : activities) {
            activityCombo.addItem(activity.getActivityName());
        }
        controlsPanel.add(activityCombo);

        JButton shiftMoodBtn = createModernButton("Get Mood-Lifting Songs", SECONDARY);
        controlsPanel.add(shiftMoodBtn);

        JPanel topPanel = new JPanel(new BorderLayout(0, 15));
        topPanel.setBackground(BG_WHITE);
        topPanel.add(infoPanel, BorderLayout.NORTH);
        topPanel.add(controlsPanel, BorderLayout.CENTER);
        panel.add(topPanel, BorderLayout.NORTH);

        // Results table
        String[] columns = { "Title", "Artist", "Genre", "Duration", "Actions" };
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4;
            }
        };
        JTable resultsTable = new JTable(tableModel);
        styleModernTable(resultsTable, SECONDARY);

        JScrollPane scrollPane = new JScrollPane(resultsTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        panel.add(scrollPane, BorderLayout.CENTER);

        shiftMoodBtn.addActionListener(e -> {
            String currentMood = (String) moodCombo.getSelectedItem();
            String activity = (String) activityCombo.getSelectedItem();

            if (currentMood != null && activity != null) {
                List<Song> recommendations = recommendationService.getMoodShiftingRecommendations(
                        currentMood, activity, currentUser.getUserId());

                tableModel.setRowCount(0);

                if (recommendations.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            "No mood-shifting songs available for this combination.",
                            "No Results", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    for (Song song : recommendations) {
                        String duration = String.format("%d:%02d",
                                song.getDuration() / 60, song.getDuration() % 60);
                        tableModel.addRow(new Object[] {
                                song.getTitle(),
                                song.getArtist(),
                                song.getGenre(),
                                duration,
                                "View Details"
                        });
                    }
                }
            }
        });

        resultsTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = resultsTable.rowAtPoint(evt.getPoint());
                int col = resultsTable.columnAtPoint(evt.getPoint());

                if (col == 4 && row >= 0) {
                    String title = (String) resultsTable.getValueAt(row, 0);
                    showSongActions(title);
                }
            }
        });

        wrapper.add(panel);
        return wrapper;
    }

    /**
     * Create Search Panel
     */
    private JPanel createSearchPanel() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(BG_LIGHT);
        wrapper.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel panel = createCardPanel();

        // Search controls
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        searchPanel.setBackground(BG_WHITE);

        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        searchPanel.add(searchLabel);

        JTextField searchField = new JTextField(35);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchField.setPreferredSize(new Dimension(350, 36));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(5, 12, 5, 12)));
        searchPanel.add(searchField);

        JButton searchBtn = createModernButton("Search", INFO);
        searchPanel.add(searchBtn);

        panel.add(searchPanel, BorderLayout.NORTH);

        // Results table
        String[] columns = { "Title", "Artist", "Genre", "Duration", "Avg Rating", "Actions" };
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5;
            }
        };
        JTable resultsTable = new JTable(tableModel);
        styleModernTable(resultsTable, INFO);

        JScrollPane scrollPane = new JScrollPane(resultsTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        panel.add(scrollPane, BorderLayout.CENTER);

        Runnable performSearch = () -> {
            String searchTerm = searchField.getText().trim();
            if (searchTerm.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a search term!",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            List<Song> results = songDAO.searchSongs(searchTerm);
            tableModel.setRowCount(0);

            if (results.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No songs found!",
                        "No Results", JOptionPane.INFORMATION_MESSAGE);
            } else {
                for (Song song : results) {
                    String duration = String.format("%d:%02d",
                            song.getDuration() / 60, song.getDuration() % 60);
                    double avgRating = ratingDAO.getAverageSongRating(song.getSongId());
                    String rating = avgRating > 0 ? String.format("%.1f ⭐", avgRating) : "N/A";

                    tableModel.addRow(new Object[] {
                            song.getTitle(),
                            song.getArtist(),
                            song.getGenre(),
                            duration,
                            rating,
                            "View Details"
                    });
                }
            }
        };

        searchBtn.addActionListener(e -> performSearch.run());
        searchField.addActionListener(e -> performSearch.run());

        resultsTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = resultsTable.rowAtPoint(evt.getPoint());
                int col = resultsTable.columnAtPoint(evt.getPoint());

                if (col == 5 && row >= 0) {
                    String title = (String) resultsTable.getValueAt(row, 0);
                    showSongActions(title);
                }
            }
        });

        wrapper.add(panel);
        return wrapper;
    }

    /**
     * Create Favorites Panel
     */
    private JPanel createFavoritesPanel() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(BG_LIGHT);
        wrapper.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel panel = createCardPanel();

        // Header with refresh button
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BG_WHITE);

        JLabel headerLabel = new JLabel("My Favorite Songs");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        headerLabel.setForeground(TEXT_DARK);
        headerPanel.add(headerLabel, BorderLayout.WEST);

        JButton refreshBtn = createModernButton("🔄 Refresh", PRIMARY);
        headerPanel.add(refreshBtn, BorderLayout.EAST);

        panel.add(headerPanel, BorderLayout.NORTH);

        // Table
        String[] columns = { "Title", "Artist", "Genre", "Duration", "My Rating", "Actions" };
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5;
            }
        };
        JTable favoritesTable = new JTable(tableModel);
        styleModernTable(favoritesTable, DANGER);

        JScrollPane scrollPane = new JScrollPane(favoritesTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        panel.add(scrollPane, BorderLayout.CENTER);

        loadFavorites(tableModel);

        refreshBtn.addActionListener(e -> loadFavorites(tableModel));

        favoritesTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = favoritesTable.rowAtPoint(evt.getPoint());
                int col = favoritesTable.columnAtPoint(evt.getPoint());

                if (col == 5 && row >= 0) {
                    String title = (String) favoritesTable.getValueAt(row, 0);
                    showSongActions(title);
                }
            }
        });

        wrapper.add(panel);
        return wrapper;
    }

    private void loadFavorites(DefaultTableModel tableModel) {
        tableModel.setRowCount(0);
        List<Song> favorites = favoriteDAO.getUserFavorites(currentUser.getUserId());

        if (favorites.isEmpty()) {
            tableModel.addRow(new Object[] { "No favorites yet!", "", "", "", "", "" });
        } else {
            for (Song song : favorites) {
                String duration = String.format("%d:%02d",
                        song.getDuration() / 60, song.getDuration() % 60);
                int myRating = ratingDAO.getUserRating(currentUser.getUserId(), song.getSongId());
                String rating = myRating > 0 ? myRating + " ⭐" : "Not Rated";

                tableModel.addRow(new Object[] {
                        song.getTitle(),
                        song.getArtist(),
                        song.getGenre(),
                        duration,
                        rating,
                        "Manage"
                });
            }
        }
    }

    /**
     * Create Top Rated Panel
     */
    private JPanel createTopRatedPanel() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(BG_LIGHT);
        wrapper.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel panel = createCardPanel();

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BG_WHITE);

        JLabel headerLabel = new JLabel("Top Rated Songs");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        headerLabel.setForeground(TEXT_DARK);
        headerPanel.add(headerLabel, BorderLayout.WEST);

        JButton refreshBtn = createModernButton("🔄 Refresh", PRIMARY);
        headerPanel.add(refreshBtn, BorderLayout.EAST);

        panel.add(headerPanel, BorderLayout.NORTH);

        // Table
        String[] columns = { "Rank", "Title", "Artist", "Genre", "Avg Rating", "Actions" };
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5;
            }
        };
        JTable topRatedTable = new JTable(tableModel);
        styleModernTable(topRatedTable, WARNING);

        JScrollPane scrollPane = new JScrollPane(topRatedTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        panel.add(scrollPane, BorderLayout.CENTER);

        loadTopRated(tableModel);

        refreshBtn.addActionListener(e -> loadTopRated(tableModel));

        topRatedTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = topRatedTable.rowAtPoint(evt.getPoint());
                int col = topRatedTable.columnAtPoint(evt.getPoint());

                if (col == 5 && row >= 0) {
                    String title = (String) topRatedTable.getValueAt(row, 1);
                    showSongActions(title);
                }
            }
        });

        wrapper.add(panel);
        return wrapper;
    }

    private void loadTopRated(DefaultTableModel tableModel) {
        tableModel.setRowCount(0);
        List<Song> topRated = ratingDAO.getTopRatedSongs(20);

        if (topRated.isEmpty()) {
            tableModel.addRow(new Object[] { "", "No rated songs yet!", "", "", "", "" });
        } else {
            int rank = 1;
            for (Song song : topRated) {
                double avgRating = ratingDAO.getAverageSongRating(song.getSongId());

                tableModel.addRow(new Object[] {
                        rank++,
                        song.getTitle(),
                        song.getArtist(),
                        song.getGenre(),
                        String.format("%.1f ⭐", avgRating),
                        "View Details"
                });
            }
        }
    }

    /**
     * Create Profile Panel
     */
    private JPanel createProfilePanel() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(BG_LIGHT);
        wrapper.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel panel = createCardPanel();
        panel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 15, 12, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Profile header
        JLabel profileTitle = new JLabel("User Profile");
        profileTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        profileTitle.setForeground(TEXT_DARK);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(profileTitle, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;

        // Add vertical spacing
        panel.add(Box.createVerticalStrut(20), gbc);
        gbc.gridy++;

        // Username
        JLabel usernameLabel = new JLabel("Name:");
        usernameLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        usernameLabel.setForeground(TEXT_MUTED);
        gbc.gridx = 0;
        panel.add(usernameLabel, gbc);

        JLabel usernameValue = new JLabel(currentUser.getUsername());
        usernameValue.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        usernameValue.setForeground(TEXT_DARK);
        gbc.gridx = 1;
        panel.add(usernameValue, gbc);

        gbc.gridy++;

        // Email
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        emailLabel.setForeground(TEXT_MUTED);
        gbc.gridx = 0;
        panel.add(emailLabel, gbc);

        JLabel emailValue = new JLabel(currentUser.getEmail());
        emailValue.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        emailValue.setForeground(TEXT_DARK);
        gbc.gridx = 1;
        panel.add(emailValue, gbc);

        gbc.gridy++;

        // Member since
        JLabel memberLabel = new JLabel("Member Since:");
        memberLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        memberLabel.setForeground(TEXT_MUTED);
        gbc.gridx = 0;
        panel.add(memberLabel, gbc);

        JLabel memberValue = new JLabel(currentUser.getCreatedAt().toString().substring(0, 10));
        memberValue.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        memberValue.setForeground(TEXT_DARK);
        gbc.gridx = 1;
        panel.add(memberValue, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;

        // Add vertical spacing
        panel.add(Box.createVerticalStrut(30), gbc);
        gbc.gridy++;

        // Statistics section
        JLabel statsTitle = new JLabel("My Statistics");
        statsTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        statsTitle.setForeground(TEXT_DARK);
        panel.add(statsTitle, gbc);

        gbc.gridy++;

        // Favorites count
        int favoritesCount = favoriteDAO.getUserFavorites(currentUser.getUserId()).size();
        JLabel favCountLabel = new JLabel("Favorite Songs: " + favoritesCount);
        favCountLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        favCountLabel.setForeground(TEXT_DARK);
        panel.add(favCountLabel, gbc);

        gbc.gridy++;

        // Add vertical spacing
        panel.add(Box.createVerticalStrut(40), gbc);
        gbc.gridy++;

        // Change password button
        JButton changePasswordBtn = createModernButton("Change Password", PRIMARY);
        changePasswordBtn.setPreferredSize(new Dimension(220, 42));
        changePasswordBtn.addActionListener(e -> changePassword());
        panel.add(changePasswordBtn, gbc);

        wrapper.add(panel);
        return wrapper;
    }

    /**
     * Show song actions dialog with modern styling
     */
    private void showSongActions(String title) {
        List<Song> songs = songDAO.searchSongs(title);
        if (songs.isEmpty())
            return;

        Song song = songs.get(0);

        // Create modern dialog
        JDialog dialog = new JDialog(this, "Song Actions", true);
        dialog.setSize(450, 350);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(0, 0));
        dialog.getContentPane().setBackground(BG_WHITE);

        // Song info panel
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(BG_LIGHT);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JLabel titleLabel = new JLabel("🎵 " + song.getTitle());
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(TEXT_DARK);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(titleLabel);

        infoPanel.add(Box.createVerticalStrut(10));

        JLabel artistLabel = new JLabel("🎤 " + song.getArtist());
        artistLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        artistLabel.setForeground(TEXT_MUTED);
        artistLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(artistLabel);

        infoPanel.add(Box.createVerticalStrut(5));

        JLabel genreLabel = new JLabel("🎸 " + song.getGenre());
        genreLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        genreLabel.setForeground(TEXT_MUTED);
        genreLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(genreLabel);

        infoPanel.add(Box.createVerticalStrut(10));

        double avgRating = ratingDAO.getAverageSongRating(song.getSongId());
        int myRating = ratingDAO.getUserRating(currentUser.getUserId(), song.getSongId());
        JLabel ratingLabel = new JLabel(String.format("⭐ Avg: %.1f | My Rating: %s",
                avgRating, myRating > 0 ? myRating + " ⭐" : "Not rated"));
        ratingLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        ratingLabel.setForeground(TEXT_DARK);
        ratingLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(ratingLabel);

        dialog.add(infoPanel, BorderLayout.NORTH);

        // Action buttons panel
        JPanel actionsPanel = new JPanel(new GridLayout(3, 1, 0, 12));
        actionsPanel.setBackground(BG_WHITE);
        actionsPanel.setBorder(BorderFactory.createEmptyBorder(15, 25, 25, 25));

        JButton rateBtn = createModernButton("⭐ Rate This Song", PRIMARY);
        rateBtn.setPreferredSize(new Dimension(380, 42));
        rateBtn.addActionListener(e -> {
            rateSong(song);
            dialog.dispose();
        });
        actionsPanel.add(rateBtn);

        boolean isFavorite = favoriteDAO.isFavorite(currentUser.getUserId(), song.getSongId());
        JButton favoriteBtn = createModernButton(
                isFavorite ? "💔 Remove from Favorites" : "❤️ Add to Favorites",
                isFavorite ? DANGER : SUCCESS);
        favoriteBtn.setPreferredSize(new Dimension(380, 42));
        favoriteBtn.addActionListener(e -> {
            if (isFavorite) {
                favoriteDAO.removeFromFavorites(currentUser.getUserId(), song.getSongId());
                JOptionPane.showMessageDialog(dialog, "Removed from favorites!");
            } else {
                favoriteDAO.addToFavorites(currentUser.getUserId(), song.getSongId());
                JOptionPane.showMessageDialog(dialog, "Added to favorites!");
            }
            dialog.dispose();
        });
        actionsPanel.add(favoriteBtn);

        JButton closeBtn = createModernButton("Close", new Color(107, 114, 128));
        closeBtn.setPreferredSize(new Dimension(380, 42));
        closeBtn.addActionListener(e -> dialog.dispose());
        actionsPanel.add(closeBtn);

        dialog.add(actionsPanel, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    /**
     * Rate a song
     */
    private void rateSong(Song song) {
        String[] options = { "1 ⭐", "2 ⭐", "3 ⭐", "4 ⭐", "5 ⭐" };
        String selection = (String) JOptionPane.showInputDialog(
                this,
                "Rate \"" + song.getTitle() + "\":",
                "Rate Song",
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[4]);

        if (selection != null) {
            int rating = Integer.parseInt(selection.substring(0, 1));
            if (ratingDAO.addOrUpdateRating(currentUser.getUserId(), song.getSongId(), rating)) {
                JOptionPane.showMessageDialog(this, "Rating saved successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    /**
     * Change password
     */
    private void changePassword() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 15, 15));
        panel.setBackground(BG_WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPasswordField oldPasswordField = new JPasswordField();
        JPasswordField newPasswordField = new JPasswordField();
        JPasswordField confirmPasswordField = new JPasswordField();

        oldPasswordField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        newPasswordField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        confirmPasswordField.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JLabel oldLabel = new JLabel("Current Password:");
        JLabel newLabel = new JLabel("New Password:");
        JLabel confirmLabel = new JLabel("Confirm Password:");

        oldLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        newLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        confirmLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        panel.add(oldLabel);
        panel.add(oldPasswordField);
        panel.add(newLabel);
        panel.add(newPasswordField);
        panel.add(confirmLabel);
        panel.add(confirmPasswordField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Change Password",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String oldPassword = new String(oldPasswordField.getPassword());
            String newPassword = new String(newPasswordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());

            if (newPassword.length() < 6) {
                JOptionPane.showMessageDialog(this, "New password must be at least 6 characters!",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(this, "Passwords do not match!",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            UserDAO userDAO = new UserDAO();
            if (userDAO.changePassword(currentUser.getUserId(), oldPassword, newPassword)) {
                JOptionPane.showMessageDialog(this, "Password changed successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to change password!",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Logout
     */
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?",
                "Logout", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            new LoginScreen();
        }
    }

    /**
     * Create Music Player Panel
     */
    private JPanel createMusicPlayerPanel() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(BG_LIGHT);
        wrapper.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Create music player instance
        MusicPlayer player = new MusicPlayer();

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(BG_LIGHT);

        // Music player at top
        mainPanel.add(player, BorderLayout.NORTH);

        // Song list below
        JPanel listPanel = createCardPanel();
        listPanel.setLayout(new BorderLayout(10, 10));

        JLabel listLabel = new JLabel("📁 Available Songs - Click to Play");
        listLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        listLabel.setForeground(TEXT_DARK);
        listPanel.add(listLabel, BorderLayout.NORTH);

        // Songs table
        String[] columns = { "Title", "Artist", "Genre", "Duration", "▶ Play" };
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4;
            }
        };

        JTable songsTable = new JTable(tableModel);
        styleModernTable(songsTable, PRIMARY);

        // Load songs
        List<Song> songs = songDAO.getAllSongs();
        for (Song song : songs) {
            String duration = String.format("%d:%02d", song.getDuration() / 60, song.getDuration() % 60);
            tableModel.addRow(new Object[] {
                    song.getTitle(),
                    song.getArtist(),
                    song.getGenre(),
                    duration,
                    "▶ Play"
            });
        }

        JScrollPane scrollPane = new JScrollPane(songsTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        listPanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(listPanel, BorderLayout.CENTER);

        // Add click listener to play songs
        songsTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = songsTable.rowAtPoint(evt.getPoint());
                int col = songsTable.columnAtPoint(evt.getPoint());

                if (col == 4 && row >= 0) { // Play column
                    Song selectedSong = songs.get(row);
                    player.playSong(selectedSong);

                    // Log the activity
                    ActivityLogDAO logDAO = new ActivityLogDAO();
                    logDAO.logActivity(
                            "SELECT",
                            "songs",
                            "User played: " + selectedSong.getTitle(),
                            currentUser.getUserId(),
                            null);
                }
            }
        });

        wrapper.add(mainPanel);
        return wrapper;
    }
}
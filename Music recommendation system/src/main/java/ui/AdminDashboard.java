package main.java.ui;

import main.java.model.Admin;
import main.java.model.Song;
import main.java.model.Mood;
import main.java.model.Activity;
import main.java.model.ActivityLog;
import main.java.dao.ActivityLogDAO;
import main.java.dao.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class AdminDashboard extends JFrame {

    private Admin currentAdmin;
    private SongDAO songDAO;
    private MoodDAO moodDAO;
    private ActivityDAO activityDAO;

    // Modern Color Palette
    private static final Color PRIMARY = new Color(99, 102, 241); // Indigo
    private static final Color SECONDARY = new Color(139, 92, 246); // Purple
    private static final Color SUCCESS = new Color(16, 185, 129); // Green
    private static final Color DANGER = new Color(239, 68, 68); // Red
    private static final Color WARNING = new Color(245, 158, 11); // Amber
    private static final Color INFO = new Color(59, 130, 246); // Blue
    private static final Color ADMIN_PRIMARY = new Color(239, 68, 68); // Red for admin
    private static final Color ADMIN_SECONDARY = new Color(220, 38, 38); // Darker red
    private static final Color BG_LIGHT = new Color(249, 250, 251); // Gray-50
    private static final Color BG_WHITE = Color.WHITE;
    private static final Color TEXT_DARK = new Color(17, 24, 39); // Gray-900
    private static final Color TEXT_MUTED = new Color(107, 114, 128); // Gray-500
    private static final Color BORDER_COLOR = new Color(229, 231, 235); // Gray-200

    private JTabbedPane tabbedPane;

    public AdminDashboard(Admin admin) {
        this.currentAdmin = admin;
        this.songDAO = new SongDAO();
        this.moodDAO = new MoodDAO();
        this.activityDAO = new ActivityDAO();

        initializeUI();
    }

    private void initializeUI() {
        setTitle("Music Recommender - Admin Dashboard");
        setSize(1100, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BG_LIGHT);

        // Header
        JPanel headerPanel = createModernHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Tabbed pane
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabbedPane.setBackground(BG_WHITE);
        tabbedPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        tabbedPane.addTab("  Manage Songs  ", createManageSongsPanel());
        tabbedPane.addTab("  Manage Moods  ", createManageMoodsPanel());
        tabbedPane.addTab("  Manage Activities  ", createManageActivitiesPanel());
        tabbedPane.addTab("  Tag Songs  ", createTagSongsPanel());
        tabbedPane.addTab("  Statistics  ", createStatisticsPanel());
        tabbedPane.addTab("  Activity Logs  ", createActivityLogsPanel());

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
                GradientPaint gp = new GradientPaint(0, 0, ADMIN_PRIMARY, w, h, ADMIN_SECONDARY);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
            }
        };
        headerPanel.setPreferredSize(new Dimension(1100, 80));
        headerPanel.setLayout(null);

        JLabel welcomeLabel = new JLabel("Admin Panel - " + currentAdmin.getAdminUsername());
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setBounds(40, 20, 700, 40);
        headerPanel.add(welcomeLabel);

        JButton logoutButton = createModernButton("Logout", new Color(185, 28, 28));
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
        combo.setPreferredSize(new Dimension(300, 36));
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
     * Create Manage Songs Panel
     */
    private JPanel createManageSongsPanel() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(BG_LIGHT);
        wrapper.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel panel = createCardPanel();

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        buttonPanel.setBackground(BG_WHITE);

        JButton addButton = createModernButton("Add Song", SUCCESS);
        addButton.addActionListener(e -> addSong());
        buttonPanel.add(addButton);

        JButton editButton = createModernButton("Edit Song", INFO);
        buttonPanel.add(editButton);

        JButton deleteButton = createModernButton("Delete Song", DANGER);
        buttonPanel.add(deleteButton);

        JButton refreshButton = createModernButton("Refresh", new Color(107, 114, 128));
        buttonPanel.add(refreshButton);

        panel.add(buttonPanel, BorderLayout.NORTH);

        // Songs table
        String[] columns = { "ID", "Title", "Artist", "Genre", "Duration" };
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable songsTable = new JTable(tableModel);
        styleModernTable(songsTable, new Color(52, 73, 94));
        songsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(songsTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        panel.add(scrollPane, BorderLayout.CENTER);

        // Load songs
        loadSongs(tableModel);

        // Button actions
        refreshButton.addActionListener(e -> loadSongs(tableModel));

        editButton.addActionListener(e -> {
            int selectedRow = songsTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a song to edit!",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int songId = (int) tableModel.getValueAt(selectedRow, 0);
            editSong(songId, tableModel);
        });

        deleteButton.addActionListener(e -> {
            int selectedRow = songsTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a song to delete!",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int songId = (int) tableModel.getValueAt(selectedRow, 0);
            String title = (String) tableModel.getValueAt(selectedRow, 1);
            deleteSong(songId, title, tableModel);
        });

        wrapper.add(panel);
        return wrapper;
    }

    /**
     * Load all songs into table
     */
    private void loadSongs(DefaultTableModel tableModel) {
        tableModel.setRowCount(0);
        List<Song> songs = songDAO.getAllSongs();

        for (Song song : songs) {
            String duration = String.format("%d:%02d",
                    song.getDuration() / 60, song.getDuration() % 60);
            tableModel.addRow(new Object[] {
                    song.getSongId(),
                    song.getTitle(),
                    song.getArtist(),
                    song.getGenre(),
                    duration
            });
        }
    }

    /**
     * Add new song
     */
    private void addSong() {
        JPanel panel = new JPanel(new GridLayout(5, 2, 12, 12));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setBackground(BG_WHITE);

        JTextField titleField = createModernTextField();
        JTextField artistField = createModernTextField();
        JTextField genreField = createModernTextField();
        JTextField durationField = createModernTextField();
        JTextField filePathField = createModernTextField();

        panel.add(createLabel("Title:"));
        panel.add(titleField);
        panel.add(createLabel("Artist:"));
        panel.add(artistField);
        panel.add(createLabel("Genre:"));
        panel.add(genreField);
        panel.add(createLabel("Duration (seconds):"));
        panel.add(durationField);
        panel.add(createLabel("File Path:"));
        panel.add(filePathField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add New Song",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                String title = titleField.getText().trim();
                String artist = artistField.getText().trim();
                String genre = genreField.getText().trim();
                int duration = Integer.parseInt(durationField.getText().trim());
                String filePath = filePathField.getText().trim();

                if (title.isEmpty() || artist.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Title and Artist are required!",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Song song = new Song(title, artist, genre, duration, filePath);
                int songId = songDAO.addSong(song);

                if (songId > 0) {
                    JOptionPane.showMessageDialog(this,
                            "Song added successfully! ID: " + songId,
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                    tabbedPane.setComponentAt(0, createManageSongsPanel());
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to add song!",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Duration must be a number!",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Edit song
     */
    private void editSong(int songId, DefaultTableModel tableModel) {
        Song song = songDAO.getSongById(songId);
        if (song == null) {
            JOptionPane.showMessageDialog(this, "Song not found!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JPanel panel = new JPanel(new GridLayout(4, 2, 12, 12));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setBackground(BG_WHITE);

        JTextField titleField = createModernTextField();
        titleField.setText(song.getTitle());
        JTextField artistField = createModernTextField();
        artistField.setText(song.getArtist());
        JTextField genreField = createModernTextField();
        genreField.setText(song.getGenre());
        JTextField durationField = createModernTextField();
        durationField.setText(String.valueOf(song.getDuration()));

        panel.add(createLabel("Title:"));
        panel.add(titleField);
        panel.add(createLabel("Artist:"));
        panel.add(artistField);
        panel.add(createLabel("Genre:"));
        panel.add(genreField);
        panel.add(createLabel("Duration (seconds):"));
        panel.add(durationField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Edit Song",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                song.setTitle(titleField.getText().trim());
                song.setArtist(artistField.getText().trim());
                song.setGenre(genreField.getText().trim());
                song.setDuration(Integer.parseInt(durationField.getText().trim()));

                if (songDAO.updateSong(song)) {
                    JOptionPane.showMessageDialog(this, "Song updated successfully!",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadSongs(tableModel);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update song!",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Duration must be a number!",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Delete song
     */
    private void deleteSong(int songId, String title, DefaultTableModel tableModel) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete \"" + title + "\"?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (songDAO.deleteSong(songId)) {
                JOptionPane.showMessageDialog(this, "Song deleted successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                loadSongs(tableModel);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete song!",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Create Manage Moods Panel
     */
    private JPanel createManageMoodsPanel() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(BG_LIGHT);
        wrapper.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel panel = createCardPanel();

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        buttonPanel.setBackground(BG_WHITE);

        JButton addButton = createModernButton("Add Mood", SUCCESS);
        addButton.addActionListener(e -> addMood());
        buttonPanel.add(addButton);

        JButton refreshButton = createModernButton("Refresh", new Color(107, 114, 128));
        buttonPanel.add(refreshButton);

        panel.add(buttonPanel, BorderLayout.NORTH);

        // Moods table
        String[] columns = { "ID", "Mood Name", "Description" };
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable moodsTable = new JTable(tableModel);
        styleModernTable(moodsTable, SECONDARY);

        JScrollPane scrollPane = new JScrollPane(moodsTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        panel.add(scrollPane, BorderLayout.CENTER);

        // Load moods
        loadMoods(tableModel);

        refreshButton.addActionListener(e -> loadMoods(tableModel));

        wrapper.add(panel);
        return wrapper;
    }

    /**
     * Load all moods
     */
    private void loadMoods(DefaultTableModel tableModel) {
        tableModel.setRowCount(0);
        List<Mood> moods = moodDAO.getAllMoods();

        for (Mood mood : moods) {
            tableModel.addRow(new Object[] {
                    mood.getMoodId(),
                    mood.getMoodName(),
                    mood.getMoodDescription()
            });
        }
    }

    /**
     * Add new mood
     */
    private void addMood() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 12, 12));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setBackground(BG_WHITE);

        JTextField nameField = createModernTextField();
        JTextField descField = createModernTextField();

        panel.add(createLabel("Mood Name:"));
        panel.add(nameField);
        panel.add(createLabel("Description:"));
        panel.add(descField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add New Mood",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            String description = descField.getText().trim();

            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Mood name is required!",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Mood mood = new Mood(name, description);
            int moodId = moodDAO.addMood(mood);

            if (moodId > 0) {
                JOptionPane.showMessageDialog(this, "Mood added successfully! ID: " + moodId,
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                tabbedPane.setComponentAt(1, createManageMoodsPanel());
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add mood!",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Create Manage Activities Panel
     */
    private JPanel createManageActivitiesPanel() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(BG_LIGHT);
        wrapper.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel panel = createCardPanel();

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        buttonPanel.setBackground(BG_WHITE);

        JButton addButton = createModernButton("Add Activity", SUCCESS);
        addButton.addActionListener(e -> addActivity());
        buttonPanel.add(addButton);

        JButton refreshButton = createModernButton("Refresh", new Color(107, 114, 128));
        buttonPanel.add(refreshButton);

        panel.add(buttonPanel, BorderLayout.NORTH);

        // Activities table
        String[] columns = { "ID", "Activity Name", "Description" };
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable activitiesTable = new JTable(tableModel);
        styleModernTable(activitiesTable, INFO);

        JScrollPane scrollPane = new JScrollPane(activitiesTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        panel.add(scrollPane, BorderLayout.CENTER);

        // Load activities
        loadActivities(tableModel);

        refreshButton.addActionListener(e -> loadActivities(tableModel));

        wrapper.add(panel);
        return wrapper;
    }

    /**
     * Load all activities
     */
    private void loadActivities(DefaultTableModel tableModel) {
        tableModel.setRowCount(0);
        List<Activity> activities = activityDAO.getAllActivities();

        for (Activity activity : activities) {
            tableModel.addRow(new Object[] {
                    activity.getActivityId(),
                    activity.getActivityName(),
                    activity.getActivityDescription()
            });
        }
    }

    /**
     * Add new activity
     */
    private void addActivity() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 12, 12));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setBackground(BG_WHITE);

        JTextField nameField = createModernTextField();
        JTextField descField = createModernTextField();

        panel.add(createLabel("Activity Name:"));
        panel.add(nameField);
        panel.add(createLabel("Description:"));
        panel.add(descField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add New Activity",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            String description = descField.getText().trim();

            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Activity name is required!",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Activity activity = new Activity(name, description);
            int activityId = activityDAO.addActivity(activity);

            if (activityId > 0) {
                JOptionPane.showMessageDialog(this, "Activity added successfully! ID: " + activityId,
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                tabbedPane.setComponentAt(2, createManageActivitiesPanel());
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add activity!",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Create Tag Songs Panel
     */
    private JPanel createTagSongsPanel() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(BG_LIGHT);
        wrapper.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel panel = createCardPanel();

        // Info panel
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBackground(new Color(254, 243, 199)); // Amber-100
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(WARNING, 2),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        JLabel infoLabel = new JLabel("<html><b style='font-size:14px'>🏷️ Tag Songs</b><br>" +
                "Assign mood and activity tags to songs for better recommendations</html>");
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        infoLabel.setForeground(TEXT_DARK);
        infoPanel.add(infoLabel);

        panel.add(infoPanel, BorderLayout.NORTH);

        // Center panel
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(BG_WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Select song
        gbc.gridx = 0;
        gbc.gridy = 0;
        centerPanel.add(createLabel("Select Song:"), gbc);

        JComboBox<String> songCombo = createModernComboBox();
        List<Song> songs = songDAO.getAllSongs();
        for (Song song : songs) {
            songCombo.addItem(song.getSongId() + " - " + song.getTitle() + " by " + song.getArtist());
        }
        gbc.gridx = 1;
        centerPanel.add(songCombo, gbc);

        // Select mood
        gbc.gridx = 0;
        gbc.gridy = 1;
        centerPanel.add(createLabel("Select Mood:"), gbc);

        JComboBox<String> moodCombo = createModernComboBox();
        List<Mood> moods = moodDAO.getAllMoods();
        for (Mood mood : moods) {
            moodCombo.addItem(mood.getMoodId() + " - " + mood.getMoodName());
        }
        gbc.gridx = 1;
        centerPanel.add(moodCombo, gbc);

        // Select activity
        gbc.gridx = 0;
        gbc.gridy = 2;
        centerPanel.add(createLabel("Select Activity:"), gbc);

        JComboBox<String> activityCombo = createModernComboBox();
        List<Activity> activities = activityDAO.getAllActivities();
        for (Activity activity : activities) {
            activityCombo.addItem(activity.getActivityId() + " - " + activity.getActivityName());
        }
        gbc.gridx = 1;
        centerPanel.add(activityCombo, gbc);

        // Buttons
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(25, 12, 12, 12);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(BG_WHITE);

        JButton addMoodTagBtn = createModernButton("Add Mood Tag", SECONDARY);
        addMoodTagBtn.addActionListener(e -> {
            if (songCombo.getSelectedItem() != null && moodCombo.getSelectedItem() != null) {
                String songStr = (String) songCombo.getSelectedItem();
                String moodStr = (String) moodCombo.getSelectedItem();

                int songId = Integer.parseInt(songStr.split(" - ")[0]);
                int moodId = Integer.parseInt(moodStr.split(" - ")[0]);

                if (songDAO.addMoodToSong(songId, moodId)) {
                    JOptionPane.showMessageDialog(this, "Mood tag added successfully!",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to add mood tag!",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        buttonPanel.add(addMoodTagBtn);

        JButton addActivityTagBtn = createModernButton("Add Activity Tag", INFO);
        addActivityTagBtn.addActionListener(e -> {
            if (songCombo.getSelectedItem() != null && activityCombo.getSelectedItem() != null) {
                String songStr = (String) songCombo.getSelectedItem();
                String activityStr = (String) activityCombo.getSelectedItem();

                int songId = Integer.parseInt(songStr.split(" - ")[0]);
                int activityId = Integer.parseInt(activityStr.split(" - ")[0]);

                if (songDAO.addActivityToSong(songId, activityId)) {
                    JOptionPane.showMessageDialog(this, "Activity tag added successfully!",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to add activity tag!",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        buttonPanel.add(addActivityTagBtn);

        centerPanel.add(buttonPanel, gbc);

        panel.add(centerPanel, BorderLayout.CENTER);

        wrapper.add(panel);
        return wrapper;
    }

    /**
     * Create Statistics Panel
     */
    private JPanel createStatisticsPanel() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(BG_LIGHT);
        wrapper.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel panel = createCardPanel();
        panel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;

        // Title
        JLabel titleLabel = new JLabel("📊 System Statistics");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(TEXT_DARK);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        gbc.gridy++;
        panel.add(Box.createVerticalStrut(20), gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;

        // Statistics
        List<Song> allSongs = songDAO.getAllSongs();
        List<Mood> allMoods = moodDAO.getAllMoods();
        List<Activity> allActivities = activityDAO.getAllActivities();

        // Create stat cards
        JPanel statsGrid = new JPanel(new GridLayout(1, 3, 20, 0));
        statsGrid.setBackground(BG_WHITE);

        statsGrid.add(createStatCard("🎵", "Total Songs", String.valueOf(allSongs.size()), PRIMARY));
        statsGrid.add(createStatCard("🎭", "Total Moods", String.valueOf(allMoods.size()), SECONDARY));
        statsGrid.add(createStatCard("🎯", "Total Activities", String.valueOf(allActivities.size()), INFO));

        gbc.gridx = 0;
        gbc.gridwidth = 2;
        panel.add(statsGrid, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(40, 15, 15, 15);

        JButton refreshBtn = createModernButton("Refresh Statistics", PRIMARY);
        refreshBtn.setPreferredSize(new Dimension(220, 42));
        refreshBtn.addActionListener(e -> {
            tabbedPane.setSelectedIndex(4);
            tabbedPane.setComponentAt(4, createStatisticsPanel());
        });
        panel.add(refreshBtn, gbc);

        wrapper.add(panel);
        return wrapper;
    }

    /**
     * Create stat card
     */
    private JPanel createStatCard(String icon, String label, String value, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(BG_LIGHT);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(accentColor, 2),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        JLabel iconLabel = new JLabel(icon, SwingConstants.CENTER);
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 40));
        card.add(iconLabel, BorderLayout.NORTH);

        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        textPanel.setBackground(BG_LIGHT);

        JLabel valueLabel = new JLabel(value, SwingConstants.CENTER);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        valueLabel.setForeground(accentColor);

        JLabel labelText = new JLabel(label, SwingConstants.CENTER);
        labelText.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        labelText.setForeground(TEXT_MUTED);

        textPanel.add(valueLabel);
        textPanel.add(labelText);

        card.add(textPanel, BorderLayout.CENTER);

        return card;
    }

    /**
     * Create modern text field
     */
    private JTextField createModernTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setPreferredSize(new Dimension(200, 36));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(5, 12, 5, 12)));
        return field;
    }

    /**
     * Create modern label
     */
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(TEXT_DARK);
        return label;
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
     * Create Activity Logs Panel
     */
    private JPanel createActivityLogsPanel() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(BG_LIGHT);
        wrapper.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel panel = createCardPanel();
        panel.setLayout(new BorderLayout(10, 10));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BG_WHITE);

        JLabel headerLabel = new JLabel("Database Activity Logs");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        headerLabel.setForeground(TEXT_DARK);
        headerPanel.add(headerLabel, BorderLayout.WEST);

        JButton openWindowBtn = createModernButton("Open in New Window", INFO);
        openWindowBtn.addActionListener(e -> new ActivityLogViewer());
        headerPanel.add(openWindowBtn, BorderLayout.EAST);

        panel.add(headerPanel, BorderLayout.NORTH);

        // Mini Activity Log Table
        String[] columns = { "Action", "Table", "Description", "Time" };
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable logsTable = new JTable(tableModel);
        styleModernTable(logsTable, INFO);

        JScrollPane scrollPane = new JScrollPane(logsTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        panel.add(scrollPane, BorderLayout.CENTER);

        // Load recent logs
        ActivityLogDAO logDAO = new ActivityLogDAO();
        List<ActivityLog> logs = logDAO.getAllLogs(50);
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        for (ActivityLog log : logs) {
            tableModel.addRow(new Object[] {
                    log.getActionType(),
                    log.getTableName() != null ? log.getTableName() : "-",
                    log.getDescription(),
                    sdf.format(log.getTimestamp())
            });
        }

        // Refresh button
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(BG_WHITE);

        JButton refreshBtn = createModernButton("Refresh", PRIMARY);
        refreshBtn.addActionListener(e -> {
            tableModel.setRowCount(0);
            List<ActivityLog> newLogs = logDAO.getAllLogs(50);
            for (ActivityLog log : newLogs) {
                tableModel.addRow(new Object[] {
                        log.getActionType(),
                        log.getTableName() != null ? log.getTableName() : "-",
                        log.getDescription(),
                        sdf.format(log.getTimestamp())
                });
            }
        });
        bottomPanel.add(refreshBtn);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        wrapper.add(panel);
        return wrapper;
    }
}
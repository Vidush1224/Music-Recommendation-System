package main.java.ui;

import main.java.dao.ActivityLogDAO;
import main.java.model.ActivityLog;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Activity Log Viewer - Real-time Database Activity Monitor
 * Shows all database actions to admin
 */
public class ActivityLogViewer extends JFrame {

    // Modern Color Palette
    private static final Color PRIMARY = new Color(99, 102, 241);
    private static final Color SUCCESS = new Color(16, 185, 129);
    private static final Color DANGER = new Color(239, 68, 68);
    private static final Color WARNING = new Color(245, 158, 11);
    private static final Color INFO = new Color(59, 130, 246);
    private static final Color BG_LIGHT = new Color(249, 250, 251);
    private static final Color BG_WHITE = Color.WHITE;
    private static final Color TEXT_DARK = new Color(17, 24, 39);
    private static final Color TEXT_MUTED = new Color(107, 114, 128);
    private static final Color BORDER_COLOR = new Color(229, 231, 235);

    private ActivityLogDAO logDAO;
    private DefaultTableModel tableModel;
    private JTable logsTable;
    private Timer refreshTimer;
    private JLabel countLabel; // ✅ Store reference to label
    private JComboBox<String> filterCombo; // ✅ Store reference to combo

    public ActivityLogViewer() {
        this.logDAO = new ActivityLogDAO();
        initializeUI();
        startAutoRefresh();
    }

    private void initializeUI() {
        setTitle("Activity Log Monitor - Database Actions");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BG_LIGHT);

        // Header
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Content panel with card
        JPanel contentPanel = new JPanel(new BorderLayout(15, 15));
        contentPanel.setBackground(BG_LIGHT);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel cardPanel = new JPanel(new BorderLayout(15, 15));
        cardPanel.setBackground(BG_WHITE);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        // Filter panel
        JPanel filterPanel = createFilterPanel();
        cardPanel.add(filterPanel, BorderLayout.NORTH);

        // Table
        createTable();
        JScrollPane scrollPane = new JScrollPane(logsTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        cardPanel.add(scrollPane, BorderLayout.CENTER);

        // Bottom panel with stats
        JPanel bottomPanel = createBottomPanel();
        cardPanel.add(bottomPanel, BorderLayout.SOUTH);

        contentPanel.add(cardPanel, BorderLayout.CENTER);
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        add(mainPanel);

        // Load initial data
        loadLogs("ALL");

        setVisible(true);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                int w = getWidth();
                int h = getHeight();
                GradientPaint gp = new GradientPaint(0, 0, new Color(59, 130, 246), w, h, new Color(147, 51, 234));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
            }
        };
        headerPanel.setPreferredSize(new Dimension(1000, 80));
        headerPanel.setLayout(null);

        JLabel titleLabel = new JLabel("📊 Activity Log Monitor");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(30, 15, 400, 30);
        headerPanel.add(titleLabel);

        JLabel subtitleLabel = new JLabel("Real-time database activity tracking");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitleLabel.setForeground(new Color(255, 255, 255, 200));
        subtitleLabel.setBounds(30, 45, 400, 20);
        headerPanel.add(subtitleLabel);

        // Live indicator
        JPanel livePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        livePanel.setOpaque(false);
        livePanel.setBounds(850, 25, 100, 30);

        JLabel liveIcon = new JLabel("●");
        liveIcon.setFont(new Font("Arial", Font.BOLD, 20));
        liveIcon.setForeground(SUCCESS);

        JLabel liveLabel = new JLabel("LIVE");
        liveLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        liveLabel.setForeground(Color.WHITE);

        livePanel.add(liveIcon);
        livePanel.add(liveLabel);
        headerPanel.add(livePanel);

        return headerPanel;
    }

    private JPanel createFilterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        panel.setBackground(BG_WHITE);

        JLabel filterLabel = new JLabel("Filter by Action:");
        filterLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        filterLabel.setForeground(TEXT_DARK);
        panel.add(filterLabel);

        filterCombo = new JComboBox<>(new String[] {
                "ALL", "INSERT", "UPDATE", "DELETE", "SELECT", "LOGIN"
        });
        filterCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        filterCombo.setPreferredSize(new Dimension(120, 32));
        filterCombo.setBackground(BG_WHITE);
        filterCombo.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        filterCombo.addActionListener(e -> loadLogs((String) filterCombo.getSelectedItem()));
        panel.add(filterCombo);

        JButton refreshBtn = createModernButton("🔄 Refresh", PRIMARY);
        refreshBtn.addActionListener(e -> loadLogs((String) filterCombo.getSelectedItem()));
        panel.add(refreshBtn);

        JButton clearBtn = createModernButton("🗑️ Clear Old Logs", DANGER);
        clearBtn.addActionListener(e -> clearOldLogs());
        panel.add(clearBtn);

        return panel;
    }

    private void createTable() {
        String[] columns = { "ID", "Action", "Table", "Description", "User ID", "Admin ID", "Timestamp" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        logsTable = new JTable(tableModel);
        logsTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        logsTable.setRowHeight(40);
        logsTable.setShowGrid(false);
        logsTable.setIntercellSpacing(new Dimension(0, 0));
        logsTable.setSelectionBackground(new Color(224, 231, 255));

        // Header styling
        logsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        logsTable.getTableHeader().setBackground(INFO);
        logsTable.getTableHeader().setForeground(Color.WHITE);
        logsTable.getTableHeader().setPreferredSize(new Dimension(logsTable.getWidth(), 45));

        // Column widths
        logsTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        logsTable.getColumnModel().getColumn(1).setPreferredWidth(80);
        logsTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        logsTable.getColumnModel().getColumn(3).setPreferredWidth(300);
        logsTable.getColumnModel().getColumn(4).setPreferredWidth(70);
        logsTable.getColumnModel().getColumn(5).setPreferredWidth(70);
        logsTable.getColumnModel().getColumn(6).setPreferredWidth(150);

        // Custom renderer for action column
        logsTable.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (!isSelected) {
                    String action = value.toString();
                    switch (action) {
                        case "INSERT":
                            c.setForeground(SUCCESS);
                            break;
                        case "UPDATE":
                            c.setForeground(INFO);
                            break;
                        case "DELETE":
                            c.setForeground(DANGER);
                            break;
                        case "SELECT":
                            c.setForeground(TEXT_MUTED);
                            break;
                        case "LOGIN":
                            c.setForeground(PRIMARY);
                            break;
                        default:
                            c.setForeground(TEXT_DARK);
                    }
                    c.setBackground(row % 2 == 0 ? BG_WHITE : BG_LIGHT);
                } else {
                    c.setForeground(TEXT_DARK);
                }

                setFont(new Font("Segoe UI", Font.BOLD, 12));
                setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
                return c;
            }
        });

        // Alternating row colors for other columns
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
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
        };

        for (int i = 0; i < logsTable.getColumnCount(); i++) {
            if (i != 1) {
                logsTable.getColumnModel().getColumn(i).setCellRenderer(renderer);
            }
        }
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JLabel infoLabel = new JLabel("Auto-refreshing every 5 seconds");
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        infoLabel.setForeground(TEXT_MUTED);
        panel.add(infoLabel, BorderLayout.WEST);

        // ✅ Create and store reference to count label
        countLabel = new JLabel("Total Logs: 0");
        countLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        countLabel.setForeground(TEXT_DARK);
        panel.add(countLabel, BorderLayout.EAST);

        return panel;
    }

    private void loadLogs(String filter) {
        tableModel.setRowCount(0);
        List<ActivityLog> logs;

        if ("ALL".equals(filter)) {
            logs = logDAO.getAllLogs(100);
        } else {
            logs = logDAO.getLogsByAction(filter, 100);
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        for (ActivityLog log : logs) {
            tableModel.addRow(new Object[] {
                    log.getLogId(),
                    log.getActionType(),
                    log.getTableName() != null ? log.getTableName() : "-",
                    log.getDescription(),
                    log.getUserId() != null ? log.getUserId() : "-",
                    log.getAdminId() != null ? log.getAdminId() : "-",
                    sdf.format(log.getTimestamp())
            });
        }

        // ✅ Update count using stored reference
        updateLogCount(logs.size());
    }

    // ✅ SIMPLIFIED updateLogCount method
    private void updateLogCount(int count) {
        if (countLabel != null) {
            countLabel.setText("Total Logs: " + count);
        }
    }

    private void clearOldLogs() {
        String input = JOptionPane.showInputDialog(this,
                "Delete logs older than how many days?",
                "Clear Old Logs",
                JOptionPane.QUESTION_MESSAGE);

        if (input != null && !input.trim().isEmpty()) {
            try {
                int days = Integer.parseInt(input.trim());
                if (days > 0) {
                    int confirm = JOptionPane.showConfirmDialog(this,
                            "Are you sure you want to delete logs older than " + days + " days?",
                            "Confirm Delete",
                            JOptionPane.YES_NO_OPTION);

                    if (confirm == JOptionPane.YES_OPTION) {
                        if (logDAO.clearOldLogs(days)) {
                            JOptionPane.showMessageDialog(this,
                                    "Old logs cleared successfully!",
                                    "Success",
                                    JOptionPane.INFORMATION_MESSAGE);
                            loadLogs("ALL");
                        }
                    }
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this,
                        "Please enter a valid number!",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void startAutoRefresh() {
        refreshTimer = new Timer(5000, e -> {
            String currentFilter = (String) filterCombo.getSelectedItem();
            loadLogs(currentFilter);
        });
        refreshTimer.start();
    }

    @Override
    public void dispose() {
        if (refreshTimer != null) {
            refreshTimer.stop();
        }
        super.dispose();
    }

    private JButton createModernButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));

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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ActivityLogViewer());
    }
}
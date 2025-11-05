package main.java.ui;

import main.java.dao.UserDAO;
import main.java.dao.AdminDAO;
import main.java.model.User;
import main.java.model.Admin;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

public class LoginScreen extends JFrame {

    private UserDAO userDAO;
    private AdminDAO adminDAO;

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton signupButton;
    private JButton adminLoginButton;
    private JLabel messageLabel;

    public LoginScreen() {
        userDAO = new UserDAO();
        adminDAO = new AdminDAO();
        initializeUI();
    }

    private void initializeUI() {
        setTitle("🎵 Music Recommendation System");
        setSize(500, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                int w = getWidth();
                int h = getHeight();

                Color color1 = new Color(30, 60, 114);
                Color color2 = new Color(42, 82, 152);
                GradientPaint gp = new GradientPaint(0, 0, color1, 0, h, color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
            }
        };
        mainPanel.setLayout(null);

        // Logo/Icon area
        JPanel logoPanel = new JPanel();
        logoPanel.setOpaque(false);
        logoPanel.setBounds(175, 30, 150, 150);
        logoPanel.setLayout(new BorderLayout());

        JLabel iconLabel = new JLabel("🎵", SwingConstants.CENTER);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 80));
        iconLabel.setForeground(Color.WHITE);
        logoPanel.add(iconLabel);
        mainPanel.add(logoPanel);

        // Title
        JLabel titleLabel = new JLabel("Music Recommender", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(50, 190, 400, 40);
        mainPanel.add(titleLabel);

        // Subtitle
        JLabel subtitleLabel = new JLabel("Mood & Activity Adaptive System", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(200, 220, 255));
        subtitleLabel.setBounds(50, 230, 400, 25);
        mainPanel.add(subtitleLabel);

        // Login card panel
        JPanel loginPanel = new JPanel();
        loginPanel.setBackground(Color.WHITE);
        loginPanel.setBounds(50, 280, 400, 280);
        loginPanel.setLayout(null);
        loginPanel.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(15),
                new EmptyBorder(25, 30, 25, 30)));

        // Login title
        JLabel loginTitle = new JLabel("Login to Continue");
        loginTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        loginTitle.setForeground(new Color(44, 62, 80));
        loginTitle.setBounds(30, 15, 340, 30);
        loginPanel.add(loginTitle);

        // Username field with icon
        JLabel userIcon = new JLabel("👤");
        userIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        userIcon.setBounds(35, 65, 25, 35);
        loginPanel.add(userIcon);

        usernameField = new JTextField();
        usernameField.setBounds(65, 65, 305, 40);
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 2),
                new EmptyBorder(5, 15, 5, 15)));
        usernameField.setBackground(new Color(250, 250, 250));
        loginPanel.add(usernameField);

        // Password field with icon
        JLabel passIcon = new JLabel("🔒");
        passIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        passIcon.setBounds(35, 120, 25, 35);
        loginPanel.add(passIcon);

        passwordField = new JPasswordField();
        passwordField.setBounds(65, 120, 305, 40);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 2),
                new EmptyBorder(5, 15, 5, 15)));
        passwordField.setBackground(new Color(250, 250, 250));
        loginPanel.add(passwordField);

        // Login button - IMPROVED STYLING
        loginButton = new JButton("LOGIN");
        loginButton.setBounds(30, 180, 340, 45);
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
        loginButton.setBackground(new Color(52, 152, 219));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setBorderPainted(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.addActionListener(e -> handleUserLogin());

        // Hover effect
        loginButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                loginButton.setBackground(new Color(41, 128, 185));
            }

            public void mouseExited(MouseEvent e) {
                loginButton.setBackground(new Color(52, 152, 219));
            }
        });
        loginPanel.add(loginButton);

        // Signup button
        signupButton = new JButton("Don't have an account? Sign Up");
        signupButton.setBounds(30, 235, 340, 30);
        signupButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        signupButton.setForeground(new Color(52, 152, 219));
        signupButton.setBackground(Color.WHITE);
        signupButton.setFocusPainted(false);
        signupButton.setBorderPainted(false);
        signupButton.setContentAreaFilled(false);
        signupButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        signupButton.addActionListener(e -> openSignupScreen());

        signupButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                signupButton.setForeground(new Color(41, 128, 185));
            }

            public void mouseExited(MouseEvent e) {
                signupButton.setForeground(new Color(52, 152, 219));
            }
        });
        loginPanel.add(signupButton);

        mainPanel.add(loginPanel);

        // Admin Login Button - IMPROVED
        adminLoginButton = new JButton("Admin Portal");
        adminLoginButton.setBounds(150, 580, 200, 40);
        adminLoginButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        adminLoginButton.setBackground(new Color(231, 76, 60));
        adminLoginButton.setForeground(Color.WHITE);
        adminLoginButton.setFocusPainted(false);
        adminLoginButton.setBorderPainted(false);
        adminLoginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        adminLoginButton.addActionListener(e -> handleAdminLogin());

        adminLoginButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                adminLoginButton.setBackground(new Color(192, 57, 43));
            }

            public void mouseExited(MouseEvent e) {
                adminLoginButton.setBackground(new Color(231, 76, 60));
            }
        });
        mainPanel.add(adminLoginButton);

        // Message label
        messageLabel = new JLabel("", SwingConstants.CENTER);
        messageLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        messageLabel.setForeground(Color.WHITE);
        messageLabel.setBounds(50, 565, 400, 20);
        mainPanel.add(messageLabel);

        // Enter key listeners
        KeyAdapter enterKeyListener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    handleUserLogin();
                }
            }
        };
        usernameField.addKeyListener(enterKeyListener);
        passwordField.addKeyListener(enterKeyListener);

        add(mainPanel);
        setVisible(true);
    }

    private void handleUserLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            showMessage("Please enter both username and password!", new Color(231, 76, 60));
            return;
        }

        loginButton.setEnabled(false);
        loginButton.setText("LOGGING IN...");
        loginButton.setBackground(new Color(149, 165, 166));

        SwingWorker<User, Void> worker = new SwingWorker<User, Void>() {
            @Override
            protected User doInBackground() throws Exception {
                return userDAO.loginUser(username, password);
            }

            @Override
            protected void done() {
                try {
                    User user = get();
                    if (user != null) {
                        showMessage("✓ Login successful! Welcome, " + user.getUsername(), new Color(46, 204, 113));

                        Timer timer = new Timer(1000, e -> {
                            dispose();
                            new UserDashboard(user);
                        });
                        timer.setRepeats(false);
                        timer.start();
                    } else {
                        showMessage("✗ Invalid username or password!", new Color(231, 76, 60));
                        loginButton.setEnabled(true);
                        loginButton.setText("LOGIN");
                        loginButton.setBackground(new Color(52, 152, 219));
                    }
                } catch (Exception e) {
                    showMessage("✗ Error: " + e.getMessage(), new Color(231, 76, 60));
                    loginButton.setEnabled(true);
                    loginButton.setText("LOGIN");
                    loginButton.setBackground(new Color(52, 152, 219));
                }
            }
        };
        worker.execute();
    }

    private void handleAdminLogin() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 15));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JTextField adminUsernameField = new JTextField();
        adminUsernameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JPasswordField adminPasswordField = new JPasswordField();
        adminPasswordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JLabel userLabel = new JLabel("Admin Username:");
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));

        JLabel passLabel = new JLabel("Admin Password:");
        passLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));

        panel.add(userLabel);
        panel.add(adminUsernameField);
        panel.add(passLabel);
        panel.add(adminPasswordField);

        int result = JOptionPane.showConfirmDialog(this, panel, "🔐 Admin Login",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String adminUsername = adminUsernameField.getText().trim();
            String adminPassword = new String(adminPasswordField.getPassword());

            if (adminUsername.isEmpty() || adminPassword.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter admin credentials!",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Admin admin = adminDAO.loginAdmin(adminUsername, adminPassword);

            if (admin != null) {
                JOptionPane.showMessageDialog(this,
                        "Admin login successful!\nWelcome, " + admin.getAdminUsername(),
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
                new AdminDashboard(admin);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid admin credentials!",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void openSignupScreen() {
        dispose();
        new SignupScreen();
    }

    private void showMessage(String message, Color color) {
        messageLabel.setText(message);
        messageLabel.setForeground(color);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> new LoginScreen());
    }
}

/**
 * Custom rounded border for modern look
 */
class RoundedBorder implements javax.swing.border.Border {
    private int radius;

    RoundedBorder(int radius) {
        this.radius = radius;
    }

    public Insets getBorderInsets(Component c) {
        return new Insets(this.radius + 1, this.radius + 1, this.radius + 2, this.radius);
    }

    public boolean isBorderOpaque() {
        return true;
    }

    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(230, 230, 230));
        g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
    }
}
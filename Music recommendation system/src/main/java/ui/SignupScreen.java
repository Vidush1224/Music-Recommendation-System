package main.java.ui;

import main.java.dao.UserDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

public class SignupScreen extends JFrame {

    private UserDAO userDAO;

    // Components
    private JTextField usernameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JButton signupButton;
    private JButton backButton;
    private JLabel messageLabel;

    public SignupScreen() {
        userDAO = new UserDAO();
        initializeUI();
    }

    private void initializeUI() {
        setTitle("🎵 Sign Up - Music Recommendation System");
        setSize(500, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Main panel with gradient
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

        // Logo
        JLabel iconLabel = new JLabel("🎵", SwingConstants.CENTER);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 60));
        iconLabel.setForeground(Color.WHITE);
        iconLabel.setBounds(220, 20, 60, 60);
        mainPanel.add(iconLabel);

        // Title
        JLabel titleLabel = new JLabel("Create Account", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 30));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(50, 90, 400, 40);
        mainPanel.add(titleLabel);

        // Subtitle
        JLabel subtitleLabel = new JLabel("Join us to discover personalized music", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(200, 220, 255));
        subtitleLabel.setBounds(50, 130, 400, 25);
        mainPanel.add(subtitleLabel);

        // Signup card panel
        JPanel signupPanel = new JPanel();
        signupPanel.setBackground(Color.WHITE);
        signupPanel.setBounds(50, 180, 400, 480);
        signupPanel.setLayout(null);
        signupPanel.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(15),
                new EmptyBorder(25, 30, 25, 30)));

        // Form title
        JLabel formTitle = new JLabel("Sign Up");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        formTitle.setForeground(new Color(44, 62, 80));
        formTitle.setBounds(30, 15, 340, 30);
        signupPanel.add(formTitle);

        // Username field
        JLabel userIcon = new JLabel("👤");
        userIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        userIcon.setBounds(35, 60, 25, 35);
        signupPanel.add(userIcon);

        usernameField = new JTextField();
        usernameField.setBounds(65, 60, 305, 40);
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 2),
                new EmptyBorder(5, 15, 5, 15)));
        usernameField.setBackground(new Color(250, 250, 250));
        signupPanel.add(usernameField);

        JLabel userHint = new JLabel("Username (min. 3 characters)");
        userHint.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        userHint.setForeground(new Color(127, 140, 141));
        userHint.setBounds(70, 102, 300, 15);
        signupPanel.add(userHint);

        // Email field
        JLabel emailIcon = new JLabel("📧");
        emailIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        emailIcon.setBounds(35, 130, 25, 35);
        signupPanel.add(emailIcon);

        emailField = new JTextField();
        emailField.setBounds(65, 130, 305, 40);
        emailField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        emailField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 2),
                new EmptyBorder(5, 15, 5, 15)));
        emailField.setBackground(new Color(250, 250, 250));
        signupPanel.add(emailField);

        JLabel emailHint = new JLabel("Valid email address");
        emailHint.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        emailHint.setForeground(new Color(127, 140, 141));
        emailHint.setBounds(70, 172, 300, 15);
        signupPanel.add(emailHint);

        // Password field
        JLabel passIcon = new JLabel("🔒");
        passIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        passIcon.setBounds(35, 200, 25, 35);
        signupPanel.add(passIcon);

        passwordField = new JPasswordField();
        passwordField.setBounds(65, 200, 305, 40);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 2),
                new EmptyBorder(5, 15, 5, 15)));
        passwordField.setBackground(new Color(250, 250, 250));
        signupPanel.add(passwordField);

        JLabel passHint = new JLabel("Password (min. 6 characters)");
        passHint.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        passHint.setForeground(new Color(127, 140, 141));
        passHint.setBounds(70, 242, 300, 15);
        signupPanel.add(passHint);

        // Confirm Password field
        JLabel confirmIcon = new JLabel("🔐");
        confirmIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        confirmIcon.setBounds(35, 270, 25, 35);
        signupPanel.add(confirmIcon);

        confirmPasswordField = new JPasswordField();
        confirmPasswordField.setBounds(65, 270, 305, 40);
        confirmPasswordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        confirmPasswordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 2),
                new EmptyBorder(5, 15, 5, 15)));
        confirmPasswordField.setBackground(new Color(250, 250, 250));
        signupPanel.add(confirmPasswordField);

        JLabel confirmHint = new JLabel("Re-enter your password");
        confirmHint.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        confirmHint.setForeground(new Color(127, 140, 141));
        confirmHint.setBounds(70, 312, 300, 15);
        signupPanel.add(confirmHint);

        // Signup button
        signupButton = new JButton("CREATE ACCOUNT");
        signupButton.setBounds(30, 350, 340, 45);
        signupButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
        signupButton.setBackground(new Color(46, 204, 113));
        signupButton.setForeground(Color.WHITE);
        signupButton.setFocusPainted(false);
        signupButton.setBorderPainted(false);
        signupButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        signupButton.addActionListener(e -> handleSignup());

        signupButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                signupButton.setBackground(new Color(39, 174, 96));
            }

            public void mouseExited(MouseEvent e) {
                signupButton.setBackground(new Color(46, 204, 113));
            }
        });
        signupPanel.add(signupButton);

        // Back button
        backButton = new JButton("Already have an account? Login");
        backButton.setBounds(30, 405, 340, 30);
        backButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        backButton.setForeground(new Color(52, 152, 219));
        backButton.setBackground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.setBorderPainted(false);
        backButton.setContentAreaFilled(false);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.addActionListener(e -> backToLogin());

        backButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                backButton.setForeground(new Color(41, 128, 185));
            }

            public void mouseExited(MouseEvent e) {
                backButton.setForeground(new Color(52, 152, 219));
            }
        });
        signupPanel.add(backButton);

        mainPanel.add(signupPanel);

        // Message label
        messageLabel = new JLabel("", SwingConstants.CENTER);
        messageLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        messageLabel.setForeground(Color.WHITE);
        messageLabel.setBounds(50, 670, 400, 25);
        mainPanel.add(messageLabel);

        // Enter key listener
        KeyAdapter enterKeyListener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    handleSignup();
                }
            }
        };
        usernameField.addKeyListener(enterKeyListener);
        emailField.addKeyListener(enterKeyListener);
        passwordField.addKeyListener(enterKeyListener);
        confirmPasswordField.addKeyListener(enterKeyListener);

        add(mainPanel);
        setVisible(true);
    }

    private void handleSignup() {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        // Validation
        if (username.isEmpty() || email.isEmpty() ||
                password.isEmpty() || confirmPassword.isEmpty()) {
            showMessage("✗ All fields are required!", new Color(231, 76, 60));
            return;
        }

        if (username.length() < 3) {
            showMessage("✗ Username must be at least 3 characters!", new Color(231, 76, 60));
            return;
        }

        if (!isValidEmail(email)) {
            showMessage("✗ Invalid email format!", new Color(231, 76, 60));
            return;
        }

        if (password.length() < 6) {
            showMessage("✗ Password must be at least 6 characters!", new Color(231, 76, 60));
            return;
        }

        if (!password.equals(confirmPassword)) {
            showMessage("✗ Passwords do not match!", new Color(231, 76, 60));
            return;
        }

        if (userDAO.usernameExists(username)) {
            showMessage("✗ Username already taken!", new Color(231, 76, 60));
            return;
        }

        if (userDAO.emailExists(email)) {
            showMessage("✗ Email already registered!", new Color(231, 76, 60));
            return;
        }

        signupButton.setEnabled(false);
        signupButton.setText("CREATING ACCOUNT...");
        signupButton.setBackground(new Color(149, 165, 166));

        SwingWorker<Integer, Void> worker = new SwingWorker<Integer, Void>() {
            @Override
            protected Integer doInBackground() throws Exception {
                return userDAO.registerUser(username, email, password);
            }

            @Override
            protected void done() {
                try {
                    int userId = get();
                    if (userId > 0) {
                        showMessage("✓ Account created successfully! Redirecting...", new Color(46, 204, 113));

                        Timer timer = new Timer(2000, e -> backToLogin());
                        timer.setRepeats(false);
                        timer.start();
                    } else {
                        showMessage("✗ Registration failed! Please try again.", new Color(231, 76, 60));
                        signupButton.setEnabled(true);
                        signupButton.setText("CREATE ACCOUNT");
                        signupButton.setBackground(new Color(46, 204, 113));
                    }
                } catch (Exception e) {
                    showMessage("✗ Error: " + e.getMessage(), new Color(231, 76, 60));
                    signupButton.setEnabled(true);
                    signupButton.setText("CREATE ACCOUNT");
                    signupButton.setBackground(new Color(46, 204, 113));
                }
            }
        };
        worker.execute();
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }

    private void backToLogin() {
        dispose();
        new LoginScreen();
    }

    private void showMessage(String message, Color color) {
        messageLabel.setText(message);
        messageLabel.setForeground(color);
    }
}
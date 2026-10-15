package ui;

import manager.LibraryManager;
import models.Student;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// Dialog window for user authentication
public class LoginDialog extends JDialog {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton cancelButton;
    private boolean succeeded;
    private Student authenticatedStudent;
    private boolean isAdmin;
    private LibraryManager manager;

    public LoginDialog(Frame parent) {
        super(parent, "ISSAE Library Login", true);
        this.manager = LibraryManager.getInstance();

        JPanel mainContentPanel = new JPanel(new BorderLayout());
        mainContentPanel.setBackground(Color.WHITE);

        // Input fields panel
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints cs = new GridBagConstraints();
        cs.insets = new Insets(10, 10, 10, 10);
        cs.fill = GridBagConstraints.HORIZONTAL;
        cs.weightx = 1.0;

        JLabel lbUsername = new JLabel("Username");
        lbUsername.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbUsername.setForeground(Color.GRAY);
        cs.gridx = 0;
        cs.gridy = 0;
        cs.gridwidth = 1;
        panel.add(lbUsername, cs);

        usernameField = new JTextField();
        usernameField.setPreferredSize(new Dimension(usernameField.getPreferredSize().width, 40));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        cs.gridx = 0;
        cs.gridy = 1;
        cs.gridwidth = 2;
        panel.add(usernameField, cs);

        JLabel lbPassword = new JLabel("Password");
        lbPassword.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbPassword.setForeground(Color.GRAY);
        cs.gridx = 0;
        cs.gridy = 2;
        cs.gridwidth = 1;
        panel.add(lbPassword, cs);

        passwordField = new JPasswordField();
        passwordField.setPreferredSize(new Dimension(passwordField.getPreferredSize().width, 40));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        cs.gridx = 0;
        cs.gridy = 3;
        cs.gridwidth = 2;
        panel.add(passwordField, cs);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));

        // Buttons
        loginButton = new JButton("LOGIN");
        loginButton.setBackground(new Color(226, 0, 26));
        loginButton.setOpaque(true);
        loginButton.setBorderPainted(false);
        loginButton.setContentAreaFilled(true);
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.setPreferredSize(new Dimension(loginButton.getPreferredSize().width, 45));
        loginButton.setBorder(BorderFactory.createEmptyBorder());

        cancelButton = new JButton("Cancel");
        cancelButton.setBackground(Color.WHITE);
        cancelButton.setOpaque(true);
        cancelButton.setContentAreaFilled(true);
        cancelButton.setBorderPainted(true);
        cancelButton.setForeground(new Color(226, 0, 26));
        cancelButton.setFocusPainted(false);
        cancelButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cancelButton.setPreferredSize(new Dimension(cancelButton.getPreferredSize().width, 45));
        cancelButton.setBorder(BorderFactory.createLineBorder(new Color(226, 0, 26)));

        JPanel bp = new JPanel(new GridBagLayout());
        bp.setOpaque(false);
        GridBagConstraints bpc = new GridBagConstraints();
        bpc.gridx = 0;
        bpc.gridy = 0;
        bpc.fill = GridBagConstraints.HORIZONTAL;
        bpc.weightx = 1.0;
        bpc.insets = new Insets(10, 30, 10, 30);
        bp.add(loginButton, bpc);
        bpc.gridy = 1;
        bpc.insets = new Insets(0, 30, 20, 30);
        bp.add(cancelButton, bpc);

        // Header Panel with Logo
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(new Color(226, 0, 26));
        headerPanel.setPreferredSize(new Dimension(0, 120));
        
        JLabel logoLabel = new JLabel();
        try {
            java.net.URL logoURL = getClass().getResource("/resources/logomain.png");
            if (logoURL != null) {
                ImageIcon logoIcon = new ImageIcon(logoURL);
                logoLabel.setIcon(logoIcon);
            } else {
                logoLabel.setText("le cnam");
                logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
                logoLabel.setForeground(Color.WHITE);
            }
        } catch (Exception e) {
            logoLabel.setText("le cnam");
            logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
            logoLabel.setForeground(Color.WHITE);
        }
        logoLabel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        headerPanel.add(logoLabel);

        mainContentPanel.add(headerPanel, BorderLayout.NORTH);
        mainContentPanel.add(panel, BorderLayout.CENTER);
        mainContentPanel.add(bp, BorderLayout.SOUTH);

        getContentPane().add(mainContentPanel);

        pack();
        setSize(450, 450);
        setLocationRelativeTo(parent);

        // Login button listener
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                // Admin check
                if ("admin".equals(username) && "admin".equals(password)) {
                    isAdmin = true;
                    succeeded = true;
                    dispose();
                } else {
                    // Student check
                    authenticatedStudent = manager.getStudents().stream()
                            .filter(s -> s.getUsername().equals(username) && s.getPassword().equals(password))
                            .findFirst().orElse(null);

                    if (authenticatedStudent != null) {
                        isAdmin = false;
                        succeeded = true;
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(LoginDialog.this,
                                "Invalid username or password",
                                "Login Error",
                                JOptionPane.ERROR_MESSAGE);
                        usernameField.setText("");
                        passwordField.setText("");
                    }
                }
            }
        });

        // Cancel button listener
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                succeeded = false;
                dispose();
            }
        });
    }

    public boolean isSucceeded() { return succeeded; }
    public boolean isAdmin() { return isAdmin; }
    public Student getAuthenticatedStudent() { return authenticatedStudent; }
}

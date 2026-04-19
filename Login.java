package DEVPATHCONNECTIVITY;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class Login extends JFrame {

    private JTextField emailField;
    private JPasswordField passwordField;

    public Login() {
        setTitle("DevPath - Login");
        setSize(800, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        JPanel mainPanel = new JPanel(new GridLayout(1, 2));

        // ===== LEFT PANEL =====
        JPanel left = new JPanel();
        left.setBackground(new Color(52, 152, 219));
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setBorder(BorderFactory.createEmptyBorder(60, 40, 60, 40));

        JLabel title = new JLabel("DevPath");
        title.setFont(new Font("Segoe UI", Font.BOLD, 32));
        title.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel("<html>Welcome back!<br>Login to continue</html>");
        subtitle.setForeground(Color.WHITE);

        left.add(Box.createVerticalGlue());
        left.add(title);
        left.add(Box.createVerticalStrut(20));
        left.add(subtitle);
        left.add(Box.createVerticalGlue());

        // ===== RIGHT PANEL (FIXED ALIGNMENT) =====
        JPanel right = new JPanel(new GridBagLayout());
        right.setBackground(new Color(245, 247, 250));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST; // ✅ FIX: LEFT align
        gbc.insets = new Insets(20, 20, 20, 20);

        JPanel card = new JPanel();
        card.setPreferredSize(new Dimension(350, 300));
        card.setBackground(Color.WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));

        JLabel loginTitle = new JLabel("Login");
        loginTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        loginTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        emailField = new JTextField();
        passwordField = new JPasswordField();

        JButton loginBtn = new JButton("Login");
        JButton registerBtn = new JButton("Register");

        styleButton(loginBtn, new Color(52, 152, 219));
        styleButton(registerBtn, new Color(46, 204, 113));

        // ===== ACTIONS =====
        loginBtn.addActionListener(e -> handleLogin());
        registerBtn.addActionListener(e -> {
            new Register();
            dispose();
        });

        // ✅ ENTER KEY LOGIN
        getRootPane().setDefaultButton(loginBtn);

        // ===== BUTTON PANEL =====
        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        btnPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btnPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnPanel.setBackground(Color.WHITE);

        btnPanel.add(loginBtn);
        btnPanel.add(registerBtn);

        // ===== ADD COMPONENTS =====
        card.add(loginTitle);
        card.add(Box.createVerticalStrut(15));

        addField(card, "Email", emailField);
        addField(card, "Password", passwordField);

        card.add(Box.createVerticalStrut(20));
        card.add(btnPanel);

        right.add(card, gbc);

        mainPanel.add(left);
        mainPanel.add(right);
        add(mainPanel);

        setVisible(true);
    }

    // ===== LOGIN LOGIC =====
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields required!");
            return;
        }

        try (Connection con = DevPathDB.getConnection()) {

            PreparedStatement ps = con.prepareStatement(
                    "SELECT * FROM User WHERE email=? AND password=?"
            );

            ps.setString(1, email);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int id = rs.getInt("user_id");
                String role = rs.getString("role");

                dispose();

                if ("faculty".equalsIgnoreCase(role)) {
                    new FacultyMenu(id);
                } else {
                    new StudentMenu(id);
                }

            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "DB Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ===== FIELD CREATION =====
    private void addField(JPanel panel, String labelText, JComponent field) {

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setAlignmentX(Component.LEFT_ALIGNMENT);
        wrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        wrapper.setBackground(Color.WHITE);

        field.setPreferredSize(new Dimension(300, 40));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        wrapper.add(field, BorderLayout.CENTER);

        panel.add(label);
        panel.add(Box.createVerticalStrut(5));
        panel.add(wrapper);
        panel.add(Box.createVerticalStrut(15));
    }

    // ===== BUTTON STYLE =====
    private void styleButton(JButton b, Color c) {
        b.setBackground(c);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    // ===== MAIN METHOD =====
    public static void main(String[] args) {
        SwingUtilities.invokeLater(Login::new);
    }
}
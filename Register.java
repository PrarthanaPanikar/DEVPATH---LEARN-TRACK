package DEVPATHCONNECTIVITY;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.*;

public class Register extends JFrame {

    private JTextField nameField, emailField;
    private JPasswordField passField;
    private JComboBox<String> roleBox, companyBox;

    public Register() {
        setTitle("DevPath - Register");
        setSize(850, 550); // Increased height slightly
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        JPanel mainPanel = new JPanel(new GridLayout(1, 2));

        // ===== LEFT PANEL =====
        JPanel left = new JPanel();
        left.setBackground(new Color(46, 204, 113));
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setBorder(new EmptyBorder(60, 40, 60, 40));

        JLabel title = new JLabel("DevPath");
        title.setFont(new Font("Segoe UI", Font.BOLD, 32));
        title.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel("<html>Create your account<br>Start your journey</html>");
        subtitle.setForeground(Color.WHITE);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        left.add(Box.createVerticalGlue());
        left.add(title);
        left.add(Box.createVerticalStrut(20));
        left.add(subtitle);
        left.add(Box.createVerticalGlue());

        // ===== RIGHT PANEL =====
        JPanel right = new JPanel(new GridBagLayout());
        right.setBackground(new Color(245, 247, 250));

        JPanel card = new JPanel();
        // 🔥 INCREASED HEIGHT HERE (From 420 to 480)
        card.setPreferredSize(new Dimension(400, 480)); 
        card.setBackground(Color.WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(20, 25, 20, 25));

        JLabel regTitle = new JLabel("Register");
        regTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        regTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        // ===== FIELDS =====
        nameField = new JTextField();
        emailField = new JTextField();
        passField = new JPasswordField();
        roleBox = new JComboBox<>(new String[]{"Student", "Faculty"});
        companyBox = new JComboBox<>();

        // LOAD COMPANIES FROM DB
        try (Connection con = DevPathDB.getConnection()) {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT company_id, name FROM company");
            while (rs.next()) {
                companyBox.addItem(rs.getInt("company_id") + "-" + rs.getString("name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // ===== BUTTONS =====
        JButton registerBtn = new JButton("Create Account");
        JButton backBtn = new JButton("Back to Login");

        styleButton(registerBtn, new Color(46, 204, 113));
        styleButton(backBtn, new Color(231, 76, 60));

        registerBtn.addActionListener(e -> registerUser());
        backBtn.addActionListener(e -> {
            new Login();
            dispose();
        });

        // ===== ADD COMPONENTS =====
        card.add(regTitle);
        card.add(Box.createVerticalStrut(10));

        addField(card, "Name", nameField);
        addField(card, "Email", emailField);
        addField(card, "Password", passField);
        addField(card, "Role", roleBox);
        addField(card, "Company", companyBox);

        card.add(Box.createVerticalStrut(10));
        card.add(registerBtn);
        card.add(Box.createVerticalStrut(10));
        card.add(backBtn);

        right.add(card);

        mainPanel.add(left);
        mainPanel.add(right);

        add(mainPanel);
        setVisible(true);
    }

    private void addField(JPanel panel, String labelText, JComponent field) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Fixed wrapper to prevent disappearing
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setAlignmentX(Component.LEFT_ALIGNMENT);
        wrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35)); // Slimmer fields
        wrapper.setBackground(Color.WHITE);

        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        if (field instanceof JComboBox) {
            field.setPreferredSize(new Dimension(300, 35));
        }

        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(2, 10, 2, 10)
        ));

        wrapper.add(field, BorderLayout.CENTER);

        panel.add(label);
        panel.add(Box.createVerticalStrut(3));
        panel.add(wrapper);
        panel.add(Box.createVerticalStrut(10));
    }

    private void styleButton(JButton b, Color c) {
        b.setBackground(c);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
    }

    // (registerUser() method remains the same as your original)
    private void registerUser() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String pass = new String(passField.getPassword()).trim();
        String role = (String) roleBox.getSelectedItem();

        if (name.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields required!");
            return;
        }

        try (Connection con = DevPathDB.getConnection()) {
            PreparedStatement check = con.prepareStatement("SELECT email FROM User WHERE email=?");
            check.setString(1, email);
            if (check.executeQuery().next()) {
                JOptionPane.showMessageDialog(this, "Email already exists!");
                return;
            }

            int companyId = Integer.parseInt(((String) companyBox.getSelectedItem()).split("-")[0]);

            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO User(name,email,password,role,company_id) VALUES(?,?,?,?,?)"
            );
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, pass);
            ps.setString(4, role);
            ps.setInt(5, companyId);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Registered Successfully!");
            new Login();
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "DB Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Register::new);
    }
}
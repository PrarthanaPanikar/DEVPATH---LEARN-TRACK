package DEVPATHCONNECTIVITY;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.*;

public class FacultyMenu extends JFrame {

    private int facultyId;
    private JPanel coursePanel;

    public FacultyMenu(int facultyId) {
        this.facultyId = facultyId;

        setTitle("Faculty Dashboard");
        setSize(900, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 247, 250));

        // ===== TOP BAR =====
        JPanel topBar = new JPanel();
        topBar.setBackground(Color.WHITE);
        topBar.setBorder(new EmptyBorder(10, 10, 10, 10));

        JButton addBtn = new JButton("Add");
        JButton updateBtn = new JButton("Update");
        JButton deleteBtn = new JButton("Delete");
        JButton refreshBtn = new JButton("Refresh");

        styleButton(addBtn, new Color(46, 204, 113));
        styleButton(updateBtn, new Color(52, 152, 219));
        styleButton(deleteBtn, new Color(231, 76, 60));
        styleButton(refreshBtn, new Color(155, 89, 182));

        topBar.add(addBtn);
        topBar.add(updateBtn);
        topBar.add(deleteBtn);
        topBar.add(refreshBtn);

        mainPanel.add(topBar, BorderLayout.NORTH);

        // ===== COURSE PANEL =====
        coursePanel = new JPanel(new GridLayout(0, 2, 15, 15));
        coursePanel.setBackground(new Color(245, 247, 250));
        coursePanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JScrollPane scroll = new JScrollPane(coursePanel);
        scroll.setBorder(null);

        mainPanel.add(scroll, BorderLayout.CENTER);
        add(mainPanel);

        // ===== ACTIONS =====
        addBtn.addActionListener(e -> {
            addCourse();
            loadCourses();
        });

        updateBtn.addActionListener(e -> {
            updateCourse();
            loadCourses();
        });

        deleteBtn.addActionListener(e -> {
            deleteCourse();
            loadCourses();
        });

        refreshBtn.addActionListener(e -> loadCourses());

        loadCourses();

        setVisible(true);
    }

    // ================= LOAD COURSES =================
    private void loadCourses() {
        coursePanel.removeAll();

        try (Connection con = DevPathDB.getConnection()) {

            PreparedStatement ps = con.prepareStatement(
                "SELECT * FROM course WHERE created_by=?"
            );
            ps.setInt(1, facultyId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("course_id");
                String title = rs.getString("title");
                String difficulty = rs.getString("difficulty");
                int hours = rs.getInt("hours");

                coursePanel.add(createCourseCard(id, title, difficulty, hours));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        coursePanel.revalidate();
        coursePanel.repaint();
    }

    // ================= CARD =================
    private JPanel createCourseCard(int id, String title, String difficulty, int hours) {

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));

        JLabel diffLabel = new JLabel("Difficulty: " + difficulty);
        JLabel hourLabel = new JLabel("Hours: " + hours);
        JLabel idLabel = new JLabel("Course ID: " + id);

        card.add(titleLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(diffLabel);
        card.add(hourLabel);
        card.add(idLabel);

        return card;
    }

    // ================= STYLE =================
    private void styleButton(JButton b, Color c) {
        b.setBackground(c);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    // ================= ADD COURSE =================
    private void addCourse() {
        JTextField titleField = new JTextField();
        JTextField hoursField = new JTextField();
        JComboBox<String> difficultyBox = new JComboBox<>(new String[]{"Easy", "Medium", "Hard"});
        JComboBox<String> companyBox = new JComboBox<>();

        // Fill the company dropdown
        try (Connection con = DevPathDB.getConnection()) {
            ResultSet rs = con.createStatement().executeQuery("SELECT company_id, name FROM company");
            while (rs.next()) {
                companyBox.addItem(rs.getInt("company_id") + " - " + rs.getString("name"));
            }
        } catch (Exception e) { e.printStackTrace(); }

        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        panel.add(new JLabel("Course Title:")); panel.add(titleField);
        panel.add(new JLabel("Difficulty:")); panel.add(difficultyBox);
        panel.add(new JLabel("Hours:")); panel.add(hoursField);
        panel.add(new JLabel("Select Company:")); panel.add(companyBox);

        if (JOptionPane.showConfirmDialog(this, panel, "Add New Course", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try (Connection con = DevPathDB.getConnection()) {
                int compId = Integer.parseInt(((String) companyBox.getSelectedItem()).split(" - ")[0]);

                // Note: We are now using company_id directly
                PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO course(title, difficulty, hours, created_by, company_id) VALUES(?,?,?,?,?)"
                );
                ps.setString(1, titleField.getText().trim());
                ps.setString(2, (String) difficultyBox.getSelectedItem());
                ps.setInt(3, Integer.parseInt(hoursField.getText().trim()));
                ps.setInt(4, facultyId); 
                ps.setInt(5, compId);

                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Course Added Successfully!");
                loadCourses(); 
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }
    // ================= UPDATE =================
    private void updateCourse() {
        String idStr = JOptionPane.showInputDialog(this, "Enter Course ID to Update:");
        if (idStr == null || idStr.trim().isEmpty()) return;

        try {
            int courseId = Integer.parseInt(idStr.trim());
            JTextField titleField = new JTextField();
            JTextField hoursField = new JTextField();
            JComboBox<String> companyBox = new JComboBox<>();

            try (Connection con = DevPathDB.getConnection()) {
                // Load companies
                ResultSet rsComp = con.createStatement().executeQuery("SELECT company_id, name FROM company");
                while (rsComp.next()) companyBox.addItem(rsComp.getInt("company_id") + " - " + rsComp.getString("name"));

                // Load existing data
                PreparedStatement ps = con.prepareStatement("SELECT * FROM course WHERE course_id=?");
                ps.setInt(1, courseId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    titleField.setText(rs.getString("title"));
                    hoursField.setText(String.valueOf(rs.getInt("hours")));
                } else {
                    JOptionPane.showMessageDialog(this, "Course not found!");
                    return;
                }
            }

            JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
            panel.add(new JLabel("Title:")); panel.add(titleField);
            panel.add(new JLabel("Hours:")); panel.add(hoursField);
            panel.add(new JLabel("New Company:")); panel.add(companyBox);

            if (JOptionPane.showConfirmDialog(this, panel, "Update Course", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                int newCompId = Integer.parseInt(((String) companyBox.getSelectedItem()).split(" - ")[0]);
                try (Connection con = DevPathDB.getConnection()) {
                    PreparedStatement psUp = con.prepareStatement("UPDATE course SET title=?, hours=?, company_id=? WHERE course_id=?");
                    psUp.setString(1, titleField.getText());
                    psUp.setInt(2, Integer.parseInt(hoursField.getText()));
                    psUp.setInt(3, newCompId);
                    psUp.setInt(4, courseId);
                    psUp.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Update successful!");
                    loadCourses();
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
    }
    // ================= DELETE =================
    private void deleteCourse() {
        String idStr = JOptionPane.showInputDialog(this, "Enter Course ID to Delete:");
        if (idStr == null) return;

        try (Connection con = DevPathDB.getConnection()) {
            int cid = Integer.parseInt(idStr.trim());
            PreparedStatement ps = con.prepareStatement(
                "SELECT c.title, co.name FROM course c JOIN company co ON c.company_id = co.company_id WHERE c.course_id=?"
            );
            ps.setInt(1, cid);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int confirm = JOptionPane.showConfirmDialog(this, "Delete " + rs.getString("title") + " from " + rs.getString("name") + "?");
                if (confirm == JOptionPane.YES_OPTION) {
                    con.createStatement().executeUpdate("DELETE FROM course WHERE course_id=" + cid);
                    JOptionPane.showMessageDialog(this, "Deleted!");
                    loadCourses();
                }
            } else {
                JOptionPane.showMessageDialog(this, "ID not found!");
            }
        } catch (Exception e) { e.printStackTrace(); }
    }
}
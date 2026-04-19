package DEVPATHCONNECTIVITY;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.*;

public class StudentMenu extends JFrame {

    private int userId;
    private JPanel coursePanel;

    public StudentMenu(int userId) {
        this.userId = userId;

        setTitle("Student Dashboard");
        setSize(900, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 247, 250));

        // ===== HEADER =====
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(52, 152, 219));
        header.setBorder(new EmptyBorder(10, 20, 10, 20));

        JLabel title = new JLabel("Student Dashboard");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));

        JButton logoutBtn = new JButton("Logout");
        styleButton(logoutBtn, new Color(231, 76, 60));

        logoutBtn.addActionListener(e -> {
            new Login();
            dispose();
        });

        header.add(title, BorderLayout.WEST);
        header.add(logoutBtn, BorderLayout.EAST);

        // ===== BUTTON PANEL =====
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        buttonPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
        buttonPanel.setBackground(new Color(245, 247, 250));

        JButton viewBtn = new JButton("View Courses");
        JButton completeBtn = new JButton("Mark Complete");
        JButton progressBtn = new JButton("View Progress");

        styleButton(viewBtn, new Color(52, 152, 219));
        styleButton(completeBtn, new Color(46, 204, 113));
        styleButton(progressBtn, new Color(155, 89, 182));

        buttonPanel.add(viewBtn);
        buttonPanel.add(completeBtn);
        buttonPanel.add(progressBtn);

        // ===== TOP WRAPPER (HEADER + BUTTONS) =====
        JPanel topWrapper = new JPanel(new BorderLayout());
        topWrapper.add(header, BorderLayout.NORTH);
        topWrapper.add(buttonPanel, BorderLayout.SOUTH);

        mainPanel.add(topWrapper, BorderLayout.NORTH);

        // ===== COURSE PANEL =====
        coursePanel = new JPanel(new GridLayout(0, 2, 15, 15));
        coursePanel.setBackground(new Color(245, 247, 250));
        coursePanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JScrollPane scroll = new JScrollPane(coursePanel);
        scroll.setBorder(null);

        mainPanel.add(scroll, BorderLayout.CENTER);

        add(mainPanel);

        // ===== ACTIONS =====
        viewBtn.addActionListener(e -> loadCourses());
        completeBtn.addActionListener(e -> markComplete());
        progressBtn.addActionListener(e -> showProgress());

        setVisible(true);
    }

    // ===================== LOAD COURSES (UPDATED QUERY) =====================
    private void loadCourses() {
        coursePanel.removeAll();

        // The subquery ensures we only fetch courses that belong to the 
        // exact company linked to the logged-in student.
        String query = "SELECT c.* FROM course c " +
                       "JOIN phase p ON c.phase_id = p.phase_id " +
                       "JOIN roadmap r ON p.roadmap_id = r.roadmap_id " +
                       "WHERE r.company_id = (SELECT company_id FROM user WHERE user_id = ?)";

        try (Connection con = DevPathDB.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            boolean found = false;

            while (rs.next()) {
                found = true;

                int id = rs.getInt("course_id");
                String title = rs.getString("title");
                String difficulty = rs.getString("difficulty");
                int hours = rs.getInt("hours");

                // Adds the card using your existing UI helper method
                coursePanel.add(createCourseCard(id, title, difficulty, hours));
            }

            // Keeps your UI feedback for when no courses exist for that specific company
            if (!found) {
                JLabel noCourseLabel = new JLabel("No courses available for your company");
                noCourseLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
                coursePanel.add(noCourseLabel);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading courses: " + e.getMessage());
            e.printStackTrace();
        }

        // Refresh the panel to show the new cards
        coursePanel.revalidate();
        coursePanel.repaint();
    }    // ===================== CARD UI =====================
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

        JButton completeBtn = new JButton("Complete");
        styleButton(completeBtn, new Color(46, 204, 113));

        completeBtn.addActionListener(e -> markCompleteById(id));

        card.add(titleLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(diffLabel);
        card.add(hourLabel);
        card.add(idLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(completeBtn);

        return card;
    }

    // ===================== MARK COMPLETE =====================
    private void markComplete() {
        try {
            int courseId = Integer.parseInt(
                    JOptionPane.showInputDialog("Enter Course ID:")
            );
            markCompleteById(courseId);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid input!");
        }
    }

    private void markCompleteById(int courseId) {
        try (Connection con = DevPathDB.getConnection()) {

            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO Progress(user_id, course_id, status) VALUES(?,?,?)"
            );

            ps.setInt(1, userId);
            ps.setInt(2, courseId);
            ps.setString(3, "Completed");

            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Marked as Completed!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ===================== PROGRESS =====================
    private void showProgress() {
        try (Connection con = DevPathDB.getConnection()) {

            PreparedStatement ps = con.prepareStatement(
                    "SELECT COUNT(*) AS total FROM Progress WHERE user_id=?"
            );

            ps.setInt(1, userId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Completed Courses: " + rs.getInt("total"),
                        "Progress",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ===================== BUTTON STYLE =====================
    private void styleButton(JButton b, Color c) {
        b.setBackground(c);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}
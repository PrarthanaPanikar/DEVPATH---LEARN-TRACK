package DEVPATHCONNECTIVITY;

import java.util.Scanner;
import java.sql.*;

public class DevPath {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n--- Welcome ---");
            System.out.println("1. Login (Console)");
            System.out.println("2. Open Login Window (GUI)");
            System.out.println("3. Exit");

            System.out.print("Enter choice: ");
            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    System.out.print("Enter Email: ");
                    String email = sc.nextLine();

                    System.out.print("Enter Password: ");
                    String password = sc.nextLine();

                    try (Connection con = DevPathDB.getConnection()) {

                        PreparedStatement ps = con.prepareStatement(
                            "SELECT * FROM User WHERE email=? AND password=?"
                        );

                        ps.setString(1, email);
                        ps.setString(2, password);

                        ResultSet rs = ps.executeQuery();

                        if (rs.next()) {
                            int userId = rs.getInt("user_id");
                            String role = rs.getString("role");

                            System.out.println("Login Successful!");

                            if (role.equalsIgnoreCase("faculty")) {
                                new FacultyMenu(userId);
                            } else {
                                new StudentMenu(userId);
                            }

                        } else {
                            System.out.println("Invalid Email or Password");
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

                case 2:
                    new Login(); // GUI login
                    break;

                case 3:
                    System.out.println("Goodbye!");
                    return;

                default:
                    System.out.println("Invalid choice");
            }
        }
    }
}
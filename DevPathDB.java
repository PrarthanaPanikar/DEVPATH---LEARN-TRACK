package DEVPATHCONNECTIVITY;

import java.sql.*;

public class DevPathDB {

    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver");

            return DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/devpath",
                "root",
                "panikar"
            );

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}



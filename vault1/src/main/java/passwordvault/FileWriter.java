package passwordvault;

import java.sql.*;

public class FileWriter {
    private static final String dPath = "/Users/9709796/Documents/Personal Project/PasswordVault";

    public static void storeUsers(String email, String password, String vaultName) {
        try{
            Class.forName("org.sqlite.JDBC");
            try(Connection connection = DriverManager.getConnection("jdbc:sqlite:users.db")) {
                String query = "INSERT INTO users (email, password, vaultName) VALUES (?, ?, ?)";
                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    preparedStatement.setString(1, email);
                    preparedStatement.setString(2, password);
                    preparedStatement.setString(3, vaultName);
                    preparedStatement.executeUpdate();
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Error storing credentials: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void storeUserCred (String vaultName, String website, String username, String password) {
       try {
        Class.forName("org.sqlite.JDBC");
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + dPath + "/" + vaultName + ".db")) {
            String query = "INSERT INTO storage (website, username, password) VALUES (?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, website);
                preparedStatement.setString(2, username);
                preparedStatement.setString(3, password);
                preparedStatement.executeUpdate();
            }
        }
       } catch (ClassNotFoundException | SQLException e) {
        System.err.println("Error storing credentials: " + e.getMessage());
        e.printStackTrace();
       }
    }
}

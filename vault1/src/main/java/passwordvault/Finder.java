package passwordvault;

import java.sql.*;

public class Finder {
    private static final String dPath = "/Users/9709796/Documents/Personal Project/PasswordVault";
    public static void findCredentials(String vaultName, String website) {
        try {
            Class.forName("org.sqlite.JDBC");
            try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + dPath + "/" + vaultName + ".db")) {
                String query = "SELECT username, password FROM storage WHERE website = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    preparedStatement.setString(1, website);
                    ResultSet resultSet = preparedStatement.executeQuery();
                    if(resultSet.next()) {
                        String username = resultSet.getString("username");
                        String password = resultSet.getString("password");
                        System.out.println("Username for " + website + ":" + username);
                        System.out.println("Password for + " + website + ": " + password);
                    } else {
                        System.out.println("No credentials found for the website " + website);
                    }
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Error finding credentials: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

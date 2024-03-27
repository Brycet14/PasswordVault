package passwordvault;

import java.sql.*;

import java.util.Scanner;

import org.mindrot.jbcrypt.BCrypt;

import javax.mail.*;
import javax.mail.internet.*;

public class Main {
    public static void main(String[] args) {
        String email;
        String sEmail;
        String sPswrd;
        String cUPswrd = "";
        String uPswrd;
        String vaultName;
        int start;
        Scanner sc = new Scanner(System.in);
        System.out.println("Welcome to the secure password vault.\nTo sign in, press 1\nTo create an account, press 2");
        start = sc.nextInt();

        if (start == 1) {
            System.out.println("Enter your email: ");
            sEmail = sc.nextLine();
            System.out.println("Enter your password: ");
            sPswrd = sc.nextLine();

            if (checkSignIn(sEmail, sPswrd)) {
                System.out.println("Sign in successful!");
            } else {
                System.out.println("Incorrect email or password. Sign in failed.");
            }
        } else if (start == 2) {
            System.out.println("Please enter an email: ");
            email = sc.nextLine();

            System.out.println("Please enter a password");
            uPswrd = sc.nextLine();
            System.out.println("Please confirm your password");
            cUPswrd = sc.nextLine();
            if(cUPswrd == uPswrd) {
                hashPswrd(cUPswrd);
                System.out.println("Please name your vault");
                vaultName = sc.nextLine();

                storeCred(email, uPswrd);
                createVault(vaultName);
            } else {
                System.out.println("Passwords did not match! Try again.");
            }
        }
        sc.close();
    }

    private static String hashPswrd(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    private static void storeCred(String email, String pswrd) {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:users.db")) {
            String query = "INSERT INTO users (email, password) VALUES (?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, email);
                preparedStatement.setString(2, pswrd);
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                System.err.println("Error storing credentials: " + e.getMessage());
            }
        } catch (SQLException e) {
            System.err.println("Error storing credentials: " + e.getMessage());
        }
    }

    private static void createVault(String vaultName) {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + vaultName + ".db")) {
            String query = "CREATE TABLE IF NOT EXISTS websites (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, username TEXT, password TEXT)";
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate(query);
            } catch (SQLException e) {
                System.err.println("Error creating Vault: " + e.getMessage());
            }
        } catch (SQLException e) {
            System.err.println("Error creating Vault: " + e.getMessage());
        }
    }

    private static boolean checkSignIn(String email, String pswrd) {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:users.db")) {
            String query = "SELECT password FROM users WHERE email = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, query);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    String hashedPassword = resultSet.getString("password");
                    return BCrypt.checkpw(pswrd, hashedPassword);
                }
            } catch (SQLException e) {
                System.err.println("Error checking sign-in credentials: " + e.getMessage());
            }
        } catch (SQLException e) {
            System.err.println("Error checking sign-in credentials: " + e.getMessage());
        }
        return false;
    }
}
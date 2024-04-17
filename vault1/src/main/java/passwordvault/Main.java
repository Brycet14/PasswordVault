package passwordvault;

import java.sql.*;

import java.util.Scanner;

import org.mindrot.jbcrypt.BCrypt;

// import javax.mail.*;
// import javax.mail.internet.*;

public class Main {
    public static void main(String[] args) {
        String email, sEmail;
        String cUPswrd, uPswrd, fPswrd, sPswrd;
        String vaultName;
        final String path = "/Users/9709796/Documents/Personal Project/PasswordVault";
        int start, select;
        boolean signedIn = false;
        Scanner sc = new Scanner(System.in);
        System.out.println("Welcome to the secure password vault.\nTo sign in, press 1: \nTo create an account, press 2: ");
        start = sc.nextInt();

        // Encrypt.encrypt(path);
        if (start == 1) {
            System.out.println("Enter your email: ");
            sEmail = sc.nextLine();
            System.out.println("Enter your password: ");
            sPswrd = sc.nextLine();

            if (checkSignIn(sEmail, sPswrd)) {
                signedIn = true;
                System.out.println("Sign in successful! Welcome to your vault!");
                Decrypt.decrypt(path + "/" + );
                while (signedIn) {
                    System.out.println("To find a password, press 1. \nTo add a password to your vault, press 2 \nTo exit the vault, press 3");
                    select = sc.nextInt();
                    if(select == 1) {

                    } else if(select == 2) {

                    } else if (select == 3) {
                        break;
                    }
                }
            } else {
                System.out.println("Incorrect email or password. Please try again.");
            }

        } else if (start == 2) {
            System.out.println("Please enter an email: ");
            email = sc.nextLine();
            System.out.println("Please enter a password: ");
            uPswrd = sc.nextLine();
            System.out.println("Please confirm your password: ");
            cUPswrd = sc.nextLine();
            if (cUPswrd.equals(uPswrd)) {
                fPswrd = hashPswrd(cUPswrd);
                System.out.println("Please name your vault: ");
                vaultName = sc.nextLine();

                storeCred(email, fPswrd, vaultName);
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

    private static void storeCred(String email, String pswrd, String vaultName) {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:users.db")) {
            String query = "INSERT INTO users (email, password, vaultName) VALUES (?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, email);
                preparedStatement.setString(2, pswrd);
                preparedStatement.setString(3, vaultName);
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
            System.err.println("Error establishing connection: " + e.getMessage());
        }
        return false;
    }
}
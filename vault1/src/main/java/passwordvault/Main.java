package passwordvault;

import java.sql.*;

import java.util.Scanner;

import org.mindrot.jbcrypt.BCrypt;

// import javax.mail.*;
// import javax.mail.internet.*;

public class Main {
    public static void main(String[] args) {
        String email, sEmail, wUsername, website, wPswrd;
        String cUPswrd, uPswrd, fPswrd, sPswrd;
        String vaultName;
        final String path = "/Users/9709796/Documents/Personal Project/PasswordVault";
        int start, select;
        boolean signedIn = false;
        Scanner sc = new Scanner(System.in);
        System.out.println(
                "Welcome to the secure password vault.\nTo sign in, press 1: \nTo create an account, press 2: \nTo exit the program, press 3: ");
        start = sc.nextInt();

        // Encrypt.encrypt(path);
        // Decrypt.decrypt("/Users/9709796/Documents/Personal Project/PasswordVault/users.db");
        while (!signedIn) {
        if (start == 1) {
            System.out.println("Enter your email: ");
            sc.nextLine();
            sEmail = sc.nextLine();
            System.out.println("Enter your password: ");
            sPswrd = sc.nextLine();
            checkSignIn(sEmail, sPswrd);

            if (checkSignIn(sEmail, sPswrd)) {
                signedIn = true;
                System.out.println("Sign in successful! Welcome to your vault!");
                Decrypt.decrypt(path + "/" + getVaultName(sEmail));
                while (signedIn) {
                    System.out.println(
                            "To find a password, press 1. \nTo add a password to your vault, press 2 \nTo exit the vault, press 3");
                    select = sc.nextInt();
                    if (select == 1) {
                        sc.nextLine();
                        System.out.println("Enter the websites name: ");
                        website = sc.nextLine();
                        Finder.findCredentials(getVaultName(sEmail), website);
                    } else if (select == 2) {
                        sc.nextLine();
                        System.out.println("Enter the websites name: ");
                        website = sc.nextLine();
                        System.out.println("Enter your username for the website: ");
                        wUsername = sc.nextLine();
                        System.out.println("Enter your password for the website: ");
                        wPswrd = sc.nextLine();
                        FileWriter.storeUserCred(getVaultName(sEmail), website, wUsername, wPswrd);
                    } else if (select == 3) {
                        break;
                    }
                }
            } else {
                System.out.println("Incorrect email or password.");
            }
        } else if (start == 2) {
            System.out.println("Please enter an email: ");
            sc.nextLine();
            email = sc.nextLine();
            System.out.println("Please enter a password: ");
            uPswrd = sc.nextLine();
            System.out.println("Please confirm your password: ");
            cUPswrd = sc.nextLine();
            if (cUPswrd.equals(uPswrd)) {
                fPswrd = hashPswrd(cUPswrd);
                System.out.println("Please name your vault: ");
                vaultName = sc.nextLine();

                FileWriter.storeUsers(email, fPswrd, vaultName);
                createVault(vaultName);
            } else {
                System.out.println("Passwords did not match! Try again.");
            }
        } else if (start == 3) {
            break;
        }
    }
    sc.close();
}

    private static String hashPswrd(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    private static void createVault(String vaultName) {
        try {
            Class.forName("org.sqlite.JDBC");

            try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + vaultName + ".db")) {
                String query = "CREATE TABLE IF NOT EXISTS storage (id INTEGER PRIMARY KEY AUTOINCREMENT, website TEXT, username TEXT, password TEXT)";
                try (Statement statement = connection.createStatement()) {
                    statement.executeUpdate(query);
                } catch (SQLException e) {
                    System.err.println("Error creating Vault: " + e.getMessage());
                }
            } catch (SQLException e) {
                System.err.println("Error creating Vault: " + e.getMessage());
            }
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC Driver not found: " + e.getMessage());
        }
    }

    private static boolean checkSignIn(String email, String pswrd) {
        try {
            Class.forName("org.sqlite.JDBC");

            try (Connection connection = DriverManager.getConnection("jdbc:sqlite:users.db")) {
                String query = "SELECT password FROM users WHERE email = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    preparedStatement.setString(1, email);
                    ResultSet resultSet = preparedStatement.executeQuery();
                    if (resultSet.next()) {
                        String hashedPassword = resultSet.getString("password");
                        return BCrypt.checkpw(pswrd, hashedPassword);
                    } else {
                        System.out.println("User with email '" + email + "' not found.");
                    }
                } catch (SQLException e) {
                    System.err.println("Error checking sign-in credentials: " + e.getMessage());
                }
            } catch (SQLException e) {
                System.err.println("Error establishing connection: " + e.getMessage());
            }
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC driver not found: " + e.getMessage());
        }
        return false;
    }

    private static String getVaultName(String email) {
        String vaultName = null;
        try {
            Class.forName("org.sqlite.JDBC");
            try {
                Connection connection = DriverManager.getConnection("jdbc:sqlite:users.db");
                String sql = "SELECT vaultName FROM users WHERE email = ?";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, email);

                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    vaultName = resultSet.getString("vaultName");
                }

                resultSet.close();
                statement.close();
                connection.close();
            } catch (SQLException e) {
                System.err.println("Error fetching vault name" + e.getMessage());
                e.printStackTrace();
            }
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC driver not found: " + e.getMessage());
        }
        return vaultName;
    }
}
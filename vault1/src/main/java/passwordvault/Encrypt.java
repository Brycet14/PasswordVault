package passwordvault;

import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.*;
import java.security.spec.KeySpec;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class Encrypt {
    private static final int kSize = 256;
    private static final String eAlg = "AES/GCM/NoPadding";
    private static final String kAlg = "AES";
    private static final String pswrd = "Password";

    public static void encrypt(String projectDirectory) {
        try {
            SecureRandom secRand = new SecureRandom();
            byte[] salt = new byte[16];
            secRand.nextBytes(salt);

            SecretKeyFactory fac = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(pswrd.toCharArray(), salt, 65536, kSize);
            SecretKey tmp = fac.generateSecret(spec);
            SecretKey sKey = new SecretKeySpec(tmp.getEncoded(), kAlg);

            Cipher cipher = Cipher.getInstance(eAlg);
            cipher.init(Cipher.ENCRYPT_MODE, sKey);

            List<File> databaseFiles = getDatabaseFiles(projectDirectory);

            for (File f : databaseFiles) {
                if(f.getName().endsWith(".encrypted")) {
                    continue;
                }
                if (f.getName().equals("users.db")) {
                    continue;
                }
            }

            for (File f : databaseFiles) {
                byte[] fileBytes = Files.readAllBytes(f.toPath());
                byte[] encryptedBytes = cipher.doFinal(fileBytes);

                try (FileOutputStream oS = new FileOutputStream(f)) {
                    oS.write(encryptedBytes);
                }
                System.out.println("Encrypted Files: " + f.getName());
            }
            System.out.println("Files encrypted Succesfully");

        } catch (Exception e) {
            System.err.println("Error setting up Encryption");
            e.printStackTrace();
        }
    }

    private static List<File> getDatabaseFiles(String directory) {
        List<File> databaseFiles = new ArrayList<>();
        File[] files = new File(directory).listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".db")) {
                    databaseFiles.add(file);
                }
            }
        }
        return databaseFiles;
    }
}

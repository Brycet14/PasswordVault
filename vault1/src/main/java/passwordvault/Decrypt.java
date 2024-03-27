package passwordvault;

import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.spec.KeySpec;
import java.io.*;
import java.nio.file.*;

public class Decrypt {
    private static final int kSize = 256;
    private static final String eAlg = "AES/GCM/NoPadding";
    private static final String kAlg = "AES";
    private static final String salt = "Salt";
    private static final String pswrd = "Password";

    public static void decrypt (String encryptedDatabasePath) {
        try {
            SecretKeyFactory fac = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(pswrd.toCharArray(), salt.getBytes(), 65536, kSize);
            SecretKey temp = fac.generateSecret(spec);
            SecretKey sKey = new SecretKeySpec(temp.getEncoded(), kAlg);

            Cipher cipher = Cipher.getInstance(eAlg);
            cipher.init(Cipher.DECRYPT_MODE, sKey);

            byte[] encryptedBytes = Files.readAllBytes(Paths.get(encryptedDatabasePath));
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

            String decryptedDatabasePath = encryptedDatabasePath.replace(".encrypted", "");
            try (FileOutputStream oS = new FileOutputStream(decryptedDatabasePath)) {
                oS.write(decryptedBytes);
            }
        } catch (Exception e) {
            System.err.println("Error setting up decryptor");
            e.printStackTrace();
        }
    }
}

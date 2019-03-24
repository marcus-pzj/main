package planmysem.storage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import java.security.KeyStore;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

/**
 * Manages key storage and load.
 */
public class KeyStorage {

    /**
     * Loads the secret key from the key store.
     */
    public static SecretKey load () throws Exception {

        char[] password = "password".toCharArray();
        //Initialize keystore.
        KeyStore ks = KeyStore.getInstance("JCEKS");
        SecretKey secretKey;
        try {
            ks.load(new FileInputStream("KeyStorage.jceks"), password);
            secretKey = (SecretKey) ks.getKey("secret-key", password);

        } catch (FileNotFoundException ex) {

            //Generates key.
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(256);
            secretKey = keyGen.generateKey();

            //Save key.
            KeyStore.SecretKeyEntry secret = new KeyStore.SecretKeyEntry(secretKey);
            KeyStore.ProtectionParameter passwordParam = new KeyStore.PasswordProtection(password);
            ks.load(null, null);
            ks.setEntry("secret-key", secret, passwordParam);

            FileOutputStream fos = new FileOutputStream("KeyStorage.jceks");
            ks.store(fos, password);

        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
        return secretKey;
    }

}

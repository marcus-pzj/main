package planmysem.storage;

import java.io.FileInputStream;
import java.io.FileOutputStream;

import java.io.IOException;
import java.security.KeyStore;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

/**
 * Manages key storage and load.
 */
public class KeyStorage {

    private static char[] password = "password".toCharArray();

    /**
     * Loads the secret key from the key store.
     */
    public static SecretKey load () throws Exception {

        //Initialize keystore.
        KeyStore ks = KeyStore.getInstance("JCEKS");
        SecretKey secretKey;
        try {
            ks.load(new FileInputStream("KeyStorage.jceks"), password);
            secretKey = (SecretKey) ks.getKey("secret-key", password);

        } catch (IOException | CertificateException | UnrecoverableKeyException ex) {
            secretKey = generateSecretKey();
        }
        return secretKey;
    }

    /**
     * Generates a SecretKey and saves it into a KeyStorage.jceks file.
     */
    private static SecretKey generateSecretKey() throws Exception {
        //Initialize keystore.
        KeyStore ks = KeyStore.getInstance("JCEKS");

        //Generates key.
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256);
        SecretKey secretKey = keyGen.generateKey();

        //Save key.
        KeyStore.SecretKeyEntry secret = new KeyStore.SecretKeyEntry(secretKey);
        KeyStore.ProtectionParameter passwordParam = new KeyStore.PasswordProtection(password);
        ks.load(null, null);
        ks.setEntry("secret-key", secret, passwordParam);

        FileOutputStream fos = new FileOutputStream("KeyStorage.jceks");
        ks.store(fos, password);

        return secretKey;
    }
}



import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public class Datum {
    private byte[] datum;

    // Converts a String or file into datum
    public Datum(String given, boolean isFile) throws IOException {
        if (isFile) {
            File file = new File(given);

            FileInputStream input = new FileInputStream(file);

            this.datum = input.readAllBytes();
            input.close();
        } else {
            this.datum = given.getBytes("UTF8");
        }
    }


    // Construct datum from a given byte array
    public Datum(byte[] datum) {
        this.datum = datum;
    }


    // Return the datum value
    public byte[] getDatum() {
        return datum;
    }


    // Encrypt/Decrypt the datum
    private void crypt(byte[] key, byte[] salt, byte[] IV, boolean encrypt) throws Exception {
        byte[] saltyKey = ByteBuffer.allocate(salt.length + key.length)
            .put(salt)
            .put(key)
            .array();

        MessageDigest SHA3 = MessageDigest.getInstance("SHA3-256");
        byte[] hashedKey = SHA3.digest(saltyKey);

        SecretKey finalKey = new SecretKeySpec(hashedKey, "AES");
        IvParameterSpec finalIV = new IvParameterSpec(IV);

        Cipher AES = Cipher.getInstance("AES/CTR/NoPadding");

        if (encrypt) {
            AES.init(Cipher.ENCRYPT_MODE, finalKey, finalIV);
        } else {
            AES.init(Cipher.DECRYPT_MODE, finalKey, finalIV);
        }

        datum = AES.doFinal(datum);
    }


    // Encrypt the datum
    public void encrypt(byte[] key, byte[] salt, byte[] IV) throws Exception {
        crypt(key, salt, IV, true);
    }


    // Decrypt the datum
    public void decrypt(byte[] key, byte[] salt, byte[] IV) throws Exception {
        crypt(key, salt, IV, false);
    }


    // Hash the datum
    public byte[] hash() throws NoSuchAlgorithmException {
        MessageDigest SHA3 = MessageDigest.getInstance("SHA3-256");
        return SHA3.digest(datum);
    }

    // Used in unit testing to find bugs
    public String toString() {
        String output = "[";

        for (int i = 0; i < datum.length - 1; ++i) {
            output += datum[i] + " ";
        }

        output += datum[datum.length - 1] + "]";

        return output;
    }
}
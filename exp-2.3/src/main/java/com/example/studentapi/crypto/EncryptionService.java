package com.example.studentapi.crypto;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Service
public class EncryptionService {

    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private final SecretKey secretKey;

    public EncryptionService(@Value("${security.encryption.key:mySuperSecretEncryptionKey12345}") String key) {
        byte[] keyBytes = new byte[32];
        byte[] inputBytes = key.getBytes(StandardCharsets.UTF_8);
        System.arraycopy(inputBytes, 0, keyBytes, 0, Math.min(inputBytes.length, keyBytes.length));
        this.secretKey = new SecretKeySpec(keyBytes, "AES");
    }

    public String encrypt(String plainText) {
        if (plainText == null) {
            return null;
        }
        try {
            byte[] iv = new byte[16];
            new SecureRandom().nextBytes(iv);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);

            byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            byte[] combined = new byte[iv.length + encrypted.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(encrypted, 0, combined, iv.length, encrypted.length);

            return Base64.getEncoder().encodeToString(combined);
        } catch (Exception ex) {
            throw new RuntimeException("Encryption failed", ex);
        }
    }

    public String decrypt(String cipherText) {
        if (cipherText == null) {
            return null;
        }
        try {
            byte[] combined = Base64.getDecoder().decode(cipherText);
            byte[] iv = new byte[16];
            System.arraycopy(combined, 0, iv, 0, iv.length);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            int encryptedLength = combined.length - iv.length;
            byte[] encrypted = new byte[encryptedLength];
            System.arraycopy(combined, iv.length, encrypted, 0, encryptedLength);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);

            byte[] decrypted = cipher.doFinal(encrypted);
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception ex) {
            throw new RuntimeException("Decryption failed", ex);
        }
    }
}

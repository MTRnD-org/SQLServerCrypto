package org.mtrnd.sqlservercrypto;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * SQL Server crypto algorithm wrapper
 */
public class SQLServerCryptoAlgorithm {
    private final SQLServerCryptoVersion version;
    private final MessageDigest hash;
    private final String symmetricAlgorithm;
    private final int keySize;
    private SecretKeySpec key;
    private IvParameterSpec iv;

    public SQLServerCryptoAlgorithm(SQLServerCryptoVersion version) {
        this.version = version;
        
        try {
            switch (version) {
                case V1:
                    this.hash = MessageDigest.getInstance("SHA-1");
                    this.symmetricAlgorithm = "DESede/CBC/PKCS5Padding"; // TripleDES
                    this.keySize = 16;
                    break;
                case V2:
                    this.hash = MessageDigest.getInstance("SHA-256");
                    this.symmetricAlgorithm = "AES/CBC/PKCS5Padding";
                    this.keySize = 32;
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported SQLServerCryptoVersion");
            }
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Cryptographic algorithm not available", e);
        }
    }

    public SQLServerCryptoVersion getVersion() {
        return version;
    }

    public int getKeySize() {
        return keySize;
    }

    public void setKeyFromPassPhrase(String passphrase) {
        byte[] unicodeBytes = passphrase.getBytes(StandardCharsets.UTF_16LE);
        byte[] hashBytes = hash.digest(unicodeBytes);
        byte[] keyBytes;
        
        if (version == SQLServerCryptoVersion.V1) {
            // For TripleDES, we need 24 bytes but SQL Server uses 16 bytes from SHA1
            // TripleDES in Java needs a 24-byte key, so we pad by repeating the first 8 bytes
            keyBytes = new byte[24];
            System.arraycopy(hashBytes, 0, keyBytes, 0, 16);
            System.arraycopy(hashBytes, 0, keyBytes, 16, 8);
        } else {
            // For AES256, use 32 bytes from SHA256
            keyBytes = Arrays.copyOf(hashBytes, keySize);
        }
        
        String keyAlgorithm = version == SQLServerCryptoVersion.V1 ? "DESede" : "AES";
        this.key = new SecretKeySpec(keyBytes, keyAlgorithm);
    }

    public void setIV(byte[] iv) {
        this.iv = new IvParameterSpec(iv);
    }

    public byte[] getIV() {
        return iv != null ? iv.getIV() : null;
    }

    public byte[] encrypt(byte[] data) {
        try {
            Cipher cipher = Cipher.getInstance(symmetricAlgorithm);
            cipher.init(Cipher.ENCRYPT_MODE, key, iv);
            return cipher.doFinal(data);
        } catch (Exception e) {
            throw new RuntimeException("Encryption failed", e);
        }
    }

    public byte[] decrypt(byte[] data) {
        try {
            Cipher cipher = Cipher.getInstance(symmetricAlgorithm);
            cipher.init(Cipher.DECRYPT_MODE, key, iv);
            return cipher.doFinal(data);
        } catch (Exception e) {
            throw new RuntimeException("Decryption failed", e);
        }
    }

    public byte[] generateIV() {
        try {
            int ivSize = keySize / 2;
            java.security.SecureRandom random = new java.security.SecureRandom();
            byte[] ivBytes = new byte[ivSize];
            random.nextBytes(ivBytes);
            this.iv = new IvParameterSpec(ivBytes);
            return ivBytes;
        } catch (Exception e) {
            throw new RuntimeException("IV generation failed", e);
        }
    }
}

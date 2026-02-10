package org.mtrnd.sqlservercrypto;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * Main API class for SQL Server encryption/decryption methods
 */
public class SQLServerCryptoMethod {

    /**
     * Encrypts a string using a passphrase (default version V1)
     *
     * @param passphrase The passphrase used for encryption
     * @param cleartext  The text to encrypt
     * @return Encrypted hex string
     */
    public static HexString encryptByPassPhrase(String passphrase, String cleartext) {
        return encryptByPassPhrase(passphrase, cleartext, 0, "", SQLServerCryptoVersion.V1);
    }

    /**
     * Encrypts a string using a passphrase with specified version
     *
     * @param passphrase             The passphrase used for encryption
     * @param cleartext              The text to encrypt
     * @param sqlServerCryptoVersion The SQL Server crypto version
     * @return Encrypted hex string
     */
    public static HexString encryptByPassPhrase(String passphrase, String cleartext, SQLServerCryptoVersion sqlServerCryptoVersion) {
        return encryptByPassPhrase(passphrase, cleartext, 0, "", sqlServerCryptoVersion);
    }

    /**
     * Encrypts a string using a passphrase with authenticator
     *
     * @param passphrase        The passphrase used for encryption
     * @param cleartext         The text to encrypt
     * @param add_authenticator 1 to add authenticator, 0 otherwise
     * @param authenticator     The authenticator string
     * @return Encrypted hex string
     */
    public static HexString encryptByPassPhrase(String passphrase, String cleartext, int add_authenticator, String authenticator) {
        return encryptByPassPhrase(passphrase, cleartext, add_authenticator, authenticator, SQLServerCryptoVersion.V1);
    }

    /**
     * Encrypts a string using a passphrase with all options
     *
     * @param passphrase             The passphrase used for encryption
     * @param cleartext              The text to encrypt
     * @param add_authenticator      1 to add authenticator, 0 otherwise
     * @param authenticator          The authenticator string
     * @param sqlServerCryptoVersion The SQL Server crypto version
     * @return Encrypted hex string
     */
    public static HexString encryptByPassPhrase(String passphrase, String cleartext, int add_authenticator, 
                                                  String authenticator, SQLServerCryptoVersion sqlServerCryptoVersion) {
        SQLServerCryptoAlgorithm algorithm = new SQLServerCryptoAlgorithm(sqlServerCryptoVersion);
        algorithm.setKeyFromPassPhrase(passphrase);
        algorithm.generateIV();

        SQLServerCryptoHeader header = new SQLServerCryptoHeader();
        header.setVersion(sqlServerCryptoVersion);
        header.setInitializationVector(algorithm.getIV());

        SQLServerCryptoMessage message = new SQLServerCryptoMessage();
        message.setAddAuthenticator(add_authenticator > 0);
        message.setAuthenticator(authenticator);
        message.createFromClearText(cleartext);

        byte[] messageBytes = message.toByteArray();
        byte[] encryptedMessage = algorithm.encrypt(messageBytes);

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            outputStream.write(header.toByteArray());
            outputStream.write(encryptedMessage);
            return new HexString(outputStream.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Encryption failed", e);
        }
    }

    /**
     * Decrypts a hex string using a passphrase
     *
     * @param passphrase The passphrase used for decryption
     * @param ciphertext The hex string to decrypt
     * @return Decrypted text
     */
    public static String decryptByPassPhrase(String passphrase, String ciphertext) {
        return decryptByPassPhrase(passphrase, new HexString(ciphertext), 0, "", true);
    }

    /**
     * Decrypts a hex string using a passphrase with authenticator
     *
     * @param passphrase        The passphrase used for decryption
     * @param ciphertext        The hex string to decrypt
     * @param add_authenticator 1 if authenticator was used, 0 otherwise
     * @param authenticator     The authenticator string
     * @return Decrypted text
     */
    public static String decryptByPassPhrase(String passphrase, String ciphertext, int add_authenticator, String authenticator) {
        return decryptByPassPhrase(passphrase, new HexString(ciphertext), add_authenticator, authenticator, true);
    }

    /**
     * Decrypts a hex string using a passphrase without verification
     *
     * @param passphrase The passphrase used for decryption
     * @param ciphertext The hex string to decrypt
     * @return Decrypted text
     */
    public static String decryptByPassPhraseWithoutVerification(String passphrase, String ciphertext) {
        return decryptByPassPhrase(passphrase, new HexString(ciphertext), 0, "", false);
    }

    /**
     * Internal decryption method with all options
     *
     * @param passphrase        The passphrase used for decryption
     * @param ciphertext        The hex string to decrypt
     * @param add_authenticator 1 if authenticator was used, 0 otherwise
     * @param authenticator     The authenticator string
     * @param verify            Whether to verify message integrity
     * @return Decrypted text
     */
    private static String decryptByPassPhrase(String passphrase, HexString ciphertext, int add_authenticator, 
                                               String authenticator, boolean verify) {
        byte[] ciphertextBytes = ciphertext.toByteArray();
        SQLServerCryptoVersion version = SQLServerCryptoVersion.fromValue(ciphertextBytes[0] & 0xFF);

        SQLServerCryptoAlgorithm algorithm = new SQLServerCryptoAlgorithm(version);
        algorithm.setKeyFromPassPhrase(passphrase);

        int versionAndReservedSize = 4;
        int ivSize = algorithm.getKeySize() / 2;

        byte[] iv = Arrays.copyOfRange(ciphertextBytes, versionAndReservedSize, versionAndReservedSize + ivSize);
        algorithm.setIV(iv);

        byte[] encryptedMessage = Arrays.copyOfRange(ciphertextBytes, versionAndReservedSize + ivSize, ciphertextBytes.length);
        byte[] decryptedMessage = algorithm.decrypt(encryptedMessage);

        SQLServerCryptoMessage message = new SQLServerCryptoMessage();
        message.setAddAuthenticator(add_authenticator > 0);
        message.setAuthenticator(authenticator);
        message.createFromDecryptedMessage(decryptedMessage, verify);

        return byteArrayToString(message.getMessageBytes());
    }

    private static String byteArrayToString(byte[] array) {
        StringBuilder result = new StringBuilder();
        for (byte b : array) {
            result.append((char) (b & 0xFF));
        }
        return result.toString();
    }
}

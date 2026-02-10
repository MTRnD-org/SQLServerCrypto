package org.mtrnd.sqlservercrypto;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Basic unit tests for SQLServerCryptoMethod
 */
public class SQLServerCryptoMethodTest {

    @Test
    public void testBasicEncryptionDecryption() {
        String passphrase = "password1234";
        String cleartext = "Hello World.";
        
        HexString encrypted = SQLServerCryptoMethod.encryptByPassPhrase(passphrase, cleartext);
        assertNotNull(encrypted);
        
        String decrypted = SQLServerCryptoMethod.decryptByPassPhrase(passphrase, encrypted.toString());
        assertEquals(cleartext, decrypted);
    }

    @Test
    public void testEncryptionDecryptionV2() {
        String passphrase = "password1234";
        String cleartext = "Hello World.";
        
        HexString encrypted = SQLServerCryptoMethod.encryptByPassPhrase(passphrase, cleartext, SQLServerCryptoVersion.V2);
        assertNotNull(encrypted);
        
        String decrypted = SQLServerCryptoMethod.decryptByPassPhrase(passphrase, encrypted.toString());
        assertEquals(cleartext, decrypted);
    }

    @Test
    public void testEncryptionDecryptionWithAuthenticator() {
        String passphrase = "password1234";
        String cleartext = "Hello World.";
        String authenticator = "my_authenticator";
        
        HexString encrypted = SQLServerCryptoMethod.encryptByPassPhrase(passphrase, cleartext, 1, authenticator);
        assertNotNull(encrypted);
        
        String decrypted = SQLServerCryptoMethod.decryptByPassPhrase(passphrase, encrypted.toString(), 1, authenticator);
        assertEquals(cleartext, decrypted);
    }

    @Test
    public void testDecryptionWithoutVerification() {
        String passphrase = "password1234";
        String cleartext = "Hello World.";
        String authenticator = "my_authenticator";
        
        HexString encrypted = SQLServerCryptoMethod.encryptByPassPhrase(passphrase, cleartext, 1, authenticator);
        assertNotNull(encrypted);
        
        // Decrypt without providing the authenticator
        String decrypted = SQLServerCryptoMethod.decryptByPassPhraseWithoutVerification(passphrase, encrypted.toString());
        assertEquals(cleartext, decrypted);
    }

    @Test
    public void testHexStringConversion() {
        String hexStr = "0x48656c6c6f";
        HexString hexString = new HexString(hexStr);
        
        assertEquals("0x48656c6c6f", hexString.toString());
        assertEquals("48656c6c6f", hexString.getValueWithoutPrefix());
    }

    @Test
    public void testHexStringFromByteArray() {
        byte[] bytes = {0x48, 0x65, 0x6c, 0x6c, 0x6f};
        HexString hexString = new HexString(bytes);
        
        assertEquals("0x48656c6c6f", hexString.toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testHexStringInvalidInput() {
        new HexString("0xZZZZ"); // Invalid hex characters
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEncryptionTooLongText() {
        String passphrase = "password1234";
        StringBuilder longText = new StringBuilder();
        for (int i = 0; i < 8001; i++) {
            longText.append("a");
        }
        
        SQLServerCryptoMethod.encryptByPassPhrase(passphrase, longText.toString());
    }
}

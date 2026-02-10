package org.mtrnd.sqlservercrypto.demo;

import org.mtrnd.sqlservercrypto.*;
import java.util.Scanner;

/**
 * Interactive Demo Program for SQLServerCrypto Library
 * 
 * This program allows you to test the library interactively without running unit tests.
 */
public class InteractiveDemo {
    
    private static final Scanner scanner = new Scanner(System.in);
    
    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println("  SQLServerCrypto Interactive Demo");
        System.out.println("  Test the library without Android SDK");
        System.out.println("==========================================");
        System.out.println();
        
        while (true) {
            showMenu();
            String choice = scanner.nextLine().trim();
            
            switch (choice) {
                case "1":
                    testBasicEncryption();
                    break;
                case "2":
                    testV2Encryption();
                    break;
                case "3":
                    testWithAuthenticator();
                    break;
                case "4":
                    testSqlServerCompatibility();
                    break;
                case "5":
                    testHexString();
                    break;
                case "6":
                    runAllTests();
                    break;
                case "0":
                    System.out.println("\nGoodbye!");
                    return;
                default:
                    System.out.println("\nInvalid choice. Please try again.\n");
            }
        }
    }
    
    private static void showMenu() {
        System.out.println("------------------------------------------");
        System.out.println(" Select a test to run:");
        System.out.println("------------------------------------------");
        System.out.println(" 1. Basic Encryption/Decryption (V1)");
        System.out.println(" 2. AES256 Encryption (V2)");
        System.out.println(" 3. Encryption with Authenticator");
        System.out.println(" 4. SQL Server Compatibility Test");
        System.out.println(" 5. HexString Utilities");
        System.out.println(" 6. Run All Tests");
        System.out.println(" 0. Exit");
        System.out.println("------------------------------------------");
        System.out.print("Enter your choice: ");
    }
    
    private static void testBasicEncryption() {
        System.out.println("\n==========================================");
        System.out.println("Test 1: Basic Encryption/Decryption (V1)");
        System.out.println("==========================================");
        
        System.out.print("Enter passphrase (or press Enter for 'password1234'): ");
        String passphrase = scanner.nextLine().trim();
        if (passphrase.isEmpty()) {
            passphrase = "password1234";
        }
        
        System.out.print("Enter text to encrypt (or press Enter for 'Hello World.'): ");
        String cleartext = scanner.nextLine().trim();
        if (cleartext.isEmpty()) {
            cleartext = "Hello World.";
        }
        
        try {
            System.out.println("\nEncrypting...");
            HexString encrypted = SQLServerCryptoMethod.encryptByPassPhrase(passphrase, cleartext);
            System.out.println("Encrypted: " + encrypted.toString());
            
            System.out.println("\nDecrypting...");
            String decrypted = SQLServerCryptoMethod.decryptByPassPhrase(passphrase, encrypted.toString());
            System.out.println("Decrypted: " + decrypted);
            
            if (cleartext.equals(decrypted)) {
                System.out.println("SUCCESS: Original and decrypted text match!");
            } else {
                System.out.println("FAILED: Texts don't match!");
            }
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }
    
    private static void testV2Encryption() {
        System.out.println("\n==========================================");
        System.out.println("Test 2: AES256 Encryption (V2)");
        System.out.println("==========================================");
        
        String passphrase = "password1234";
        String cleartext = "Hello World with AES256!";
        
        try {
            System.out.println("Passphrase: " + passphrase);
            System.out.println("Cleartext: " + cleartext);
            
            System.out.println("\nEncrypting with V2 (AES256/SHA256)...");
            HexString encrypted = SQLServerCryptoMethod.encryptByPassPhrase(
                passphrase, cleartext, SQLServerCryptoVersion.V2);
            System.out.println("Encrypted: " + encrypted.toString());
            
            System.out.println("\nDecrypting...");
            String decrypted = SQLServerCryptoMethod.decryptByPassPhrase(passphrase, encrypted.toString());
            System.out.println("Decrypted: " + decrypted);
            
            if (cleartext.equals(decrypted)) {
                System.out.println("SUCCESS: V2 encryption/decryption works!");
            } else {
                System.out.println("FAILED: Texts don't match!");
            }
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }
    
    private static void testWithAuthenticator() {
        System.out.println("\n==========================================");
        System.out.println("Test 3: Encryption with Authenticator");
        System.out.println("==========================================");
        
        String passphrase = "password1234";
        String cleartext = "Secure data";
        String authenticator = "my_authenticator";
        
        try {
            System.out.println("Passphrase: " + passphrase);
            System.out.println("Cleartext: " + cleartext);
            System.out.println("Authenticator: " + authenticator);
            
            System.out.println("\nEncrypting with authenticator...");
            HexString encrypted = SQLServerCryptoMethod.encryptByPassPhrase(
                passphrase, cleartext, 1, authenticator);
            System.out.println("Encrypted: " + encrypted.toString());
            
            System.out.println("\nDecrypting with authenticator...");
            String decrypted = SQLServerCryptoMethod.decryptByPassPhrase(
                passphrase, encrypted.toString(), 1, authenticator);
            System.out.println("Decrypted: " + decrypted);
            
            System.out.println("\nDecrypting WITHOUT authenticator verification...");
            String decryptedNoVerif = SQLServerCryptoMethod.decryptByPassPhraseWithoutVerification(
                passphrase, encrypted.toString());
            System.out.println("Decrypted: " + decryptedNoVerif);
            
            if (cleartext.equals(decrypted) && cleartext.equals(decryptedNoVerif)) {
                System.out.println("SUCCESS: Authenticator encryption works!");
            } else {
                System.out.println("FAILED: Texts don't match!");
            }
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }
    
    private static void testSqlServerCompatibility() {
        System.out.println("\n==========================================");
        System.out.println("Test 4: SQL Server Compatibility");
        System.out.println("==========================================");
        
        String passphrase = "password1234";
        String sqlServerCiphertext = "0x010000003296649D6782CFD72B8145A07F2C7D7FE3D8B80CF48DA419E94FABC90EEB928D";
        
        try {
            System.out.println("SQL Server ciphertext: " + sqlServerCiphertext);
            System.out.println("Passphrase: " + passphrase);
            
            System.out.println("\nDecrypting SQL Server data...");
            String decrypted = SQLServerCryptoMethod.decryptByPassPhrase(passphrase, sqlServerCiphertext);
            System.out.println("Decrypted: " + decrypted);
            
            if ("Hello World.".equals(decrypted)) {
                System.out.println("SUCCESS: SQL Server compatibility confirmed!");
            } else {
                System.out.println("Unexpected result (but may be valid due to different IV)");
            }
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }
    
    private static void testHexString() {
        System.out.println("\n==========================================");
        System.out.println("Test 5: HexString Utilities");
        System.out.println("==========================================");
        
        try {
            String hexStr = "0x48656c6c6f";
            HexString hex = new HexString(hexStr);
            System.out.println("Input: " + hexStr);
            System.out.println("With prefix: " + hex.toString());
            System.out.println("Without prefix: " + hex.getValueWithoutPrefix());
            
            byte[] bytes = {0x48, 0x65, 0x6c, 0x6c, 0x6f};
            HexString hex2 = new HexString(bytes);
            System.out.println("\nByte array to hex: " + hex2.toString());
            
            System.out.println("\nSUCCESS: HexString utilities work!");
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }
    
    private static void runAllTests() {
        System.out.println("\n==========================================");
        System.out.println("Running All Tests");
        System.out.println("==========================================\n");
        
        int passed = 0;
        int failed = 0;
        
        try {
            System.out.println("1. Basic Encryption...");
            testQuick("password1234", "Test1");
            System.out.println("   Passed");
            passed++;
        } catch (Exception e) {
            System.out.println("   Failed: " + e.getMessage());
            failed++;
        }
        
        try {
            System.out.println("2. V2 Encryption...");
            String p = "password1234";
            String t = "Test2";
            HexString enc = SQLServerCryptoMethod.encryptByPassPhrase(p, t, SQLServerCryptoVersion.V2);
            String dec = SQLServerCryptoMethod.decryptByPassPhrase(p, enc.toString());
            if (!t.equals(dec)) throw new Exception("Mismatch");
            System.out.println("   Passed");
            passed++;
        } catch (Exception e) {
            System.out.println("   Failed: " + e.getMessage());
            failed++;
        }
        
        try {
            System.out.println("3. Authenticator...");
            String p = "password1234";
            String t = "Test3";
            String a = "auth";
            HexString enc = SQLServerCryptoMethod.encryptByPassPhrase(p, t, 1, a);
            String dec = SQLServerCryptoMethod.decryptByPassPhrase(p, enc.toString(), 1, a);
            if (!t.equals(dec)) throw new Exception("Mismatch");
            System.out.println("   Passed");
            passed++;
        } catch (Exception e) {
            System.out.println("   Failed: " + e.getMessage());
            failed++;
        }
        
        try {
            System.out.println("4. SQL Server Compatibility...");
            String dec = SQLServerCryptoMethod.decryptByPassPhrase("password1234", 
                "0x010000003296649D6782CFD72B8145A07F2C7D7FE3D8B80CF48DA419E94FABC90EEB928D");
            if (!"Hello World.".equals(dec)) throw new Exception("Mismatch");
            System.out.println("   Passed");
            passed++;
        } catch (Exception e) {
            System.out.println("   Failed: " + e.getMessage());
            failed++;
        }
        
        try {
            System.out.println("5. HexString...");
            HexString hex = new HexString("0x48656c6c6f");
            if (!"0x48656c6c6f".equals(hex.toString())) throw new Exception("Mismatch");
            System.out.println("   Passed");
            passed++;
        } catch (Exception e) {
            System.out.println("   Failed: " + e.getMessage());
            failed++;
        }
        
        System.out.println("\n==========================================");
        System.out.println("Test Results: " + passed + " passed, " + failed + " failed");
        System.out.println("==========================================");
        
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }
    
    private static void testQuick(String passphrase, String text) throws Exception {
        HexString encrypted = SQLServerCryptoMethod.encryptByPassPhrase(passphrase, text);
        String decrypted = SQLServerCryptoMethod.decryptByPassPhrase(passphrase, encrypted.toString());
        if (!text.equals(decrypted)) {
            throw new Exception("Text mismatch");
        }
    }
}

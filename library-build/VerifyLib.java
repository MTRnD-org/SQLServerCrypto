import org.mtrnd.sqlservercrypto.*;

public class VerifyLib {
    public static void main(String[] args) {
        try {
            String pass = "test";
            String data = "Hello";
            HexString enc = SQLServerCryptoMethod.encryptByPassPhrase(pass, data);
            String dec = SQLServerCryptoMethod.decryptByPassPhrase(pass, enc.toString());
            if (data.equals(dec)) {
                System.out.println("✅ Library verification PASSED!");
            } else {
                System.out.println("❌ Library verification FAILED!");
                System.exit(1);
            }
        } catch (Exception e) {
            System.out.println("❌ Library verification ERROR: " + e.getMessage());
            System.exit(1);
        }
    }
}

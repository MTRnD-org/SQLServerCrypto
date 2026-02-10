package org.mtrnd.sqlservercrypto;

/**
 * SQL Server crypto version enum
 */
public enum SQLServerCryptoVersion {
    /**
     * TripleDES/SHA1 (SQL Server 2008 - SQL Server 2016)
     */
    V1(0x01),
    
    /**
     * AES256/SHA256 (SQL Server 2017+)
     */
    V2(0x02);

    private final int value;

    SQLServerCryptoVersion(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static SQLServerCryptoVersion fromValue(int value) {
        for (SQLServerCryptoVersion version : SQLServerCryptoVersion.values()) {
            if (version.value == value) {
                return version;
            }
        }
        throw new IllegalArgumentException("Unsupported SQLServerCryptoVersion: " + value);
    }
}

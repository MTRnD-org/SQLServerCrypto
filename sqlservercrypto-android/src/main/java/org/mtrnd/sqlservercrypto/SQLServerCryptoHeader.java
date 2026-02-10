package org.mtrnd.sqlservercrypto;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * SQL Server crypto header
 */
public class SQLServerCryptoHeader {
    private SQLServerCryptoVersion version = SQLServerCryptoVersion.V1;
    private byte[] reserved = {0, 0, 0};
    private byte[] initializationVector = new byte[0];

    public SQLServerCryptoHeader() {
        this.version = SQLServerCryptoVersion.V1;
    }

    public SQLServerCryptoHeader(SQLServerCryptoVersion version) {
        this.version = version;
    }

    public SQLServerCryptoVersion getVersion() {
        return version;
    }

    public void setVersion(SQLServerCryptoVersion version) {
        this.version = version;
    }

    public byte[] getReserved() {
        return reserved;
    }

    public void setReserved(byte[] reserved) {
        this.reserved = reserved;
    }

    public byte[] getInitializationVector() {
        return initializationVector;
    }

    public void setInitializationVector(byte[] initializationVector) {
        this.initializationVector = initializationVector;
    }

    public byte[] toByteArray() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            outputStream.write((byte) version.getValue());
            outputStream.write(reserved);
            outputStream.write(initializationVector);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Error converting header to byte array", e);
        }
    }
}

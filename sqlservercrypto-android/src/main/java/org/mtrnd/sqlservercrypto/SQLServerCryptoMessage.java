package org.mtrnd.sqlservercrypto;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * SQL Server crypto message
 */
public class SQLServerCryptoMessage {
    private static final int MAGIC_NUMBER = 0xbaadf00d;
    
    private int magicNumber;
    private short integrityBytesLength;
    private short plainTextLength;
    private byte[] integrityBytes;
    private byte[] messageBytes;
    private boolean addAuthenticator = false;
    private String authenticator = "";

    public SQLServerCryptoMessage() {
        this.magicNumber = MAGIC_NUMBER;
    }

    public int getMagicNumber() {
        return magicNumber;
    }

    public void setMagicNumber(int magicNumber) {
        this.magicNumber = magicNumber;
    }

    public short getIntegrityBytesLength() {
        return integrityBytesLength;
    }

    public void setIntegrityBytesLength(short integrityBytesLength) {
        this.integrityBytesLength = integrityBytesLength;
    }

    public short getPlainTextLength() {
        return plainTextLength;
    }

    public void setPlainTextLength(short plainTextLength) {
        this.plainTextLength = plainTextLength;
    }

    public byte[] getIntegrityBytes() {
        return integrityBytes;
    }

    public void setIntegrityBytes(byte[] integrityBytes) {
        this.integrityBytes = integrityBytes;
    }

    public byte[] getMessageBytes() {
        return messageBytes;
    }

    public void setMessageBytes(byte[] messageBytes) {
        this.messageBytes = messageBytes;
    }

    public boolean isAddAuthenticator() {
        return addAuthenticator;
    }

    public void setAddAuthenticator(boolean addAuthenticator) {
        this.addAuthenticator = addAuthenticator;
    }

    public String getAuthenticator() {
        return authenticator;
    }

    public void setAuthenticator(String authenticator) {
        if (authenticator.length() > 128) {
            throw new IllegalArgumentException("The size of the authenticator string should not exceed 128 bytes.");
        }
        this.authenticator = authenticator;
    }

    public void createFromClearText(String cleartext) {
        this.messageBytes = cleartext.getBytes(StandardCharsets.US_ASCII);

        if (messageBytes.length > 8000) {
            throw new IllegalArgumentException("The size of the cleartext string should not exceed 8000 bytes.");
        }

        this.magicNumber = MAGIC_NUMBER;
        this.integrityBytesLength = 0;
        this.plainTextLength = (short) messageBytes.length;

        if (addAuthenticator) {
            try {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bos.write(messageBytes);
                bos.write(authenticator.getBytes(StandardCharsets.US_ASCII));
                byte[] integrityMessage = bos.toByteArray();
                
                MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
                this.integrityBytes = sha1.digest(integrityMessage);
                this.integrityBytesLength = (short) integrityBytes.length;
            } catch (NoSuchAlgorithmException | IOException e) {
                throw new RuntimeException("Error creating integrity hash", e);
            }
        }
    }

    public void createFromDecryptedMessage(byte[] decryptedMessage, boolean verify) {
        ByteBuffer buffer = ByteBuffer.wrap(decryptedMessage).order(ByteOrder.LITTLE_ENDIAN);
        
        this.magicNumber = buffer.getInt();
        this.integrityBytesLength = buffer.getShort();
        this.plainTextLength = buffer.getShort();

        int offset = 8;
        
        if (integrityBytesLength > 0 && integrityBytesLength != (short) 0xffff) {
            this.integrityBytes = new byte[integrityBytesLength];
            System.arraycopy(decryptedMessage, offset, integrityBytes, 0, integrityBytesLength);
            offset += integrityBytesLength;
        }

        int messageLength = decryptedMessage.length - offset;
        if (integrityBytesLength != (short) 0xffff) {
            this.messageBytes = new byte[messageLength];
            System.arraycopy(decryptedMessage, offset, messageBytes, 0, messageLength);
        } else {
            this.messageBytes = new byte[messageLength];
            System.arraycopy(decryptedMessage, offset, messageBytes, 0, messageLength);
        }

        if (verify) {
            verifyMessage();
        }
    }

    private void verifyMessage() {
        if (magicNumber != MAGIC_NUMBER) {
            throw new RuntimeException("Message integrity error. Magic numbers are different.");
        }

        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bos.write(messageBytes);
            bos.write(authenticator.getBytes(StandardCharsets.US_ASCII));
            byte[] integrityMessage = bos.toByteArray();
            
            MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
            byte[] hash = sha1.digest(integrityMessage);

            if (integrityBytes != null && integrityBytes.length > 0 && !Arrays.equals(hash, integrityBytes)) {
                throw new RuntimeException("Message integrity error. Invalid authenticator.");
            }

            if (plainTextLength != messageBytes.length) {
                throw new RuntimeException("Message integrity error. Invalid message length.");
            }
        } catch (NoSuchAlgorithmException | IOException e) {
            throw new RuntimeException("Error verifying message", e);
        }
    }

    public byte[] toByteArray() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            ByteBuffer buffer = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN);
            buffer.putInt(magicNumber);
            buffer.putShort(integrityBytesLength);
            buffer.putShort(plainTextLength);
            outputStream.write(buffer.array());

            if (integrityBytes != null) {
                outputStream.write(integrityBytes);
            }

            if (messageBytes != null) {
                outputStream.write(messageBytes);
            }

            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Error converting message to byte array", e);
        }
    }
}

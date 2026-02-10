package org.mtrnd.sqlservercrypto;

import java.util.regex.Pattern;

/**
 * Utility class for handling hexadecimal strings and byte array conversions
 */
public class HexString {
    private final byte[] byteArray;
    private static final Pattern HEX_VALIDATION = Pattern.compile("^[0-9a-fA-F]+$");
    private static final String PREFIX = "0x";

    public HexString(String hexString) {
        if (hexString == null || hexString.isEmpty()) {
            throw new IllegalArgumentException("Input string is null or empty.");
        }

        hexString = removePrefix(hexString);

        if (hexString.length() % 2 != 0) {
            throw new IllegalArgumentException("Invalid number of hexcharacters.");
        }

        if (!HEX_VALIDATION.matcher(hexString).matches()) {
            throw new IllegalArgumentException("Input string does not contain hexadecimal characters.");
        }

        this.byteArray = hexStringToByteArray(hexString);
    }

    public HexString(byte[] byteArray) {
        if (byteArray == null) {
            throw new IllegalArgumentException("Input array is null.");
        }

        if (byteArray.length == 0) {
            throw new IllegalArgumentException("Input array is empty.");
        }

        this.byteArray = byteArray;
    }

    public String getValueWithoutPrefix() {
        return byteArrayToHexString(byteArray);
    }

    public String getValueWithPrefix() {
        return PREFIX + getValueWithoutPrefix();
    }

    @Override
    public String toString() {
        return getValueWithPrefix();
    }

    public byte[] toByteArray() {
        return byteArray;
    }

    private static String removePrefix(String input) {
        return input.startsWith(PREFIX) ? input.substring(2) : input;
    }

    private static byte[] hexStringToByteArray(String hexString) {
        int len = hexString.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
                    + Character.digit(hexString.charAt(i + 1), 16));
        }
        return data;
    }

    private static String byteArrayToHexString(byte[] byteArray) {
        StringBuilder sb = new StringBuilder(byteArray.length * 2);
        for (byte b : byteArray) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}

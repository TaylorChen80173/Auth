package com.tw.auth.config;

import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.security.crypto.codec.Utf8;
import org.springframework.security.crypto.keygen.BytesKeyGenerator;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.util.Assert;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LdapSha2PasswordEncoder implements PasswordEncoder {

    /** The number of bytes in a SHA hash */
    private static final String SHA_PREFIX = "{SHA256}";
    private static final String SHA_PREFIX_LC = SHA_PREFIX.toLowerCase();

    private BytesKeyGenerator saltGenerator;

    private boolean forceLowerCasePrefix;

    public LdapSha2PasswordEncoder() {
        this.saltGenerator = KeyGenerators.secureRandom();
    }

    private byte[] combineHashAndSalt(byte[] hash, byte[] salt) {
        if (salt == null) {
            return hash;
        } else {
            byte[] hashAndSalt = new byte[hash.length + salt.length];
            System.arraycopy(hash, 0, hashAndSalt, 0, hash.length);
            System.arraycopy(salt, 0, hashAndSalt, hash.length, salt.length);
            return hashAndSalt;
        }
    }

    public String encodePassword(String rawPass) {
        return encodePassword(rawPass, null);
    }

    public String encodePassword(String rawPass, Object salt) {
        MessageDigest sha;
        try {
            sha = MessageDigest.getInstance("SHA-256");
            sha.update(Utf8.encode(rawPass));
        } catch (NoSuchAlgorithmException var6) {
            throw new IllegalStateException("No SHA256 implementation available!");
        }
        if (salt != null) {
            Assert.isInstanceOf(byte[].class, salt, "Salt value must be a byte array");
            sha.update((byte[])((byte[])salt));
        }
        byte[] hash = this.combineHashAndSalt(sha.digest(), (byte[])((byte[])salt));
        String prefix = forceLowerCasePrefix ? SHA_PREFIX_LC : SHA_PREFIX;
        return prefix + Utf8.decode(Base64.encode(hash));
    }

    public boolean isPasswordValid(String encPass, String rawPass, Object salt) {
        String prefix = this.extractPrefix(encPass);
        if (prefix == null) {
            return encPass.equals(rawPass);
        } else {
            if (!prefix.equals("{SHA}") && !prefix.equals(SHA_PREFIX_LC)) {
                throw new IllegalArgumentException("Unsupported password prefix '" + prefix + "'");
            }
            salt = null;

            int startOfHash = prefix.length();
            String encodedRawPass = this.encodePassword(rawPass, salt).substring(startOfHash);
            return PasswordEncoderUtils.equals(encodedRawPass, encPass.substring(startOfHash));
        }
    }

    /**
     * Returns the hash prefix or null if there isn't one.
     */
    private String extractPrefix(String encPass) {
        if (!encPass.startsWith("{")) {
            return null;
        }
        int secondBrace = encPass.lastIndexOf('}');

        if (secondBrace < 0) {
            throw new IllegalArgumentException("Couldn't find closing brace for SHA256 prefix");
        }
        return encPass.substring(0, secondBrace + 1);
    }

    public void setForceLowerCasePrefix(boolean forceLowerCasePrefix) {
        this.forceLowerCasePrefix = forceLowerCasePrefix;
    }
}

class PasswordEncoderUtils {
    static boolean equals(String expected, String actual) {
        byte[] expectedBytes = bytesUtf8(expected);
        byte[] actualBytes = bytesUtf8(actual);
        int expectedLength = expectedBytes == null ? -1 : expectedBytes.length;
        int actualLength = actualBytes == null ? -1 : actualBytes.length;
        int result = expectedLength == actualLength ? 0 : 1;

        for(int i = 0; i < actualLength; ++i) {
            byte expectedByte = expectedLength <= 0 ? 0 : expectedBytes[i % expectedLength];
            byte actualByte = actualBytes[i % actualLength];
            result |= expectedByte ^ actualByte;
        }
        return result == 0;
    }

    private static byte[] bytesUtf8(String s) {
        return s == null ? null : Utf8.encode(s);
    }

    private PasswordEncoderUtils() {
    }
}

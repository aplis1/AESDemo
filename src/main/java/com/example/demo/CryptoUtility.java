package com.example.demo;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;

@Component
public class CryptoUtility {

    /**
     * Validates that the key length is appropriate for AES encryption
     *
     * @param key The encryption key to validate
     * @throws InvalidKeyException if the key length is not 16, 24, or 32 bytes
     */
    private void validateAesKey(String key) throws InvalidKeyException {
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        int length = keyBytes.length;

        // AES allows only 128, 192, or 256 bit keys (16, 24, or 32 bytes)
        if (length != 16 && length != 24 && length != 32) {
            throw new InvalidKeyException("Invalid AES key length: " + length +
                    " bytes. AES requires exactly 16, 24, or 32 bytes (128, 192, or 256 bits).");
        }
    }

    /**
     * Performs encryption or decryption based on the cipher mode
     *
     * @param cipherMode Cipher.ENCRYPT_MODE or Cipher.DECRYPT_MODE
     * @param key The encryption/decryption key
     * @param inputStream Input data stream
     * @return Processed data as byte array
     */
    public byte[] doCrypt(int cipherMode, String key, InputStream inputStream) throws Exception {
        // Validate the key length for AES
        validateAesKey(key);

        // Create AES secret key from the provided key string
        SecretKey secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");

        // Initialize cipher
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(cipherMode, secretKey);

        // Read the input stream into a byte array
        byte[] inputBytes = readInputStream(inputStream);

        // Perform encryption/decryption
        return cipher.doFinal(inputBytes);
    }

    /**
     * Helper method to read an InputStream into a byte array
     */
    private byte[] readInputStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[1024];

        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }

        buffer.flush();
        return buffer.toByteArray();
    }

    /**
     * Encrypts a file from the resources folder and saves it as an encrypted file
     *
     * @param inputFilePath Path to the input file in resources
     * @param key The encryption key
     * @return Encrypted content as a byte array
     */
    public byte[] encryptResourceFile(String inputFilePath, String key) throws Exception {
        try (InputStream inputStream = new ClassPathResource(inputFilePath).getInputStream()) {
            return doCrypt(Cipher.ENCRYPT_MODE, key, inputStream);
        }
    }

    /**
     * Saves encrypted data to a file in the resources directory
     *
     * @param encryptedData The encrypted data to save
     * @param outputFileName The name of the output file
     */
    public void saveEncryptedFile(byte[] encryptedData, String outputFileName) throws IOException {
        // Get the path to the resources directory
        String resourcesPath = new ClassPathResource("").getFile().getAbsolutePath();
        Path outputPath = Paths.get(resourcesPath, outputFileName);

        // Create the output file
        try (FileOutputStream fos = new FileOutputStream(outputPath.toFile())) {
            fos.write(encryptedData);
            fos.flush();
        }
    }

    /**
     * Decrypts a file from the resources folder
     *
     * @param encryptedFilePath Path to the encrypted file in resources
     * @param key The decryption key
     * @return Decrypted content as a byte array
     */
    public byte[] decryptResourceFile(String encryptedFilePath, String key) throws Exception {
        try (InputStream inputStream = new ClassPathResource(encryptedFilePath).getInputStream()) {
            return doCrypt(Cipher.DECRYPT_MODE, key, inputStream);
        }
    }

    /**
     * Utility method to convert decrypted bytes to a string
     */
    public String bytesToString(byte[] bytes) {
        return new String(bytes, StandardCharsets.UTF_8);
    }
}
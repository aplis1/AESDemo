package com.example.demo;

import com.example.demo.CryptoUtility;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;

/**
 * Configuration class that handles decryption of the encrypted configuration file
 * and provides access to its content.
 */
@Configuration
public class SimpleConfig {
    private static final Logger logger = LoggerFactory.getLogger(SimpleConfig.class);

    @Value("${encryption.key:ThisIsA16ByteKey!}")
    private String encryptionKey;

    @Value("${encrypted.file.path:simple.txt.enc}")
    private String encryptedFilePath;

    private String decryptedContent;
    private boolean initialized = false;

    @Autowired
    private CryptoUtility cryptoUtility;

    /**
     * Initializes this configuration bean by checking if the encrypted file exists
     * and decrypting it if it does.
     */
    @PostConstruct
    public void init() {
        try {
            Resource resource = new ClassPathResource(encryptedFilePath);

            if (resource.exists()) {
                logger.info("Found encrypted file: {}. Attempting to decrypt.", encryptedFilePath);
                byte[] decryptedBytes = cryptoUtility.decryptResourceFile(encryptedFilePath, encryptionKey);
                decryptedContent = cryptoUtility.bytesToString(decryptedBytes);
                initialized = true;
                logger.info("Successfully decrypted configuration file.");
            } else {
                logger.warn("Encrypted file {} not found. Configuration will be unavailable until the file is created.",
                        encryptedFilePath);
                initialized = false;
            }
        } catch (Exception e) {
            logger.error("Failed to decrypt configuration file: {}", encryptedFilePath, e);
            initialized = false;
        }
    }

    /**
     * Manually initialize the config by decrypting the specified file
     * This can be called after encryption to make the config available
     */
    public void initializeConfig() {
        init();
    }

    /**
     * @return Whether the configuration has been successfully initialized
     */
    public boolean isInitialized() {
        return initialized;
    }

    /**
     * @return The decrypted content of the encrypted file
     * @throws IllegalStateException if the config hasn't been initialized
     */
    public String getDecryptedContent() {
        if (!initialized) {
            throw new IllegalStateException("Configuration has not been initialized. The encrypted file may not exist yet.");
        }
        return decryptedContent;
    }

    /**
     * Example of parsing the decrypted content into specific configuration properties.
     * This assumes the decrypted content follows a specific format that can be parsed.
     * Modify according to your actual content format.
     */
    public String getProperty(String key) {
        if (!initialized) {
            throw new IllegalStateException("Configuration has not been initialized. The encrypted file may not exist yet.");
        }

        // This is a simple example implementation
        // You should adjust this based on your actual content format
        for (String line : decryptedContent.split("\n")) {
            line = line.trim();
            if (line.startsWith(key + "=")) {
                return line.substring(key.length() + 1);
            }
        }
        return null;
    }
}
package com.example.demo;

import com.example.demo.SimpleConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Example service that uses the decrypted configuration
 */
@Service
public class ConfigDemoService {

    private final SimpleConfig simpleConfig;

    @Autowired
    public ConfigDemoService(SimpleConfig simpleConfig) {
        this.simpleConfig = simpleConfig;
    }

    /**
     * Gets the raw decrypted content from the configuration file
     * @return The decrypted content or null if not initialized
     */
    public String getDecryptedContent() {
        if (simpleConfig.isInitialized()) {
            return simpleConfig.getDecryptedContent();
        }
        return "Configuration not available yet. Please ensure the encrypted file exists.";
    }

    /**
     * Gets a specific property from the decrypted configuration
     * @return The property value or null if not found or not initialized
     */
    public String getConfigProperty(String key) {
        if (!simpleConfig.isInitialized()) {
            return "Configuration not available yet. Please ensure the encrypted file exists.";
        }
        String value = simpleConfig.getProperty(key);
        return value != null ? value : "Property not found: " + key;
    }

    /**
     * Check if the configuration is available
     */
    public boolean isConfigAvailable() {
        return simpleConfig.isInitialized();
    }

    /**
     * Re-initialize the configuration
     */
    public void refreshConfig() {
        simpleConfig.initializeConfig();
    }
}
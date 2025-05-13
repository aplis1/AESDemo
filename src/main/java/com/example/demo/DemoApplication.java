package com.example.demo;

import com.example.demo.SimpleConfig;
import com.example.demo.CryptoUtility;
import com.example.demo.ConfigDemoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.example"})
public class DemoApplication implements CommandLineRunner {
	private static final Logger logger = LoggerFactory.getLogger(DemoApplication.class);

	@Autowired
	private CryptoUtility cryptoUtility;

	@Autowired
	private ConfigDemoService configDemoService;

	@Autowired
	private SimpleConfig simpleConfig;

	@Value("${encryption.key}")
	private String encryptionKey;

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		// First, check if configuration is already available
		if (!configDemoService.isConfigAvailable()) {
			logger.info("Configuration not available. Creating encrypted file...");

			// Create the encrypted file
			encryptFile();

			// Re-initialize the config now that the file exists
			configDemoService.refreshConfig();
		}

		// Use the decrypted configuration
		useDecryptedConfig();
	}

	private void encryptFile() {
		// Path to the original file in resources folder
		String originalFilePath = "simple.txt";

		try {
			System.out.println("=== ENCRYPTION PROCESS ===");
			System.out.println("Starting encryption process...");
			System.out.println("Using key with length: " + encryptionKey.length() + " bytes");

			// Step 1: Encrypt the file
			byte[] encryptedData = cryptoUtility.encryptResourceFile(originalFilePath, encryptionKey);
			System.out.println("File successfully encrypted!");

			// Step 2: Save the encrypted data as simple.txt.enc
			cryptoUtility.saveEncryptedFile(encryptedData, "simple.txt.enc");
			System.out.println("Encrypted file saved as simple.txt.enc");
			System.out.println();

		} catch (Exception e) {
			System.err.println("Error during encryption: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private void useDecryptedConfig() {
		System.out.println("=== USING DECRYPTED CONFIGURATION ===");

		if (configDemoService.isConfigAvailable()) {
			// Display the full decrypted content
			System.out.println("Full decrypted content:");
			System.out.println(configDemoService.getDecryptedContent());
			System.out.println();

			// If the content is in key=value format, demonstrate getting a specific property
			System.out.println("Getting specific properties (if available):");
			System.out.println("Property 'app.name': " + configDemoService.getConfigProperty("app.name"));
			System.out.println("Property 'db.url': " + configDemoService.getConfigProperty("db.url"));
		} else {
			System.out.println("Configuration is not available. Please check if the encrypted file exists and can be decrypted.");
		}
	}
}
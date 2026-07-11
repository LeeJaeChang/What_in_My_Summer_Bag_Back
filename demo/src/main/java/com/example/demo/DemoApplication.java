package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		loadDotenv();
		SpringApplication.run(DemoApplication.class, args);
	}

	// spring-dotenv가 이 Boot 버전의 리스너 SPI와 호환되지 않아 직접 로드한다.
	private static void loadDotenv() {
		Path envFile = Path.of(".env");
		if (!Files.exists(envFile)) {
			return;
		}
		List<String> lines;
		try {
			lines = Files.readAllLines(envFile);
		} catch (IOException e) {
			throw new IllegalStateException("Failed to read .env file", e);
		}
		for (String line : lines) {
			String trimmed = line.trim();
			if (trimmed.isEmpty() || trimmed.startsWith("#")) {
				continue;
			}
			int separatorIndex = trimmed.indexOf('=');
			if (separatorIndex < 0) {
				continue;
			}
			String key = trimmed.substring(0, separatorIndex).trim();
			String value = trimmed.substring(separatorIndex + 1).trim();
			if (System.getProperty(key) == null && System.getenv(key) == null) {
				System.setProperty(key, value);
			}
		}
	}

}

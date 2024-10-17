package de.fhdo.smart_house.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "custom")
@Data
public class CustomProperties {
	private LogDir logDir = new LogDir();

	@Data
	private static class LogDir {
		private String base;
		private String chargingStation;
		private String energySource;
		private String system;
		private String archive;
		private String defaultDir;
	}
}

package de.fhdo.smart_house.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@ConfigurationProperties(prefix = "custom")
@Data
public class CustomProperties {
	@Data
	private static class LogDir {
		private String base;
		private String chargingStation;
		private String energySource;
		private String system;
		private String archive;
		private String _default;
	}
}

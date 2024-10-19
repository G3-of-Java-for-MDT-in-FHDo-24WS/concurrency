package de.fhdo.smart_house;

import de.fhdo.smart_house.config.CustomProperties;
import de.fhdo.smart_house.service.LogSearchService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import java.io.IOException;

@SpringBootApplication
@EnableConfigurationProperties(CustomProperties.class)
public class SmartHouseApplication {
	@Autowired
	private LogSearchService logSearchService;

	public static void main(String[] args) {
		SpringApplication.run(SmartHouseApplication.class, args);
	}

	@PostConstruct
	void init() throws IOException {
		System.out.println("yes");
		logSearchService.searchLogListByPattern(".*");
	}
}

package de.fhdo.smart_house.service;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.*;

import de.fhdo.smart_house.service.LogManageService.LogType;
import de.fhdo.smart_house.config.CustomProperties;
import org.springframework.stereotype.Component;

@Component
public class LogSearchService {
    private final CustomProperties customProperties;

    public LogSearchService(CustomProperties customProperties) {
        this.customProperties = customProperties;
    }

    public void searchLogByPattern(String logDirectory, String pattern) throws IOException {
        File dir = new File(logDirectory);
        File[] files = dir.listFiles();
        Pattern regex = Pattern.compile(pattern);

        for (File file : files) {
            Matcher matcher = regex.matcher(file.getName());
            if (matcher.find()) {
                System.out.println("Found log: " + file.getName());
                displayLog(file);
            }
        }
    }
    
    // TODO To be complete
    public List<Path> searchLogListByPattern(String pattern) {
    	List<Path> pathList = new ArrayList<>();

    	return pathList;
    }
    
    // TODO To be complete
    public List<Path> searchLogListByPattern(String pattern, LogType logType) {
    	List<Path> pathList = new ArrayList<>();
    	return pathList;
    }

    private void displayLog(File file) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        }
    }
}

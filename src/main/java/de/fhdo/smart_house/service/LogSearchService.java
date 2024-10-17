package de.fhdo.smart_house.service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.*;
import java.util.stream.Stream;

import de.fhdo.smart_house.service.LogManageService.LogType;
import de.fhdo.smart_house.config.CustomProperties;
import de.fhdo.smart_house.util.CustomExceptionHandler;
import org.springframework.stereotype.Component;

@Component
public class LogSearchService {
    private final CustomProperties.LogDir logDir;
    private final LogManageService logManageService;

    public LogSearchService(CustomProperties customProperties, LogManageService logManageService) {
        this.logDir = customProperties.getLogDir();
        this.logManageService = logManageService;
    }

    public List<Path> searchLogListByPattern(String pattern) throws IOException {
        List<Path> pathList = new ArrayList<>();
        Pattern regex = Pattern.compile(pattern);

        Path logBaseDirPath = Paths.get(logDir.getBase());

        System.out.println("Searching, please wait.....");

        try (Stream<Path> paths = Files.walk(logBaseDirPath)) {
            paths.filter(Files::isRegularFile).forEach(path -> {
                Matcher matcher = regex.matcher((CharSequence) path.getFileName());

                if(matcher.find()) {
                    pathList.add(path);
                }
            });
        }

        return pathList;
    }

    public List<Path> searchLogListByPattern(String pattern, LogType logType) throws IOException {
        List<Path> pathList = new ArrayList<>();
        Pattern regex = Pattern.compile(pattern);

        Path logTargetDirPath = Paths.get(logManageService.logTypeDirMap.get(logType));

        try (Stream<Path> paths = Files.list(logTargetDirPath)) {
            paths.filter(Files::isRegularFile).forEach(path -> {
                Matcher matcher = regex.matcher((CharSequence) path.getFileName());

                if(matcher.find()) {
                    pathList.add(path);
                }
            });
        }

        return pathList;
    }
}

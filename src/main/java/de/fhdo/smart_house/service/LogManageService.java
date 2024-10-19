package de.fhdo.smart_house.service;

import de.fhdo.smart_house.config.CustomProperties;
import de.fhdo.smart_house.util.CustomExceptionHandler;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Service
@Data
public class LogManageService {
    private final CustomProperties customProperties;

    public enum LogType {
        CHARGING_STATION,
        ENERGY_SOURCE,
        SYSTEM,
        DEFAULT,
        ARCHIVE
    }

    private final Map<LogType, String> logTypeDirMap;

    LogManageService(CustomProperties customProperties) {
        this.customProperties = customProperties;

        logTypeDirMap = Map.of(
                LogType.CHARGING_STATION, this.customProperties.getLogDir().getChargingStation(),
                LogType.ENERGY_SOURCE, this.customProperties.getLogDir().getEnergySource(),
                LogType.SYSTEM, this.customProperties.getLogDir().getSystem(),
                LogType.DEFAULT, this.customProperties.getLogDir().getDefaultDir(),
                LogType.ARCHIVE, this.customProperties.getLogDir().getArchive()
        );

        try {
            this.init();
        } catch (CustomExceptionHandler.LogException logException) {
            new CustomExceptionHandler.LogHandler(logException.getCustomMessage()).handle();
        }

    }

    private void init() throws CustomExceptionHandler.LogException {
        for (Map.Entry<LogType, String> entry : logTypeDirMap.entrySet()) {
            Path path = Paths.get(entry.getValue());
            try {
                if (!Files.exists(path) || !Files.isDirectory(path)) {
                    Files.createDirectories(path);
                }
            } catch (IOException e) {
                String customExceptionMessage = "There is a Exception when creating log directories at the init phase of LogManageService";
                throw new CustomExceptionHandler.LogException(e.getMessage(), e, customExceptionMessage);
            }
        }
    }

    public void addContentToLog(LogType logType, String equipmentName, String content) throws CustomExceptionHandler.LogException {
        String logName = generateLogName(equipmentName);
        Path logFilePath = Paths.get(logTypeDirMap.get(logType)).resolve(logName);

        try (BufferedWriter writer = Files.newBufferedWriter(logFilePath, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            writer.write(content);
        } catch (IOException e) {
            String customExceptionMessage = String.format("There is a Exception when adding content to log: %s \nAnd content is: %s", logFilePath.getFileName(), content);

            throw new  CustomExceptionHandler.LogException(e.getMessage(), e, customExceptionMessage);
        }
    }

    public void moveLog(Path logPath, Path targetDirPath) throws CustomExceptionHandler.LogException, FileNotFoundException {

        if (!Files.exists(logPath)) {
            throw new FileNotFoundException("The log file does not exist!");
        }

        if (!Files.exists(targetDirPath)) {
            try {
                Files.createDirectories(targetDirPath);
            } catch (IOException e) {
                String customExceptionMessage = String.format("There is a Exception when creating log directory before the moving of log, the target directory is: %s", targetDirPath);
                throw new CustomExceptionHandler.LogException(e.getMessage(), e, customExceptionMessage);
            }
        }

        try {
            Files.move(logPath, targetDirPath.resolve(logPath.getFileName()), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            String customExceptionMessage = String.format("There is a Exception when moving log: %s\nAnd the target directory is: %s", logPath.getFileName(), targetDirPath);
            throw new CustomExceptionHandler.LogException(e.getMessage(), e, customExceptionMessage);
        }

    }

    public void deleteLog(Path logPath) throws CustomExceptionHandler.LogException {
        if (!Files.exists(logPath)) {
            throw new CustomExceptionHandler.LogException("The log file does not exist!", null, "Log file not found: " + logPath.getFileName());
        }

        try {
            Files.delete(logPath);
        } catch (IOException e) {
            String customExceptionMessage = String.format("There is a Exception when deleting the log: %s", logPath.getFileName());
            throw new CustomExceptionHandler.LogException(e.getMessage(), e, customExceptionMessage);
        }
    }

    public void archiveLog(Path logPath) throws IOException {
        if (!Files.exists(logPath)) {
            throw new FileNotFoundException("The log file does not exist!");
        }

        Path archivePath = Paths.get(logTypeDirMap.get(LogType.ARCHIVE));

        if (!Files.exists(archivePath)) {
            Files.createDirectories(archivePath);
        }

        try {
            this.moveLog(logPath, archivePath);
        } catch (CustomExceptionHandler.LogException logException) {
            new CustomExceptionHandler.LogHandler(logException.getCustomMessage()).handle();
        }

    }

    public static String generateLogName(String equipmentName) {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        return equipmentName + "_" + date + ".log";
    }
}
package de.fhdo.smart_house.service;

import de.fhdo.smart_house.config.CustomProperties;
import de.fhdo.smart_house.util.CustomExceptionHandler;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Service
public class LogManageService {
    private final CustomProperties.LogDir logDir;

    public enum LogType {
        CHARGING_STATION,
        ENERGY_SOURCE,
        SYSTEM,
        DEFAULT,
        ARCHIVE
    }

    public final Map<LogType, String> logTypeDirMap;

    LogManageService(CustomProperties customProperties) {
        this.logDir = customProperties.getLogDir();

        logTypeDirMap = Map.of(
                LogType.CHARGING_STATION, this.logDir.getChargingStation(),
                LogType.ENERGY_SOURCE, this.logDir.getEnergySource(),
                LogType.SYSTEM, this.logDir.getSystem(),
                LogType.DEFAULT, this.logDir.getDefaultDir(),
                LogType.ARCHIVE, this.logDir.getArchive());

        this.init();
    }

    private void init() {
        for (Map.Entry<LogType, String> entry : logTypeDirMap.entrySet()) {
            Path path = Paths.get(entry.getValue());
            try {
                if (!Files.exists(path) || !Files.isDirectory(path)) {
                    Files.createDirectories(path);
                }
            } catch (IOException e) {
                e.printStackTrace();
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

            CustomExceptionHandler.LogException logException = new  CustomExceptionHandler.LogException(e.getMessage(), e, customExceptionMessage);
            CustomExceptionHandler.LogHandler(logException);

            throw logException;
        }
    }

    public void moveLog(Path logPath, Path targetDirPath) throws IOException {

        System.out.println(logPath);

        if (!Files.exists(logPath)) {
            throw new FileNotFoundException("The log file does not exist!");
        }

        if (!Files.exists(targetDirPath)) {
            Files.createDirectories(targetDirPath);
        }

        Files.move(logPath, targetDirPath.resolve(logPath.getFileName()), StandardCopyOption.REPLACE_EXISTING);
    }

    public void deleteLog(Path logPath) throws IOException {

        Files.deleteIfExists(logPath);
    }

    public void archiveLog(Path logPath) throws IOException {
        if (!Files.exists(logPath)) {
            throw new FileNotFoundException("The log file does not exist!");
        }

        Path archivePath = Paths.get(logTypeDirMap.get(LogType.ARCHIVE));

        if (!Files.exists(archivePath)) {
            Files.createDirectories(archivePath);
        }

        Files.move(logPath, archivePath.resolve(logPath.getFileName()), StandardCopyOption.REPLACE_EXISTING);
    }

    public static String generateLogName(String equipmentName) {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        return equipmentName + "_" + date + ".log";
    }
}
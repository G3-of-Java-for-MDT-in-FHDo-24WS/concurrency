package de.fhdo.smart_house.service;

import de.fhdo.smart_house.config.CustomProperties;
import de.fhdo.smart_house.util.CustomExceptionHandler.LogException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class LogManageServiceTest {

    private CustomProperties customProperties;
    private LogManageService logManageService;
    private Path mockBasePath;
    private Path mockChargingStationPath;

    private void setupCommonMocks() throws IOException, LogException {
        mockBasePath = Files.createTempDirectory("mock_logs");
        mockChargingStationPath = Files.createDirectory(mockBasePath.resolve("charging_station"));

        customProperties = new CustomProperties();
        CustomProperties.LogDir logDir = new CustomProperties.LogDir();
        
        logDir.setChargingStation(mockChargingStationPath.toString());
        logDir.setBase(mockBasePath.toString());
        logDir.setArchive(mockBasePath.toString());
        logDir.setDefaultDir(mockBasePath.toString());
        logDir.setEnergySource(mockBasePath.toString());
        logDir.setSystem(mockBasePath.toString());
        customProperties.setLogDir(logDir);
    }

    @BeforeEach
    void setUp() throws IOException, LogException {
        setupCommonMocks();

        // Initialize logManageService with mock values
        logManageService = new LogManageService(customProperties);
    }

    private void deleteDirectory(Path directoryPath) throws IOException {
        if (Files.exists(directoryPath)) {
            Files.walk(directoryPath)
                    .sorted((p1, p2) -> p2.compareTo(p1))
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException e) {
                            System.err.println("Unable to delete: " + path + " " + e.getMessage());
                        }
                    });
        }
    }

    @AfterEach
    void tearDown() throws IOException {
        deleteDirectory(mockChargingStationPath);
        deleteDirectory(mockBasePath);
    }

    @Test
    void testAddContentToLog_Success() throws LogException, IOException {
        String logContent = "Test log content";
        logManageService.addContentToLog(LogManageService.LogType.CHARGING_STATION, "station_001", logContent);

        Path logFilePath = Paths.get(mockChargingStationPath.toString(), "station_001_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".log");
        assertTrue(Files.exists(logFilePath));
        assertEquals(logContent, Files.readString(logFilePath));
    }

    @Test
    void testMoveLog_Success() throws LogException, IOException {
        Path logFilePath = Files.createFile(mockChargingStationPath.resolve("log_to_move.log"));
        Path targetDirPath = Files.createDirectory(mockBasePath.resolve("archive"));

        logManageService.moveLog(logFilePath, targetDirPath);

        assertTrue(Files.exists(targetDirPath.resolve("log_to_move.log")));
        assertFalse(Files.exists(logFilePath));
    }

    @Test
    void testMoveLog_ThrowsLogException() throws IOException {
        Path logFilePath = mockChargingStationPath.resolve("nonexistent.log");
        Path targetDirPath = mockBasePath.resolve("archive");

        assertThrows(FileNotFoundException.class, () -> logManageService.moveLog(logFilePath, targetDirPath));
    }

    @Test
    void testDeleteLog_Success() throws IOException, LogException {
        Path logFilePath = Files.createFile(mockChargingStationPath.resolve("log_to_delete.log"));

        logManageService.deleteLog(logFilePath);

        assertFalse(Files.exists(logFilePath));
    }

    @Test
    void testDeleteLog_ThrowsLogException() throws IOException {
        Path logFilePath = mockChargingStationPath.resolve("nonexistent.log");

        assertThrows(LogException.class, () -> logManageService.deleteLog(logFilePath));
    }
}

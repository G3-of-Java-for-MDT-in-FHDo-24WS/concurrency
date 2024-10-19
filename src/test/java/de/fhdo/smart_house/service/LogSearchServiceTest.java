package de.fhdo.smart_house.service;

import de.fhdo.smart_house.config.CustomProperties;
import de.fhdo.smart_house.util.CustomExceptionHandler.LogException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LogSearchServiceTest {

    private CustomProperties customProperties;
    private LogManageService logManageService;
    private LogSearchService logSearchService;
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

        logManageService = new LogManageService(customProperties);
        logSearchService = new LogSearchService(customProperties, logManageService);
        
        logManageService.addContentToLog(LogManageService.LogType.CHARGING_STATION, "charging_station_0001", "charging_station_0001 log for testing!");
        logManageService.addContentToLog(LogManageService.LogType.CHARGING_STATION, "charging_station_0002", "charging_station_0002 log for testing!");
        logManageService.addContentToLog(LogManageService.LogType.CHARGING_STATION, "charging_station_0003", "charging_station_0003 log for testing!");
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
    void testSearchLogListByPattern_ValidPattern() throws IOException {
        List<Path> result1 = logSearchService.searchLogListByPattern("charging_station");
        List<Path> result2 = logSearchService.searchLogListByPattern("2024-10");
        

        assertTrue(!result1.isEmpty());
        assertTrue(!result2.isEmpty());
    }

    @Test
    void testSearchLogListByPattern_NoMatches() throws IOException {
        List<Path> result = logSearchService.searchLogListByPattern("nonexistent");

        assertTrue(result.isEmpty());
    }

    @Test
    void testSearchLogListByPatternWithLogType_ValidPattern() throws IOException {
        List<Path> result1 = logSearchService.searchLogListByPattern("charging_station", LogManageService.LogType.CHARGING_STATION);
        List<Path> result2 = logSearchService.searchLogListByPattern("2024-10", LogManageService.LogType.CHARGING_STATION);

        assertTrue(!result1.isEmpty());
        assertTrue(!result2.isEmpty());
    }

    @Test
    void testSearchLogListByPatternWithLogType_NoMatches() throws IOException {
        List<Path> result = logSearchService.searchLogListByPattern("nonexistent", LogManageService.LogType.CHARGING_STATION);

        assertTrue(result.isEmpty());
    }

    @Test
    void testSearchLogListByPattern_ThrowsIOException() throws IOException {
        try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
            mockedFiles.when(() -> Files.walk(any(Path.class))).thenThrow(new IOException("File system error"));

            assertThrows(IOException.class, () -> {
            	logSearchService.searchLogListByPattern(".*");
            });
        }
    }
}
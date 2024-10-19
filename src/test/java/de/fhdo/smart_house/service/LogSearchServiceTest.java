package de.fhdo.smart_house.service;

import de.fhdo.smart_house.config.CustomProperties;
import de.fhdo.smart_house.util.CustomExceptionHandler.LogException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
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

    @Mock
    private CustomProperties mockCustomProperties;

    @Mock
    private LogManageService mockLogManageService;

    @InjectMocks
    private LogSearchService mockLogSearchService;
    
    private CustomProperties.LogDir logDir;

    private Path mockBasePath;
    private Path mockChargingStationPath;

    private void setupCommonMocks() throws IOException, LogException {
        mockBasePath = Files.createTempDirectory("mock_logs");
        mockChargingStationPath = Files.createDirectory(mockBasePath.resolve("charging_station"));

        logDir = new CustomProperties.LogDir();
        logDir.setChargingStation(mockChargingStationPath.toString());
        logDir.setBase(mockBasePath.toString());        
    }

    @BeforeEach
    void setUp() throws IOException, LogException {
        setupCommonMocks();

        lenient().when(mockCustomProperties.getLogDir()).thenReturn(logDir);
        lenient().when(mockLogManageService.getLogTypeDirMap()).thenReturn(Map.of(
                LogManageService.LogType.CHARGING_STATION, logDir.getChargingStation()
        ));
        
//        mockLogManageService.addContentToLog(LogManageService.LogType.CHARGING_STATION, "charging_station_0001", "charging_station_0001 log for testing!");
//        mockLogManageService.addContentToLog(LogManageService.LogType.CHARGING_STATION, "charging_station_0002", "charging_station_0002 log for testing!");
//        mockLogManageService.addContentToLog(LogManageService.LogType.CHARGING_STATION, "charging_station_0003", "charging_station_0003 log for testing!");
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
        List<Path> result1 = mockLogSearchService.searchLogListByPattern("charging_station_01");
        List<Path> result2 = mockLogSearchService.searchLogListByPattern("2024-10");
        

        assertTrue(!result1.isEmpty());
        assertTrue(!result2.isEmpty());
    }

    @Test
    void testSearchLogListByPattern_NoMatches() throws IOException {
        List<Path> result = mockLogSearchService.searchLogListByPattern("nonexistent");

        assertTrue(result.isEmpty());
    }

    @Test
    void testSearchLogListByPatternWithLogType_ValidPattern() throws IOException {
        List<Path> result1 = mockLogSearchService.searchLogListByPattern("charging_station_01", LogManageService.LogType.CHARGING_STATION);
        List<Path> result2 = mockLogSearchService.searchLogListByPattern("2024-10", LogManageService.LogType.CHARGING_STATION);

        assertTrue(!result1.isEmpty());
        assertTrue(!result2.isEmpty());
    }

    @Test
    void testSearchLogListByPatternWithLogType_NoMatches() throws IOException {
        List<Path> result = mockLogSearchService.searchLogListByPattern("nonexistent", LogManageService.LogType.CHARGING_STATION);

        assertTrue(result.isEmpty());
    }

    @Test
    void testSearchLogListByPattern_ThrowsIOException() throws IOException {
        try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
            mockedFiles.when(() -> Files.walk(any(Path.class))).thenThrow(new IOException("File system error"));

            assertThrows(IOException.class, () -> {
                mockLogSearchService.searchLogListByPattern(".*");
            });
        }
    }
}
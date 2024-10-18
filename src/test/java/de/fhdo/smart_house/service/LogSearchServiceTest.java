package de.fhdo.smart_house.service;

import de.fhdo.smart_house.config.CustomProperties;
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
    private CustomProperties customProperties;

    @Mock
    private LogManageService logManageService;

    @InjectMocks
    private LogSearchService logSearchService;

    private CustomProperties.LogDir logDir;

    private Path mockBasePath;
    private Path mockChargingStationPath;

    private void setupCommonMocks() throws IOException {
        mockBasePath = Files.createTempDirectory("mock_logs");
        mockChargingStationPath = Files.createDirectory(mockBasePath.resolve("charging_station"));

        logDir = new CustomProperties.LogDir();
        logDir.setChargingStation(mockChargingStationPath.toString());
        logDir.setBase(mockBasePath.toString());
    }

    @BeforeEach
    void setUp() throws IOException {
        setupCommonMocks();

        lenient().when(customProperties.getLogDir()).thenReturn(logDir);
        lenient().when(logManageService.getLogTypeDirMap()).thenReturn(Map.of(
                LogManageService.LogType.CHARGING_STATION, mockChargingStationPath.toString()
        ));
    }

    private void deleteDirectory(Path directoryPath) throws IOException {
        if (Files.exists(directoryPath)) {
            Files.walk(directoryPath)
                    .sorted((p1, p2) -> p2.compareTo(p1)) // 反向排序
                    .forEach(path -> {
                        try {
                            Files.delete(path); // 删除文件或目录
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
        List<Path> result = logSearchService.searchLogListByPattern(".*\\.log");

        assertNotNull(result);
    }

    @Test
    void testSearchLogListByPattern_NoMatches() throws IOException {
        List<Path> result = logSearchService.searchLogListByPattern("nonexistent");

        assertTrue(result.isEmpty(), "Expected no matches for the nonexistent pattern");
    }

    @Test
    void testSearchLogListByPatternWithLogType_ValidPattern() throws IOException {
        List<Path> result = logSearchService.searchLogListByPattern(".*info.*", LogManageService.LogType.CHARGING_STATION);

        assertNotNull(result);
    }

    @Test
    void testSearchLogListByPatternWithLogType_NoMatches() throws IOException {
        List<Path> result = logSearchService.searchLogListByPattern("nonexistent", LogManageService.LogType.CHARGING_STATION);

        assertTrue(result.isEmpty(), "Expected no matches for the nonexistent pattern");
    }

    @Test
    void testSearchLogListByPattern_ThrowsIOException() throws IOException {
        System.out.println(customProperties.getLogDir().getBase());
        try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
            mockedFiles.when(() -> Files.walk(any(Path.class))).thenThrow(new IOException("File system error"));

            assertThrows(IOException.class, () -> {
                logSearchService.searchLogListByPattern(".*");
            });
        }
    }
}

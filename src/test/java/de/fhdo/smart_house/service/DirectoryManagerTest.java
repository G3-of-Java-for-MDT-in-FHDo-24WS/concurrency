  package de.fhdo.smart_house.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DirectoryManagerTest {

    private DirectoryManager directoryManager;
    private Path tempDir;

    @BeforeEach
    void setUp() throws IOException {
        directoryManager = new DirectoryManager();
        tempDir = Files.createTempDirectory("testDir");
    }

    @AfterEach
    void tearDown() throws IOException {
        deleteDirectory(tempDir);
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

    @Test
    void testCreateDirectory_Success() throws IOException {
        Path newDir = tempDir.resolve("newDir");
        directoryManager.createDirectory(newDir);

        assertTrue(Files.exists(newDir), "Directory should have been created");
    }

    @Test
    void testCreateDirectory_AlreadyExists() throws IOException {
        Path existingDir = tempDir.resolve("existingDir");
        Files.createDirectory(existingDir);

        // Call the method that should handle existing directories gracefully
        directoryManager.createDirectory(existingDir);

        // Verify that the directory still exists
        assertTrue(Files.exists(existingDir), "Directory should still exist");

        // Optionally, verify no new creation attempt (if applicable)
        // You may mock the behavior if your method calls Files.createDirectories
    }
    
    @Test
    void testCreateDirectory_ThrowsIOException() {
        Path invalidPath = Path.of("/invalid/path");

        // Mock static methods in the Files class
        try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
            // Set up mock behavior to throw IOException
            mockedFiles.when(() -> Files.createDirectories(invalidPath)).thenThrow(new IOException("Invalid path"));

            // Verify that the exception is thrown
            IOException thrown = assertThrows(IOException.class, () -> {
                directoryManager.createDirectory(invalidPath);
            });

            // Verify the exception message
            assertEquals("Could not create directory: " + invalidPath.toString(), thrown.getMessage());
        }
    }


    @Test
    void testCreateDirectory_NullPath() {
        assertThrows(NullPointerException.class, () -> {
            directoryManager.createDirectory(null);
        });
    }

    @Test
    void testCreateDirectory_ThrowsIOExceptionOnCreationFailure() {
        Path mockPath = mock(Path.class);

        // Mock static methods in the Files class
        try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
            // Set up mock behavior
            when(Files.exists(mockPath)).thenReturn(false);
            mockedFiles.when(() -> Files.createDirectories(mockPath)).thenThrow(new IOException("Creation failed"));

            // Verify that the exception is thrown
            IOException thrown = assertThrows(IOException.class, () -> {
                directoryManager.createDirectory(mockPath);
            });

            // Verify the exception message
            assertEquals("Could not create directory: " + mockPath.toString(), thrown.getMessage());

            // Verify the static method calls directly
            mockedFiles.verify(() -> Files.exists(mockPath));
            mockedFiles.verify(() -> Files.createDirectories(mockPath));
        }
    }
}

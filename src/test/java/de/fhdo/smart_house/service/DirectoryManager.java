package de.fhdo.smart_house.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class DirectoryManager {

    public void createDirectory(Path path) throws IOException {
        try {
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
        } catch (IOException e) {
            System.err.println("Failed to create directory: " + e.getMessage());
            throw new IOException("Could not create directory: " + path, e); // Re-throwing the exception
        }
    }
}

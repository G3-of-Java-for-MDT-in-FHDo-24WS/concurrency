package basicio;

import java.io.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogManager {

    private final String logDirectory = "logs/";

    public LogManager() {
        File dir = new File(logDirectory);
        if (!dir.exists()) {
            dir.mkdirs(); // Create the logs directory if it doesn't exist
        }
    }

    public void createLog(String fileName, String content) throws IOException {
        File logFile = new File(logDirectory + fileName);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFile))) {
            writer.write(content);
        }
    }

    public void moveLog(String fileName, String targetDirectory) throws IOException {
        File logFile = new File(logDirectory + fileName);
        File targetDir = new File(targetDirectory);
        if (!targetDir.exists()) {
            targetDir.mkdirs();
        }
        Files.move(logFile.toPath(), Paths.get(targetDirectory + fileName), StandardCopyOption.REPLACE_EXISTING);
    }

    public void deleteLog(String fileName) {
        File logFile = new File(logDirectory + fileName);
        if (logFile.exists()) {
            logFile.delete();
        }
    }

    public void archiveLog(String fileName) throws IOException {
        File logFile = new File(logDirectory + fileName);
        if (logFile.exists()) {
            File archiveDir = new File(logDirectory + "archive/");
            if (!archiveDir.exists()) {
                archiveDir.mkdirs();
            }
            Files.move(logFile.toPath(), Paths.get(logDirectory + "archive/" + fileName), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    // Create a log name using current date
    public String generateLogName(String equipmentName) {
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        return equipmentName + "_" + date + ".log";
    }
}

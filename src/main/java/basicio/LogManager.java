package basicio;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import io.github.cdimascio.dotenv.Dotenv;

public class LogManager {
	private static final Dotenv dotenv = Dotenv.load();
    public static enum LogType {
        CHARGING_STATION,
        ENERGY_SOURCE,
        WHOLE_SYSTEM,
        DEFAULT,
        ARCHIVE
    }
    public final static Map<LogType, String> logTypeDirMap = Map.of(
    		LogType.CHARGING_STATION, dotenv.get("LOG_DIR_CHARGING_STATION"),
    		LogType.ENERGY_SOURCE, dotenv.get("LOG_DIR_CHARGING_STATION"),
    		LogType.WHOLE_SYSTEM, dotenv.get("LOG_DIR_CHARGING_STATION"),
    		LogType.DEFAULT, dotenv.get("LOG_DIR_CHARGING_STATION"),
    		LogType.ARCHIVE, dotenv.get("LOG_DIR_ARCHIVE"));
    
    private final Path logDirPath;

    public LogManager(LogType logType) {
    	logDirPath = Paths.get(logTypeDirMap.get(logType));

        if (!Files.exists(logDirPath) || !Files.isDirectory(logDirPath)) {
            try {
				Files.createDirectories(logDirPath);
				
				System.out.format("Log manager %s has created!", logType.toString());
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
    }

    public Path addContentToLog(String equipmentName, String content) {
    	String logName = generateLogName(equipmentName);
        Path logFilePath = logDirPath.resolve(logName);

        try (BufferedWriter writer = Files.newBufferedWriter(logFilePath, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            writer.write(content);
            return logFilePath;
        } catch (IOException e) {
        	System.err.format("Error during adding content to %s: %s", logName, e.getMessage());
        } 
        
        return logFilePath;
    }

    public void moveLog(Path logPath, Path targetDirPath) throws IOException {
        
        System.out.println(logPath);
        
        if(!Files.exists(logPath)) {
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

    public void archiveLog(Path logPath) throws IOException {;
        
        if(!Files.exists(logPath)) {
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
package basicio;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import io.github.cdimascio.dotenv.Dotenv;

public class LogManager {
    public static enum LogType {
        CHARGING_STATION,
        ENERGY_SOURCE,
        WHOLE_SYSTEM,
        DEFAULT
    }

    private static final Dotenv dotenv = Dotenv.load();
    private final Path logDirPath;
    private final static Path logArchiveDirPath = Paths.get(dotenv.get("LOG_DIR_ARCHIVE"));
    

    public LogManager(LogType logType) {
        switch (logType) {
            case CHARGING_STATION: {
            	logDirPath = Paths.get(dotenv.get("LOG_DIR_CHARGING_STATION"));
                break;
            }
            case ENERGY_SOURCE: {
            	logDirPath = Paths.get(dotenv.get("LOG_DIR_ENERGY_SOURCE"));
                break;
            }
            case WHOLE_SYSTEM: {
            	logDirPath = Paths.get(dotenv.get("LOG_DIR_WHOLE_SYSTEM"));
                break;
            }
            case DEFAULT:
            default: {
            	logDirPath = Paths.get(dotenv.get("LOG_DIR_DEFAULT"));
                break;
            }
        }

        if (!Files.exists(logDirPath) || !Files.isDirectory(logDirPath)) {
            try {
				Files.createDirectories(logDirPath);
				
				System.out.format("Log manager %s has created!", logType.toString());
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
    }

    public void createLog(String fileName, String content) throws IOException {
        Path logFilePath = logDirPath.resolve(fileName);

        try (BufferedWriter writer = Files.newBufferedWriter(logFilePath, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            writer.write(content);
        }
    }
    
    public void appendLogContent(String fileName, String content) throws IOException {
    	Path logFilePath = logDirPath.resolve(fileName);
    	
    	if(!Files.exists(logFilePath)) {
    		throw new FileNotFoundException();
    	}
    	
    	try(BufferedWriter writer = Files.newBufferedWriter(logFilePath, StandardOpenOption.CREATE )){
    		writer.write(content);
    	}
    }

    public void moveLog(String fileName, String targetDir) throws IOException {
        Path logFilePath = logDirPath.resolve(fileName);
        Path targetDirPath = Paths.get(targetDir);
        
        if(!Files.exists(logFilePath)) {
        	throw new FileNotFoundException("The log file does not exist!");
        }

        if (!Files.exists(targetDirPath)) {
            Files.createDirectories(targetDirPath);
        }
        
        Files.move(logFilePath, targetDirPath.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
    }

    public void deleteLog(String fileName) throws IOException {
        Path logFilePath = logDirPath.resolve(fileName);
        
        Files.deleteIfExists(logFilePath);
    }

    public void archiveLog(String fileName) throws IOException {
        Path logFilePath = logDirPath.resolve(fileName);
        
        if(!Files.exists(logFilePath)) {
        	throw new FileNotFoundException("The log file does not exist!");
        }
        
        Path archivePath = logArchiveDirPath;
        
        if (!Files.exists(archivePath)) {
            Files.createDirectories(archivePath);
        }
        
        Files.move(logFilePath, archivePath.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
    }

    public static String generateLogName(String equipmentName) {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        
        return equipmentName + "_" + date + ".log";
    }
}
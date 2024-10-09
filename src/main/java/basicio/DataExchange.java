package basicio;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Random;

import basicio.LogManager.LogType;
import io.github.cdimascio.dotenv.Dotenv;

public class DataExchange {
	private final static LogManager chargingStationLogManager = new LogManager(LogType.CHARGING_STATION);
	
    public static Path sendSensorData(String targetFile) {
    	Path targetPath = Paths.get(targetFile);
    	
    	if(!Files.exists(targetPath.getParent())) {
    		try {
				Files.createDirectories(targetPath.getParent());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	
        try (BufferedOutputStream out = new BufferedOutputStream(Files.newOutputStream(targetPath))) {
            byte[] sensorData = generateSensorData(256);
            out.write(sensorData);
            System.out.println("Sensor data sent to " + targetPath.getFileName());
        } catch (IOException e) {
        	System.err.println("Error during sending sensor data: " + e.getMessage());
        } 
        
        return targetPath;
    }
    
    public static void receiveSensorData(String sourceFile) throws IOException {
    	Path sourceFilePath = Paths.get(sourceFile);
    	
    	if(!Files.exists(sourceFilePath)) {
    		throw new FileNotFoundException("The source file was not found!");
    	}
    	
    	if(!Files.isRegularFile(sourceFilePath)) {
    		throw new IOException("The source file is not a regular file!");
    	}
    	
        try (BufferedInputStream in = new BufferedInputStream(Files.newInputStream(sourceFilePath))) {
            byte[] receivedData = in.readAllBytes();
            processSensorData(receivedData);
            System.out.println("Sensor data received from " + sourceFilePath.getFileName());
        }
    }
	
	
    private static byte[] generateSensorData(int length) {
        byte[] data = new byte[length]; 
        new Random().nextBytes(data);
        return data;
    }

    private static void processSensorData(byte[] data) {
        System.out.println("Processing sensor data of size: " + data.length + " bytes");
    }
    
    
    public static Path wirteChargingStationLog(String chargingStationName, String message) {
    	String logName = LogManager.generateLogName(chargingStationName);
		return chargingStationLogManager.addContentToLog(logName, message);
    }
    
    public static void readChargingStationLog(Path logPath) throws IOException {
    	if(!Files.exists(logPath)) {
    		throw new FileNotFoundException(logPath + " was not found!");
    	}
    	
    	if(!Files.isRegularFile(logPath)) {
    		throw new IOException(logPath + " is not a regular file!");
    	}
    	
        try (BufferedReader reader = Files.newBufferedReader(logPath)) {
        	String line;
        	while((line = reader.readLine()) != null) {
        		System.out.println(line);
        	}
        } catch(IOException e) {
        	System.err.format("Error during reading %s!%n", logPath.getFileName());
        }
    }
	
}

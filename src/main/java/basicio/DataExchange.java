package basicio;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;

import basicio.LogManager.LogType;
import io.github.cdimascio.dotenv.Dotenv;

public class DataExchange {
    private static final Dotenv dotenv = Dotenv.load();
	private final static Path logChargingStationDirPath = Paths.get(dotenv.get("LOG_DIR_CHARGING_STATION"));
	private final static LogManager chargingStationLogManager = new LogManager(LogType.CHARGING_STATION);
	
    public void sendSensorData(Path targetPath) {
        try (BufferedOutputStream out = new BufferedOutputStream(Files.newOutputStream(targetPath))) {
            byte[] sensorData = generateSensorData(256);
            out.write(sensorData);
            System.out.println("Sensor data sent to " + targetPath.getFileName());
        } catch (IOException e) {
        	System.err.println("Error during sending sensor data: " + e.getMessage());
        }
    }
    
    public void receiveSensorData(Path sourcePath) throws IOException {
    	if(!Files.exists(sourcePath)) {
    		throw new FileNotFoundException("The source file was not found!");
    	}
    	
    	if(!Files.isRegularFile(sourcePath)) {
    		throw new IOException("The source file is not a regular file!");
    	}
    	
        try (BufferedInputStream in = new BufferedInputStream(Files.newInputStream(sourcePath))) {
            byte[] receivedData = in.readAllBytes();
            processSensorData(receivedData);
            System.out.println("Sensor data received from " + sourcePath.getFileName());
        }
    }
	
	
    private byte[] generateSensorData(int length) {
        byte[] data = new byte[length]; 
        new Random().nextBytes(data);
        return data;
    }

    private void processSensorData(byte[] data) {
        System.out.println("Processing sensor data of size: " + data.length + " bytes");
    }
    
    
    public void wirteChargingStationLog(String chargingStationName, String message) {
		chargingStationLogManager.addContentToLog(chargingStationName, message);
    }
    
    public void readChargingStationLog(String logName) throws IOException {
    	Path logPath = logChargingStationDirPath.resolve(logName);
    	
    	if(!Files.exists(logPath)) {
    		throw new FileNotFoundException(logName + " was not found!");
    	}
    	
    	if(!Files.isRegularFile(logPath)) {
    		throw new IOException(logName + " is not a regular file!");
    	}
    	
        try (BufferedReader reader = Files.newBufferedReader(logPath)) {
        	String line;
        	while((line = reader.readLine()) != null) {
        		System.out.println(line);
        	}
        } catch(IOException e) {
        	System.err.format("Error during reading %s!%n", logName);
        }
    }
	
}

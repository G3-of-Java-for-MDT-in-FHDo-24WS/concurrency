package basicio;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class EnergyManagementSystem {
	public static void main(String[] args) throws IOException {
        LogManager logManager = new LogManager(LogManager.LogType.CHARGING_STATION);
        String equipmentName = "ChargingStation1";

        // Create log for a specific equipment
        Path logPath = logManager.addContentToLog(equipmentName, "Charging session started.\n");
        
        logManager.moveLog(logPath, Paths.get("logsDirToMove"));
        
        logManager.addContentToLog(equipmentName, "Charging session started.\n");
        logManager.deleteLog(logPath);
        
        logManager.addContentToLog(equipmentName, "Charging session started.\n");
        logManager.archiveLog(logPath);
        
        Path sensorData = DataExchange.sendSensorData("temp_data/sensor.temp");
        DataExchange.receiveSensorData(sensorData.toString());
        
        Path chargingStationLogPath = DataExchange.wirteChargingStationLog(equipmentName, "Charging session started.\n");
        DataExchange.readChargingStationLog(chargingStationLogPath);

        // Search for logs by name or date using regex
        LogSearcher searcher = new LogSearcher();
        searcher.searchLogByPattern("logs/charging_station", "ChargingStation1_\\d{4}-\\d{2}-\\d{2}");
    }
}
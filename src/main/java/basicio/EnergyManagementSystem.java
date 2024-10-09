package basicio;

import java.io.IOException;

public class EnergyManagementSystem {
	public static void main(String[] args) throws IOException {
        LogManager logManager = new LogManager();
        String equipmentName = "ChargingStation1";
        String logFileName = logManager.generateLogName(equipmentName);

        // Create log for a specific equipment
        logManager.createLog(logFileName, "Charging session started.");

        // Search for logs by name or date using regex
        LogSearcher searcher = new LogSearcher();
        searcher.searchLogByPattern("ChargingStation1_\\d{4}-\\d{2}-\\d{2}");
    }
}
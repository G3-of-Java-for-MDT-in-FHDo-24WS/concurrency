package de.fhdo.smart_house.service;

import de.fhdo.smart_house.entity.Battery;
import de.fhdo.smart_house.entity.EnergyConsumer;
import de.fhdo.smart_house.entity.EnergySource;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class BatteryManagerServiceConcurrencyTest {
    private BatteryManagerService batteryManagerService;
    private static final ArrayList<Integer> BATTERY_CAPACITIES = new ArrayList<>(Arrays.asList(1000, 600, 800));
    
    @BeforeEach
    void setUp() {
        batteryManagerService = new BatteryManagerService(BATTERY_CAPACITIES);
    }

    @AfterEach
    void tearDown() {
        System.out.println("Battery capacities: " + batteryManagerService.getBatteriesString());
    }

    @Test
    void testConcurrentCharging() throws InterruptedException {
        System.out.println("Battery capacities: " + batteryManagerService.getBatteriesString());

        // Create three different power sources
        EnergySource solarSource = new EnergySource(EnergySource.SourceType.SOLAR, 100);
        EnergySource windSource = new EnergySource(EnergySource.SourceType.WIND, 150);
        EnergySource gridSource = new EnergySource(EnergySource.SourceType.GRID, 200);

        // Start charging tasks
        batteryManagerService.startChargingTask(solarSource);
        batteryManagerService.startChargingTask(windSource);
        batteryManagerService.startChargingTask(gridSource);

        // Wait for 5 seconds to allow charging
        Thread.sleep(5000);

        // Stop all operations
        batteryManagerService.shutdown();

        // Verify batteries have been charged
        for (Battery battery : batteryManagerService.getBatteries()) {
            assertTrue(battery.getCurrentCharge() > 0, "Battery should have been charged");
            assertTrue(battery.getCurrentCharge() <= battery.getCapacity(), 
                    "Battery charge should not exceed capacity");
        }
    }

    @Test
    void testConcurrentDischarging() throws InterruptedException {
        // First, fully charge all batteries
        for (Battery battery : batteryManagerService.getBatteries()) {
            battery.setCurrentCharge(battery.getCapacity());
        }

        System.out.println("Battery capacities: " + batteryManagerService.getBatteriesString());

        // Create multiple consumers with different power rates
        EnergyConsumer consumer1 = new EnergyConsumer(150);
        EnergyConsumer consumer2 = new EnergyConsumer(200);
        EnergyConsumer consumer3 = new EnergyConsumer(250);

        // Start discharging tasks
        batteryManagerService.startDischargingTask(consumer1);
        batteryManagerService.startDischargingTask(consumer2);
        batteryManagerService.startDischargingTask(consumer3);

        // Wait for 5 seconds to allow discharging
        Thread.sleep(5000);

        // Stop all operations
        batteryManagerService.shutdown();

        // Verify batteries have been discharged but not over-discharged
        for (Battery battery : batteryManagerService.getBatteries()) {
            assertTrue(battery.getCurrentCharge() >= 0, 
                    "Battery charge should not be negative");
            assertTrue(battery.getCurrentCharge() < battery.getCapacity(), 
                    "Battery should have been discharged");
        }
    }

    @Test
    void testSystemOverloadProtection() throws InterruptedException {
        EnergyConsumer consumer = new EnergyConsumer(600);
        
        batteryManagerService.startDischargingTask(consumer);

        // Wait for 2 seconds to observe system response
        Thread.sleep(2000);

        // Stop all operations
        batteryManagerService.shutdown();

        // Verify battery hasn't been over-discharged
        assertTrue(batteryManagerService.getCurrentCharge() >= 0, 
                "Battery should not be over-discharged");
    }
}

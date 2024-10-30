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
        System.out.println("@AfterEach - Battery capacities: " + batteryManagerService.getBatteriesString());
    }

    @Test
    void testConcurrentCharging() throws InterruptedException {
        System.out.println("Battery capacities: " + batteryManagerService.getBatteriesString());

        EnergySource solarSource = new EnergySource("solar", EnergySource.SourceType.SOLAR, 100);
        EnergySource windSource = new EnergySource("wind", EnergySource.SourceType.WIND, 150);
        EnergySource gridSource = new EnergySource("grid", EnergySource.SourceType.GRID, 200);

        batteryManagerService.startChargingTask(solarSource);
        batteryManagerService.startChargingTask(windSource);
        batteryManagerService.startChargingTask(gridSource);

        Thread.sleep(6000);

        batteryManagerService.shutdown();

        for (Battery battery : batteryManagerService.getBatteries()) {
            assertTrue(battery.getCurrentCharge() > 0, "Battery should have been charged");
            assertTrue(battery.getCurrentCharge() <= battery.getCapacity(), 
                    "Battery charge should not exceed capacity");
        }
    }

    @Test
    void testConcurrentDischarging() throws InterruptedException {
        for (Battery battery : batteryManagerService.getBatteries()) {
            battery.setCurrentCharge(battery.getCapacity());
        }

        System.out.println("Battery capacities: " + batteryManagerService.getBatteriesString());

        EnergyConsumer consumer1 = new EnergyConsumer("consumer1", 150);
        EnergyConsumer consumer2 = new EnergyConsumer("consumer2", 200);
        EnergyConsumer consumer3 = new EnergyConsumer("consumer3", 250);

        batteryManagerService.startDischargingTask(consumer1);
        batteryManagerService.startDischargingTask(consumer2);
        batteryManagerService.startDischargingTask(consumer3);

        Thread.sleep(4000);

        batteryManagerService.shutdown();

        for (Battery battery : batteryManagerService.getBatteries()) {
            assertTrue(battery.getCurrentCharge() >= 0, 
                    "Battery charge should not be negative");
            assertTrue(battery.getCurrentCharge() < battery.getCapacity(), 
                    "Battery should have been discharged");
        }
    }

    @Test
    void testSystemOverloadProtection() throws InterruptedException {
        EnergyConsumer consumer = new EnergyConsumer("consumer", 600);
        
        batteryManagerService.startDischargingTask(consumer);

        Thread.sleep(1000);

        batteryManagerService.shutdown();

        assertTrue(batteryManagerService.getCurrentCharge() >= 0, 
                "Battery should not be over-discharged");
    }
}

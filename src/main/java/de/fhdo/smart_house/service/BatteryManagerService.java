package de.fhdo.smart_house.service;

import lombok.Getter;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import de.fhdo.smart_house.entity.Battery;

public class BatteryManagerService {
    private static final Logger logger = Logger.getLogger(BatteryManagerService.class.getName());
    
    @Getter
    private final List<Battery> batteries;
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    public BatteryManagerService(List<Integer> batteryCapacities) {
        this.batteries = batteryCapacities.stream()
            .map(Battery::new)
            .collect(Collectors.toList());
    }

    //public void startChargingTask(EnergySource source) {
    //    scheduleTask(() -> chargeFromSource(source), "charging");
    //}

    //public void startDischargingTask(EnergyConsumer consumer) {
    //    scheduleTask(() -> supplyPowerToConsumer(consumer), "discharging");
    //}

    private void scheduleTask(Runnable task, String taskType) {
        scheduler.scheduleAtFixedRate(() -> {
            try {
                task.run();
            } catch (Exception e) {
                logger.severe("Error during " + taskType + ": " + e.getMessage());
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    public void shutdown() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(60, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
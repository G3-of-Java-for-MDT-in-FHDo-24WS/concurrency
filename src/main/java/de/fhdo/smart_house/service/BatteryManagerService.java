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
import java.util.Comparator;

import de.fhdo.smart_house.entity.Battery;
import de.fhdo.smart_house.entity.EnergyConsumer;
import de.fhdo.smart_house.entity.EnergySource;

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

    public void startChargingTask(EnergySource source) {
        scheduleTask(() -> chargeFromSource(source), "charging");
    }

    public void startDischargingTask(EnergyConsumer consumer) {
        scheduleTask(() -> supplyPowerToConsumer(consumer), "discharging");
    }

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

    private void chargeFromSource(EnergySource source) {
        lock.lock();
        try {
            int remainingPower = source.getChargeRatePerSecond();
            
            for (Battery battery : batteries) {
                int chargeAmount = calculateChargeAmount(battery, remainingPower);
                if (chargeAmount <= 0) continue;
                
                charge(battery, chargeAmount);
                remainingPower -= chargeAmount;
                logCharging(source.getSourceType().toString(), chargeAmount);
                
                if (remainingPower <= 0) break;
            }
        } finally {
            lock.unlock();
        }
    }

    private int calculateChargeAmount(Battery battery, int availablePower) {
        int spaceAvailable = battery.getCapacity() - battery.getCurrentCharge();
        return Math.min(availablePower, spaceAvailable);
    }

    private void charge(Battery battery, int amount) {
        try {
            while (!canCharge(battery, amount)) {
                condition.await();
            }
            battery.setCurrentCharge(battery.getCurrentCharge() + amount);
            condition.signalAll();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warning("Charging interrupted");
        }
    }

    private boolean canCharge(Battery battery, int amount) {
        return battery.getCurrentCharge() + amount <= battery.getCapacity();
    }

    private boolean supplyPowerToConsumer(EnergyConsumer consumer) {
        lock.lock();
        try {
            int remainingDemand = consumer.getConsumeRatePerSecond();
            
            while (remainingDemand > 0) {
                Battery battery = findBestBattery();
                if (battery == null) {
                    logInsufficientPower(consumer.getConsumeRatePerSecond());
                    return false;
                }

                int dischargeAmount = Math.min(battery.getCurrentCharge(), remainingDemand);
                discharge(battery, dischargeAmount);
                remainingDemand -= dischargeAmount;
                logDischarging(dischargeAmount);
            }
            return true;
        } finally {
            lock.unlock();
        }
    }

    private void discharge(Battery battery, int amount) {
        try {
            while (battery.getCurrentCharge() < amount) {
                if (!condition.await(100, TimeUnit.MILLISECONDS)) {
                    break;
                }
            }
            battery.setCurrentCharge(battery.getCurrentCharge() - amount);
            condition.signalAll();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warning("Discharging interrupted");
        }
    }

    private Battery findBestBattery() {
        return batteries.stream()
            .filter(b -> b.getCurrentCharge() > 0)
            .max(Comparator.comparingDouble(b -> 
                (double) b.getCurrentCharge() / b.getCapacity()))
            .orElse(null);
    }

    private void logCharging(String sourceType, int amount) {
        System.out.println(String.format("Source %s charged battery with %d units/s, current charge: %s", 
            sourceType, amount, getBatteriesString()));
    }

    private void logDischarging(int amount) {
        System.out.println(String.format("Battery discharged %d units for consumer, current charge :%s", amount, getBatteriesString()));
    }

    private void logInsufficientPower(int required) {
        System.out.println(String.format("Insufficient power for consumer requiring %d units/s, current charge: %s", required, getBatteriesString()));
    }

    public String getBatteriesString() {
        return "[" + batteries.stream().map(Battery::getCurrentCharge).map(String::valueOf).collect(Collectors.joining(", ")) + "]";
    }

    public int getCurrentCharge() {
        return batteries.stream().map(Battery::getCurrentCharge).reduce(0, Integer::sum);
    }
}

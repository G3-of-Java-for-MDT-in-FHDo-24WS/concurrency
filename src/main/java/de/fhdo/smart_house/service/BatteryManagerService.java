package de.fhdo.smart_house.service;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import de.fhdo.smart_house.entity.Battery;
import de.fhdo.smart_house.entity.EnergyConsumer;
import de.fhdo.smart_house.entity.EnergySource;
import lombok.Getter;

@Service
public class BatteryManagerService {
	@Getter
    private final List<Battery> batteries;
    private final ReentrantLock lock = new ReentrantLock();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    public BatteryManagerService(List<Integer> batteryCapacities) {
        batteries = batteryCapacities.stream()
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
                System.err.println("Error during " + taskType + ": " + e.getMessage());
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
    
    public String getBatteriesString() {
        return "[" + batteries.stream().map(Battery::getCurrentCharge).map(String::valueOf).collect(Collectors.joining(", ")) + "]";
    }

    public int getCurrentCharge() {
        return batteries.stream().map(Battery::getCurrentCharge).reduce(0, Integer::sum);
    }
    
    public void logCharging(String sourceName, int chargeRatePerSecond) {
    	String logMessage = String.format(
    		  "Source - %s charged battery with %d units/s, current charge: %s", 
    		  sourceName,
    		  chargeRatePerSecond,
    		  getBatteriesString());
      
      	System.out.println(logMessage);
    }
    
    public void logDischarging(String consumerName, int chargeRatePerSecond) {
    	String logMessage = String.format(
    		  "Battery discharged %d units for consumer - %s, current charge :%s", 
    		  chargeRatePerSecond,
    		  consumerName,
    		  getBatteriesString());
      
      	System.out.println(logMessage);
    }

    private void chargeFromSource(EnergySource source) {
        lock.lock();
        
        int remainingCharging = source.getChargeRatePerSecond();
          
        for (Battery battery : batteries) {
        	if(remainingCharging <= 0 ) {
        		break;
        	}
        	
            int spaceAvailable = battery.getCapacity() - battery.getCurrentCharge();
            if (spaceAvailable <= 0) continue;
            
            int differ = remainingCharging - spaceAvailable;
            if(differ >= 0) {
              	remainingCharging = differ;
            	battery.setCurrentCharge(battery.getCapacity());
            	logCharging(source.getSourceName(), source.getChargeRatePerSecond());
            	continue;
            } else {
            	int newCharge = battery.getCurrentCharge() + remainingCharging;
            	battery.setCurrentCharge(newCharge);
            	logCharging(source.getSourceName(), source.getChargeRatePerSecond());
            	break;
            }
        }

        lock.unlock();
        
    }

    
    private void supplyPowerToConsumer(EnergyConsumer consumer) {
        lock.lock();
        
        int remainingConsuming = consumer.getConsumeRatePerSecond();
        
        for(Battery battery: batteries) {
        	if(remainingConsuming <= 0) {
        		break;
        	}
        	
        	if(battery.getCurrentCharge() <= 0) {
        		continue;
        	}
        	
        	int differ = remainingConsuming - battery.getCurrentCharge();
        	if(differ >= 0) {
        		remainingConsuming = differ;
        		battery.setCurrentCharge(0);
        		logDischarging(consumer.getConsumerName(), consumer.getConsumeRatePerSecond());
        		continue;
        	} else {
        		int newCharge = battery.getCurrentCharge() - remainingConsuming;
        		battery.setCurrentCharge(newCharge);
        		logDischarging(consumer.getConsumerName(), consumer.getConsumeRatePerSecond());
        		break;
        	}
        }
        
        lock.unlock();
    }
}

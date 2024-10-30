package de.fhdo.smart_house.entity;

import lombok.Getter;

@Getter
public class EnergyConsumer {
  
    private final String consumerName;
    private final int consumeRatePerSecond;

    public EnergyConsumer(String consumerName, int powerRate) {
    	this.consumerName = consumerName;
        this.consumeRatePerSecond = powerRate;
    }
}
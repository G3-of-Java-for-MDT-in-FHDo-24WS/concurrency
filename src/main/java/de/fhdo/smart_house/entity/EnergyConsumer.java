package de.fhdo.smart_house.entity;

import lombok.Getter;

public class EnergyConsumer {
    @Getter
    private final int consumeRatePerSecond;

    public EnergyConsumer(int powerRate) {
        this.consumeRatePerSecond = powerRate;
    }
}
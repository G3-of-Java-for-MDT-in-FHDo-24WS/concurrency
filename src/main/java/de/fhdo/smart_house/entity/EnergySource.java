package de.fhdo.smart_house.entity;

import lombok.Data;

@Data
public class EnergySource {
    public enum SourceType {
        SOLAR,   
        WIND, 
        GRID 
    }

    private final SourceType sourceType;
    private final int chargeRatePerSecond; 

    public EnergySource(SourceType sourceType, int chargeRatePerSecond) {
        this.sourceType = sourceType;
        this.chargeRatePerSecond = chargeRatePerSecond;
    }
}

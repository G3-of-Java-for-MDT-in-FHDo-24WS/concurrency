package de.fhdo.smart_house.entity;

import lombok.Getter;

@Getter
public class EnergySource {
    public enum SourceType {
        SOLAR,   
        WIND, 
        GRID 
    }

    private final String sourceName;
    private final SourceType sourceType;
    private final int chargeRatePerSecond; 

    public EnergySource(String sourceName, SourceType sourceType, int chargeRatePerSecond) {
    	this.sourceName = sourceName;
        this.sourceType = sourceType;
        this.chargeRatePerSecond = chargeRatePerSecond;
    }
}

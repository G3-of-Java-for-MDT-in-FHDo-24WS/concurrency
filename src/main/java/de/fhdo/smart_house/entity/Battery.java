package de.fhdo.smart_house.entity;

import lombok.Data;

@Data

public class Battery {
	private int capacity;
    private int currentCharge;

    public Battery(int capacity) {
        this.capacity = capacity;
        this.currentCharge = 0;
    }
}
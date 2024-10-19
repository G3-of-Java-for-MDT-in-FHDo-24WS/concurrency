package de.fhdo.smart_house.service;

import java.util.ArrayList;
import java.util.List;

public class CustomExceptionHandler {

    private static List<String> exceptionMessages = new ArrayList<>();

    public static void handleMultipleExceptions(Exception... exceptions) {
        if (exceptions == null || exceptions.length == 0) {
            return; // No exceptions to handle
        }

        for (Exception e : exceptions) {
            if (e != null) {
                exceptionMessages.add("Handled exception: " + e.getMessage());
            }
        }
    }

    public static List<String> getExceptionMessages() {
        return new ArrayList<>(exceptionMessages);
    }

    public static void clearMessages() {
        exceptionMessages.clear();
    }
}

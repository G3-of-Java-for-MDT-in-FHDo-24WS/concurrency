package de.fhdo.smart_house.util;

import lombok.Data;
import lombok.EqualsAndHashCode;

public class CustomExceptionHandler {
    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class LogException extends Exception {
        private String customMessage;

        public LogException(String rawMessage, Throwable cause, String customMessage) {
            super(rawMessage, cause);
            this.customMessage = customMessage;
        }
    }

    public static class LogHandler extends Handler {
        public LogHandler(String msg) {
            this.customMessage = msg;
        }

        @Override
        public void handle() {
            System.out.println(this.customMessage);
        }
    }
    // Chaining Exception Handling for Logs
    public static void chainExceptions(int input) throws LogException {
        try {
            validateInput(input); // Might throw LogException
        } catch (LogException e) {
            throw new LogException("Input validation failed: " + e.getMessage(), e, "Error in input validation");
        }
    }

    private static void validateInput(int input) throws LogException {
        if (input <= 0) {
            throw new LogException("Input must be positive", null, "Invalid input: " + input);
        }
    }
}

abstract class Handler {
    String customMessage;
    abstract void handle();
}

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

    public static void LogHandler(LogException e) {
        System.err.format("LogException here: %s", e.getCustomMessage());
    }
}

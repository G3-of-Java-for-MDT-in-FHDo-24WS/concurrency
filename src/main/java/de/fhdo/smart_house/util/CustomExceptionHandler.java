package de.fhdo.smart_house.util;

public class CustomExceptionHandler {
    public static class LogException extends Exception {
        public LogException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}

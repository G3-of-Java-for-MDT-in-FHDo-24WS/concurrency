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
}

abstract class Handler {
    String customMessage;
    abstract void handle();
}

package de.fhdo.smart_house.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

class CustomExceptionHandlerTest {

    @Test
    void testHandleSingleException() {
        CustomExceptionHandler.clearMessages();
        Exception e1 = new Exception("Test Exception 1");
        CustomExceptionHandler.handleMultipleExceptions(e1);

        List<String> messages = CustomExceptionHandler.getExceptionMessages();
        assertEquals(1, messages.size());
        assertEquals("Handled exception: Test Exception 1", messages.get(0));
    }

    @Test
    void testHandleMultipleExceptions() {
        CustomExceptionHandler.clearMessages();
        Exception e1 = new Exception("Test Exception 1");
        Exception e2 = new Exception("Test Exception 2");
        CustomExceptionHandler.handleMultipleExceptions(e1, e2);

        List<String> messages = CustomExceptionHandler.getExceptionMessages();
        assertEquals(2, messages.size());
        assertEquals("Handled exception: Test Exception 1", messages.get(0));
        assertEquals("Handled exception: Test Exception 2", messages.get(1));
    }

    @Test
    void testHandleEmptyExceptionList() {
        CustomExceptionHandler.clearMessages();
        CustomExceptionHandler.handleMultipleExceptions();

        List<String> messages = CustomExceptionHandler.getExceptionMessages();
        assertTrue(messages.isEmpty(), "Expected no messages for an empty exception list");
    }

    @Test
    void testHandleNullException() {
        CustomExceptionHandler.clearMessages();
        Exception e1 = new Exception("Test Exception 1");
        Exception e2 = null;  // This should not cause issues
        CustomExceptionHandler.handleMultipleExceptions(e1, e2);

        List<String> messages = CustomExceptionHandler.getExceptionMessages();
        assertEquals(1, messages.size());
        assertEquals("Handled exception: Test Exception 1", messages.get(0));
    }

    @Test
    void testHandleAllNullExceptions() {
        CustomExceptionHandler.clearMessages();
        CustomExceptionHandler.handleMultipleExceptions((Exception[]) null);

        List<String> messages = CustomExceptionHandler.getExceptionMessages();
        assertTrue(messages.isEmpty(), "Expected no messages for all null exceptions");
    }
}

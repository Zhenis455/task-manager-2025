package com.example.taskmanager.web;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class TaskExportControllerTest {
    @Test void escapesCsvAndPreventsFormulaExecution() {
        assertEquals("\"'=+SUM(1,2)\"", TaskExportController.cell("=+SUM(1,2)"));
        assertEquals("\"hello \"\"friend\"\"\"", TaskExportController.cell("hello \"friend\""));
        assertEquals("\"a b\"", TaskExportController.cell("a\nb"));
    }
}

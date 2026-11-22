package com.example.taskmanager.model;

public enum TaskStatus {
    TODO("To do"),
    IN_PROGRESS("In progress"),
    DONE("Completed");

    private final String label;
    TaskStatus(String label) { this.label = label; }
    public String getLabel() { return label; }
}

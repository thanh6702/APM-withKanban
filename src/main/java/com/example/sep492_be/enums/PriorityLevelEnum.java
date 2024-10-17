package com.example.sep492_be.enums;

public enum PriorityLevelEnum {
    URGENT(1, "Urgent"),
    HIGH(2, "High"),
    MEDIUM(3, "Medium"),
    LOW(4, "Low");
    private Integer priority;
    private String name;

    PriorityLevelEnum(Integer priority, String name) {
        this.priority = priority;
        this.name = name;
    }
    public Integer getPriority() {
        return priority;
    }
    public String getName() {
        return name;
    }

}

package com.example.sep492_be.enums;

public enum ProjectStatus {

    NEW("Mới"),
    IN_PROGRESS("Đang Thực hiện"),
    PENDING("Tạm hoãn"),
    FINISHED("Kết thúc");
    private String value;
    ProjectStatus(String value) {
        this.value = value;
    }
    public String getValue() {
        return value;
    }
}

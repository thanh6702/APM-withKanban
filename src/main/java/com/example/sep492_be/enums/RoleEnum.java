package com.example.sep492_be.enums;

public enum RoleEnum {

    ADMIN("Quản trị hệ thống"),
    PM("Quản lý dự án"),
    EMPLOYEE("Thành viên dự án"),
    DIRECTOR("Giám đốc bộ phận");

    private String value;
    RoleEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

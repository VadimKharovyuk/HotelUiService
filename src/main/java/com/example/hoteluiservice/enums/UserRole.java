package com.example.hoteluiservice.enums;


import lombok.Getter;

@Getter
public enum UserRole {
    USER("Клиент отеля"),
    HOTEL_OWNER("Владелец отеля"),
    ADMIN("Администратор системы");

    private final String description;

    UserRole(String description) {
        this.description = description;
    }
}

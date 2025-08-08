package com.ma.message_apps.enumDto;


public enum UserStatus {

    ONLINE("Online"),
    OFFLINE("Offline");

    private final String status;

    UserStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}

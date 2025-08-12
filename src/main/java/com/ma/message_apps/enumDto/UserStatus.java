package com.ma.message_apps.enumDto;


public enum UserStatus {

    ONLINE("Online"),
    OFFLINE("Offline"),
    AWAY("Away");

    private final String status;

    UserStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}

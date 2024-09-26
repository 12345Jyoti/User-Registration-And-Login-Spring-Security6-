package com.auth.AuthImpl.ctp.enums;

public enum PlayerActionType {
    JOIN("join"),
    BET("bet"),
    DEAL("deal"),
    FOLD("fold"),
    SHOW("show"),
    UNKNOWN("unknown");

    private final String action;

    PlayerActionType(String action) {
        this.action = action;
    }

    public String getAction() {
        return action;
    }

    public static PlayerActionType fromString(String action) {
        for (PlayerActionType type : PlayerActionType.values()) {
            if (type.getAction().equalsIgnoreCase(action)) {
                return type;
            }
        }
        return UNKNOWN; // Default to UNKNOWN if no match
    }
}

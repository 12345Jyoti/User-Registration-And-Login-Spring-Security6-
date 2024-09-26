package com.auth.AuthImpl.ctp.dto;

public class GameEvent {
    private String eventType; // "start", "playerJoined", "dealCards", "bet", etc.
    private Object eventData;

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public Object getEventData() {
        return eventData;
    }

    public void setEventData(Object eventData) {
        this.eventData = eventData;
    }
}



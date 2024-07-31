package com.example.herbal.models;

import com.google.firebase.Timestamp;

public class MessageModel {
    private String senderId;
    private Timestamp timestamp;
    private String message;

    public MessageModel(String senderId, Timestamp timestamp, String message) {
        this.senderId = senderId;
        this.timestamp = timestamp;
        this.message = message;
    }

    public MessageModel() {}

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

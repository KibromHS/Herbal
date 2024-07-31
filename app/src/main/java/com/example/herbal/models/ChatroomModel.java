package com.example.herbal.models;

import com.google.firebase.Timestamp;

import java.util.List;

public class ChatroomModel {
    String chatRoomId;
    String userId;
    String healerId;
    Timestamp lastMessageTimestamp;
    String lastMessageSenderId;
    String lastMessage;

    public ChatroomModel(String chatRoomId, String userId, String healerId, Timestamp timestamp, String lastMessageSenderId) {
        this.chatRoomId = chatRoomId;
        this.userId = userId;
        this.healerId = healerId;
        this.lastMessageTimestamp = timestamp;
        this.lastMessageSenderId = lastMessageSenderId;
    }

    public ChatroomModel() {}

    public String getChatRoomId() {
        return chatRoomId;
    }

    public void setChatRoomId(String chatRoomId) {
        this.chatRoomId = chatRoomId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getHealerId() {
        return healerId;
    }

    public void setHealerId(String healerId) {
        this.healerId = healerId;
    }

    public Timestamp getLastMessageTimestamp() {
        return lastMessageTimestamp;
    }

    public void setLastMessageTimestamp(Timestamp lastMessageTimestamp) {
        this.lastMessageTimestamp = lastMessageTimestamp;
    }

    public String getLastMessageSenderId() {
        return lastMessageSenderId;
    }

    public void setLastMessageSenderId(String lastMessageSenderId) {
        this.lastMessageSenderId = lastMessageSenderId;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }
}

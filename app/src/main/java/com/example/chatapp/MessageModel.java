package com.example.chatapp;

public class MessageModel {

    private String message;
    private String messageId;
    private String senderId;

    public MessageModel() {
    }

    public MessageModel(String message, String messageId, String senderId) {
        this.message = message;
        this.messageId = messageId;
        this.senderId = senderId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }
}

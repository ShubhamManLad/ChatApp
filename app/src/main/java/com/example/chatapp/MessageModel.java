package com.example.chatapp;

public class MessageModel {

    private String message;
    private String messageId;
    private String senderId;
    private String receiverId;
    private boolean checked;

    public MessageModel() {
    }

    public MessageModel(String message, String messageId, String senderId, String receiverId) {
        this.message = message;
        this.messageId = messageId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.checked = false;
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

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}

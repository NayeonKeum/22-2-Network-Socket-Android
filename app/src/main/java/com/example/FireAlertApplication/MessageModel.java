package com.example.FireAlertApplication;

public class MessageModel {

    public String msg, sender;
    public int messageType, senderType;
    public int liked = 0;

    // Constructor
    public MessageModel(String msg, String sender, int messageType, int senderType) {
        this.msg = msg;
        this.sender = sender;
        this.senderType=senderType;
        this.messageType = messageType;
    }

    public void setLiked(int liked) {
        this.liked = liked;
    }
}
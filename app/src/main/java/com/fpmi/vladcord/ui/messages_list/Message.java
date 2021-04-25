package com.fpmi.vladcord.ui.messages_list;

import java.time.format.DateTimeFormatter;
import java.util.Date;

public class Message {
    private String sender;
    private String receiver;
    private String userName;
    private String textMessage;
    private boolean isseen;
    private Date messageTime;

    public Message() {}

    public Message(String sender, String receiver, String userName, String textMessage, boolean isseen) {
        this.sender = sender;
        this.receiver = receiver;
        this.userName = userName;
        this.textMessage = textMessage;
        this.messageTime = new Date();
        this.isseen = isseen;
    }

    public boolean isIsseen() {
        return isseen;
    }

    public void setIsseen(boolean isseen) {
        this.isseen = isseen;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getTextMessage() {
        return textMessage;
    }

    public void setTextMessage(String textMessage) {
        this.textMessage = textMessage;
    }

    public Date getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(Date messageTime) {
        this.messageTime = messageTime;
    }
}

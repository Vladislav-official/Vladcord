package com.fpmi.vladcord.ui.messages_list;

import java.util.Date;

public class Message {
    public String userName;
    public String textMessage;
    private Date messageTime;

    public Message() {}
    public Message(String userName, String textMessage) {
        this.textMessage = textMessage;
        this.userName = userName;
        this.messageTime = new Date();
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

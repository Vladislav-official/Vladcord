package com.fpmi.vladcord.ui.messages_list.Notifications;

public class Data {
    private String user;
    private String body;
    private String title;
    private String sented;
    private String privateMessage;
    private String groupName;

    public Data(String user, String body, String title, String sented, String privateMessage, String groupName) {
        this.user = user;
        this.body = body;
        this.title = title;
        this.sented = sented;
        this.privateMessage = privateMessage;
        this.groupName = groupName;
    }

    public Data() {
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPrivateMessage() {
        return privateMessage;
    }

    public void setPrivateMessage(String privateMessage) {
        this.privateMessage = privateMessage;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSented() {
        return sented;
    }

    public void setSented(String sented) {
        this.sented = sented;
    }
}

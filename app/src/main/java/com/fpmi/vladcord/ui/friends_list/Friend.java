package com.fpmi.vladcord.ui.friends_list;


public class Friend{

    private String id;
    private String notificationStatus;

    public Friend(){
        this.id = null;
        this.notificationStatus = null;
    }
    public Friend(String friendId, String notificationStatus) {
        this.id = friendId;
        this.notificationStatus = notificationStatus;
    }

    public String getFriendId() {
        return id;
    }

    public void setFriendId(String friendId) {
        this.id = friendId;
    }

    public String getNotificationStatus() {
        return notificationStatus;
    }

    public void setNotificationStatus(String notificationStatus) {
        this.notificationStatus = notificationStatus;
    }
}

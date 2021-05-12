package com.fpmi.vladcord.ui.groups;

public class Group {
    private String groupName;
    private String groupNotificationStatus;

    public Group(String groupName, String status) {
        this.groupName = groupName;
        this.groupNotificationStatus = status;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getGroupNotificationStatus() {
        return groupNotificationStatus;
    }

    public void setGroupNotificationStatus(String groupNotificationStatus) {
        this.groupNotificationStatus = groupNotificationStatus;
    }

    public Group() {
        groupName = null;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}

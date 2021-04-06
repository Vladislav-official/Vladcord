package com.fpmi.vladcord.ui.friends_list;


public class Friend {

    public Friend(Friend friend) {
        name = friend.name;
        email = friend.email;
        urlAva = friend.urlAva;
        uID = friend.uID;
    }

    public Friend() {
        name = null;
        email = null;
        urlAva = null;
        uID = null;
    }

    public Friend(String name, String email,  String uId, String url) {
        this.name = name;
        this.email = email;
        this.uID = uId;
        this.urlAva = url;
    }

    private String name;
    private String email;
    private String uID;
    private String urlAva;

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return urlAva;
    }

    public String getuID() {
        return uID;
    }

    public String getUrlAva() {
        return urlAva;
    }

    public void setUrlAva(String urlAva) {
        this.urlAva = urlAva;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUrl(String url) {
        this.urlAva = url;
    }

    public void setuID(String uID) {
        this.uID = uID;
    }
}

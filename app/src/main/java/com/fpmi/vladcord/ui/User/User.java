package com.fpmi.vladcord.ui.User;

import android.net.Uri;

import com.fpmi.vladcord.ui.friends_list.Friend;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class User {

    private String name;
    private String email;
    private String uID;
    private String urlAva;

    public User(String name, String email, String uID, String urlAva){
        this.name = name;
        this.email = email;
        this.uID = uID;
        this.urlAva = urlAva;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(email, user.email) &&
                Objects.equals(uID, user.uID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, uID);
    }

    public User(){
        this.urlAva = null;
        this.name = null;
        this.email = null;
        this.uID = null;
    }

    public String getUrlAva() {
        return urlAva;
    }

    public void setUrlAva(String urlAva) {
        this.urlAva = urlAva;
    }

    public User(User user){
        this.name = user.name;
        this.email = user.email;
        this.uID = user.uID;
        this.urlAva = user.urlAva;
    }



    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setuID(String uID) {
        this.uID = uID;
    }

    public String getEmail() {
        return email;
    }

    public String getuID() {
        return uID;
    }

}

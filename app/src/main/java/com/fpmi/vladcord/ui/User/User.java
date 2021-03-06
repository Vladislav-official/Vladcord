package com.fpmi.vladcord.ui.User;

import java.util.Objects;

public class User {

    private String name;
    private String email;
    private String uID;
    private String urlAva;
    private String status;
    private String bio;

    public User(String name, String email, String uID, String urlAva, String status, String bio) {
        this.name = name;
        this.email = email;
        this.uID = uID;
        this.urlAva = urlAva;
        this.status = status;
        this.bio = bio;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(email, user.email) &&
                Objects.equals(uID, user.uID);
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, uID);
    }

    public User() {
        this.urlAva = null;
        this.name = null;
        this.email = null;
        this.uID = null;
        this.status = null;
    }

    public String getUrlAva() {
        return urlAva;
    }

    public void setUrlAva(String urlAva) {
        this.urlAva = urlAva;
    }

    public User(User user) {
        this.name = user.name;
        this.email = user.email;
        this.uID = user.uID;
        this.urlAva = user.urlAva;
        this.status = user.status;
        this.bio = user.bio;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

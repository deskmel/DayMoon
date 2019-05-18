package com.example.daymoon.UserInfoManagement;

import android.graphics.Bitmap;

public class User {
    private int id;
    private String name, description, email, phoneNumber;
    private Bitmap profilePhoto;

    public User(int id, String name, String description, String email, String phoneNumber){
        this.id = id;
        this.name = name;
        this.description = description;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public User(int id, String name){
        this.id = id;
        this.name = name;
    }

    public void CopyFrom(User user){
        id = user.id;
        name = user.name;
        description = user.description;
        email = user.email;
        phoneNumber = user.phoneNumber;
        profilePhoto = user.profilePhoto;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setProfilePhoto(Bitmap profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public Bitmap getProfilePhoto() {
        return profilePhoto;
    }
}

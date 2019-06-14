package com.example.daymoon.UserInfoManagement;


import android.graphics.Bitmap;

public class User {
    private int id;
    private String name, description, mailAddress, phoneNumber, profilePhotoName;
    private int[] groupIDs, eventIDs;
    private Bitmap profilePhoto;

    public User(){}

    public User(int id, String name, String description, String mailAddress, String phoneNumber, String profilePhotoName){
        this.id = id;
        this.name = name;
        this.description = description;
        this.mailAddress = mailAddress;
        this.phoneNumber = phoneNumber;
        this.profilePhotoName = profilePhotoName;
    }

    public User(int id, String name){
        this.id = id;
        this.name = name;
    }

    void CopyFrom(User user){
        id = user.id;
        name = user.name;
        description = user.description;
        mailAddress = user.mailAddress;
        phoneNumber = user.phoneNumber;
        profilePhotoName = user.profilePhotoName;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setmailAddress(String mailAddress) {
        this.mailAddress = mailAddress;
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

    public String getMailAddress() {
        return mailAddress;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getProfilePhotoName(){
        return profilePhotoName;
    }

    public Bitmap getProfilePhoto() {
        return profilePhoto;
    }
}

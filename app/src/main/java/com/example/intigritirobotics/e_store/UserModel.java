package com.example.intigritirobotics.e_store;

import android.media.Image;

public class UserModel {
    String name;
    String address;
    String phoneNumber;
    String userId;
    String pin;
    Image profileImage;

    public UserModel(String name, String address, String phoneNumber, String userId) {
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.userId = userId;
    }

    public UserModel(String name, String address, String phoneNumber, String userId, String pin) {
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.userId = userId;
        this.pin = pin;
    }

    public boolean isIncomplete() {
        return address == null || phoneNumber == null || pin == null || name == null;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getUserId() {
        return userId;
    }

    public Image getProfileImage() {
        return profileImage;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setProfileImage(Image profileImage) {
        this.profileImage = profileImage;
    }
}

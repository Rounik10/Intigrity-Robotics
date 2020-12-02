package com.example.intigritirobotics;

import android.media.Image;

public class UserModel {
    String name;
    String address;
    String phoneNumber;
    String userId;
    Image profileImage;

    public UserModel(String name, String address, String phoneNumber, String userId) {
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.userId = userId;
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

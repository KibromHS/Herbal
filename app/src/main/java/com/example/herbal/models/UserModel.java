package com.example.herbal.models;

import com.google.gson.Gson;

import java.io.Serializable;

public class UserModel implements Serializable {
    private String userId;
    private String username;
    private String email;
    private String imgUrl;
    private double weight;
    private int age;
    private String gender;
    private String fcmToken;

    public UserModel(String userId, String username, String email, String imgUrl, double weight, int age, String gender) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.imgUrl = imgUrl;
        this.weight = weight;
        this.age = age;
        this.gender = gender;
    }

    public UserModel() {}

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public static UserModel fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, UserModel.class);
    }
}

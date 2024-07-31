package com.example.herbal.models;

import com.google.gson.Gson;

import java.io.Serializable;

public class HealerModel implements Serializable {
    private String healerId;
    private String healerName;
    private String healerEmail;
    private String healerPic;
    private int experience;
    private String healerAddress;
    private String gender;
    private boolean isApproved;

    public HealerModel(String healerId, String healerName, String healerEmail, String healerPic, int experience, String healerAddress, String gender, boolean isApproved) {
        this.healerId = healerId;
        this.healerName = healerName;
        this.healerEmail = healerEmail;
        this.healerPic = healerPic;
        this.experience = experience;
        this.healerAddress = healerAddress;
        this.gender = gender;
        this.isApproved = isApproved;
    }

    public HealerModel() {}

    public String getHealerId() {
        return healerId;
    }

    public String getHealerName() {
        return healerName;
    }

    public void setHealerName(String healerName) {
        this.healerName = healerName;
    }

    public String getHealerEmail() {
        return healerEmail;
    }

    public void setHealerEmail(String healerEmail) {
        this.healerEmail = healerEmail;
    }

    public String getHealerPic() {
        return healerPic;
    }

    public void setHealerPic(String healerPic) {
        this.healerPic = healerPic;
    }

    public int getExperience() {
        return experience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    public String getHealerAddress() {
        return healerAddress;
    }

    public void setHealerAddress(String healerAddress) {
        this.healerAddress = healerAddress;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public boolean isApproved() {
        return isApproved;
    }

    public void setApproved(boolean approved) {
        isApproved = approved;
    }

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public static HealerModel fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, HealerModel.class);
    }
}

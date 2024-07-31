package com.example.herbal.models;

import java.io.Serializable;

public class Herbal implements Serializable {
    private String herbalId;
    private String herbalName;
    private String herbalImage;
    private String healerId;
    private String herbalDescription;
    private String herbalType;
    private int numOfViews;
    private String dosage;
    private String sideEffect;

    public Herbal(String herbalId, String herbalName, String herbalImage, String healerId, String herbalDescription, String herbalType, int numOfViews, String dosage, String sideEffect) {
        this.herbalId = herbalId;
        this.herbalName = herbalName;
        this.herbalImage = herbalImage;
        this.healerId = healerId;
        this.herbalDescription = herbalDescription;
        this.herbalType = herbalType;
        this.numOfViews = numOfViews;
        this.dosage = dosage;
        this.sideEffect = sideEffect;
    }

    public Herbal() {}

    public String getHerbalType() {
        return herbalType;
    }

    public void setHerbalType(String herbalType) {
        this.herbalType = herbalType;
    }

    public String getHerbalId() {
        return herbalId;
    }

    public String getHerbalName() {
        return herbalName;
    }

    public void setHerbalName(String herbalName) {
        this.herbalName = herbalName;
    }

    public String getHerbalImage() {
        return herbalImage;
    }

    public void setHerbalImage(String herbalImage) {
        this.herbalImage = herbalImage;
    }

    public String getHealerId() {
        return healerId;
    }

    public void setHealerId(String healer) {
        this.healerId = healer;
    }

    public String getHerbalDescription() {
        return herbalDescription;
    }

    public void setHerbalDescription(String herbalDescription) {
        this.herbalDescription = herbalDescription;
    }

    public int getNumOfViews() {
        return numOfViews;
    }

    public void setNumOfViews(int numOfViews) {
        this.numOfViews = numOfViews;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public String getSideEffect() {
        return sideEffect;
    }

    public void setSideEffect(String sideEffect) {
        this.sideEffect = sideEffect;
    }
}

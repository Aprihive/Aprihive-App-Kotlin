// Copyright (c) Jesulonimii 2021.
// Copyright (c) Erlite 2021.
// Copyright (c) Aprihive 2021.
// All Rights Reserved

package com.aprihive.models;

public class OnboardingModel {

    private String title, description;
    private int imageUri;
    private int buttonDisplay;

    public OnboardingModel(String title, String description, int imageUri, int buttonDisplay) {
        this.title = title;
        this.description = description;
        this.imageUri = imageUri;
        this.buttonDisplay = buttonDisplay;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getImageUri() {
        return imageUri;
    }

    public void setImageUri(int imageUri) {
        this.imageUri = imageUri;
    }

    public int getButtonDisplay() {
        return buttonDisplay;
    }

    public void setButtonDisplay(int buttonDisplay) {
        this.buttonDisplay = buttonDisplay;
    }
}

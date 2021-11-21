package com.aprihive.models;

public class FindModel {

    private String fullname, username, bio, schoolName, profileImageUrl, email, phone, twitter, instagram;
    private Boolean verified;

      public FindModel(){

    }

    public FindModel(String fullname, String username, String bio, String schoolName, String profileImageUrl, String email, String phone, String twitter, String instagram, Boolean verified) {
        this.fullname = fullname;
        this.username = username;
        this.bio = bio;
        this.schoolName = schoolName;
        this.profileImageUrl = profileImageUrl;
        this.email = email;
        this.phone = phone;
        this.twitter = twitter;
        this.instagram = instagram;
        this.verified = verified;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getTwitter() {
        return twitter;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    public String getInstagram() {
        return instagram;
    }

    public void setInstagram(String instagram) {
        this.instagram = instagram;
    }
}

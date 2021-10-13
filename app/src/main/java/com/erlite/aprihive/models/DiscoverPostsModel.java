// Copyright (c) Jesulonimii 2021. 
// Copyright (c) Erlite 2021. 
// Copyright (c) Aprihive 2021. 
// All Rights Reserved

package com.erlite.aprihive.models;

public class DiscoverPostsModel {

    private String fullname, username, postText, postId, postImageLink,  postTags, location, timePosted, authorEmail;
    private Boolean verified;

    public DiscoverPostsModel(){}

    public DiscoverPostsModel(String fullname, String username, String postText, String postId, String postImageLink, String postTags, String location, String timePosted, String authorEmail, Boolean verified) {
        this.fullname = fullname;
        this.username = username;
        this.postText = postText;
        this.postId = postId;
        this.postImageLink = postImageLink;
        this.postTags = postTags;
        this.location = location;
        this.timePosted = timePosted;
        this.authorEmail = authorEmail;
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

    public String getPostText() {
        return postText;
    }

    public void setPostText(String postText) {
        this.postText = postText;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getPostImageLink() {
        return postImageLink;
    }

    public void setPostImageLink(String postImageLink) {
        this.postImageLink = postImageLink;
    }

    public String getPostTags() {
        return postTags;
    }

    public void setPostTags(String postTags) {
        this.postTags = postTags;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTimePosted() {
        return timePosted;
    }

    public void setTimePosted(String timePosted) {
        this.timePosted = timePosted;
    }

    public String getAuthorEmail() {
        return authorEmail;
    }

    public void setAuthorEmail(String authorEmail) {
        this.authorEmail = authorEmail;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }


}

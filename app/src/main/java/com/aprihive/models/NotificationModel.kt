// Copyright (c) Jesulonimii 2021.
// Copyright (c) Erlite 2021.
// Copyright (c) Aprihive 2021.
// All Rights Reserved

package com.aprihive.models;

public class NotificationModel {

    private String requestText, postText, postId, postImageLink, authorEmail, senderEmail, deadline, type, requestedOn, receiverUsername, senderUsername;

      public NotificationModel(){

    }

    public NotificationModel(String requestText, String postText, String postId, String postImageLink, String authorEmail, String senderEmail, String deadline, String type, String requestedOn, String receiverUsername, String senderUsername) {
        this.requestText = requestText;
        this.postText = postText;
        this.postId = postId;
        this.postImageLink = postImageLink;
        this.authorEmail = authorEmail;
        this.senderEmail = senderEmail;
        this.deadline = deadline;
        this.type = type;
        this.requestedOn = requestedOn;
        this.receiverUsername = receiverUsername;
        this.senderUsername = senderUsername;
    }

    public String getRequestText() {
        return requestText;
    }

    public void setRequestText(String requestText) {
        this.requestText = requestText;
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

    public String getAuthorEmail() {
        return authorEmail;
    }

    public void setAuthorEmail(String authorEmail) {
        this.authorEmail = authorEmail;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRequestedOn() {
        return requestedOn;
    }

    public void setRequestedOn(String requestedOn) {
        this.requestedOn = requestedOn;
    }

    public String getReceiverUsername() {
        return receiverUsername;
    }

    public void setReceiverUsername(String receiverUsername) {
        this.receiverUsername = receiverUsername;
    }

    public String getSenderUsername() {
        return senderUsername;
    }

    public void setSenderUsername(String senderUsername) {
        this.senderUsername = senderUsername;
    }
}

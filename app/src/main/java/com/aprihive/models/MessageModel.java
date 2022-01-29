// Copyright (c) Jesulonimii 2022.
// Copyright (c) Erlite 2022.
// Copyright (c) Aprihive 2022.
// All Rights Reserved

package com.aprihive.models;

import com.google.firebase.Timestamp;

import java.util.HashMap;

public class MessageModel {

    private String messageText;
    private Timestamp time;
    private String messageImageLink;
    private String messageType;

    public MessageModel() {
    }

    public MessageModel(String messageText, Timestamp time, String messageImageLink, String messageType) {
        this.messageText = messageText;
        this.time = time;
        this.messageImageLink = messageImageLink;
        this.messageType = messageType;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public String getMessageImageLink() {
        return messageImageLink;
    }

    public void setMessageImageLink(String messageImageLink) {
        this.messageImageLink = messageImageLink;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }
}

// Copyright (c) Jesulonimii 2022.
// Copyright (c) Erlite 2022.
// Copyright (c) Aprihive 2022.
// All Rights Reserved

package com.aprihive.models;

import java.util.HashMap;

public class MessagedUsersModel {
    private String receiverEmail;


    public MessagedUsersModel() {
    }

    public MessagedUsersModel(String receiverEmail) {
        this.receiverEmail = receiverEmail;
    }

    public String getReceiverEmail() {
        return receiverEmail;
    }

    public void setReceiverEmail(String receiverEmail) {
        this.receiverEmail = receiverEmail;
    }

}

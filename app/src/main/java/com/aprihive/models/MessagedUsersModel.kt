// Copyright (c) Jesulonimii 2022.
// Copyright (c) Erlite 2022.
// Copyright (c) Aprihive 2022.
// All Rights Reserved
package com.aprihive.models

class MessagedUsersModel {
    var receiverEmail: String? = null

    constructor() {}
    constructor(receiverEmail: String?) {
        this.receiverEmail = receiverEmail
    }
}
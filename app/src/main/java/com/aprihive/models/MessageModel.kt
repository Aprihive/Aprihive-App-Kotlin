// Copyright (c) Jesulonimii 2022.
// Copyright (c) Erlite 2022.
// Copyright (c) Aprihive 2022.
// All Rights Reserved
package com.aprihive.models

import com.google.firebase.Timestamp

class MessageModel {
    var messageText: String? = null
    var time: Timestamp? = null
    var messageImageLink: String? = null
    var messageType: String? = null
    var messageId: String? = null
    var otherUserEmail: String? = null

    constructor() {}
    constructor(messageText: String?, time: Timestamp?, messageImageLink: String?, messageType: String?, messageId: String?, otherUserEmail: String?) {
        this.messageText = messageText
        this.time = time
        this.messageImageLink = messageImageLink
        this.messageType = messageType
        this.messageId = messageId
        this.otherUserEmail = otherUserEmail
    }
}
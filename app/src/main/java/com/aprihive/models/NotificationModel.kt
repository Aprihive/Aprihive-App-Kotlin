// Copyright (c) Jesulonimii 2021.
// Copyright (c) Erlite 2021.
// Copyright (c) Aprihive 2021.
// All Rights Reserved
package com.aprihive.models

class NotificationModel {
    var requestText: String? = null
    var postText: String? = null
    var postId: String? = null
    var postImageLink: String? = null
    var authorEmail: String? = null
    var senderEmail: String? = null
    var deadline: String? = null
    var type: String? = null
    var requestedOn: String? = null
    var receiverUsername: String? = null
    var senderUsername: String? = null

    constructor() {}
    constructor(requestText: String?, postText: String?, postId: String?, postImageLink: String?, authorEmail: String?, senderEmail: String?, deadline: String?, type: String?, requestedOn: String?, receiverUsername: String?, senderUsername: String?) {
        this.requestText = requestText
        this.postText = postText
        this.postId = postId
        this.postImageLink = postImageLink
        this.authorEmail = authorEmail
        this.senderEmail = senderEmail
        this.deadline = deadline
        this.type = type
        this.requestedOn = requestedOn
        this.receiverUsername = receiverUsername
        this.senderUsername = senderUsername
    }
}
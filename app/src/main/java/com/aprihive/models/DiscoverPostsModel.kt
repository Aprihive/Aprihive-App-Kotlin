// Copyright (c) Jesulonimii 2021. 
// Copyright (c) Erlite 2021. 
// Copyright (c) Aprihive 2021. 
// All Rights Reserved
package com.aprihive.models

import java.util.HashMap

class DiscoverPostsModel {
    var fullname: String? = null
    var username: String? = null
    var postText: String? = null
    var postId: String? = null
    var postImageLink: String? = null
    var postTags: String? = null
    var location: String? = null
    var timePosted: String? = null
    var authorEmail: String? = null
    var linkData: HashMap<String, String>? = null
    var verified: Boolean? = null
    var positionId = 0

    constructor() {}
    constructor(fullname: String?, username: String?, postText: String?, postId: String?, postImageLink: String?, postTags: String?, location: String?, timePosted: String?, authorEmail: String?, linkData: HashMap<String, String>?, verified: Boolean?) {
        this.fullname = fullname
        this.username = username
        this.postText = postText
        this.postId = postId
        this.postImageLink = postImageLink
        this.postTags = postTags
        this.location = location
        this.timePosted = timePosted
        this.authorEmail = authorEmail
        this.linkData = linkData
        this.verified = verified
    }
}
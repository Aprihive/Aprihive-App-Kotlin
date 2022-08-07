package com.aprihive.models

class FindModel {
    var fullname: String? = null
    var username: String? = null
    var bio: String? = null
    var schoolName: String? = null
    var profileImageUrl: String? = null
    var email: String? = null
    var phone: String? = null
    var twitter: String? = null
    var instagram: String? = null
    var verified: Boolean? = null
    var threat: Boolean? = null

    constructor() {}
    constructor(fullname: String?, username: String?, bio: String?, schoolName: String?, profileImageUrl: String?, email: String?, phone: String?, twitter: String?, instagram: String?, verified: Boolean?, threat: Boolean?) {
        this.fullname = fullname
        this.username = username
        this.bio = bio
        this.schoolName = schoolName
        this.profileImageUrl = profileImageUrl
        this.email = email
        this.phone = phone
        this.twitter = twitter
        this.instagram = instagram
        this.verified = verified
        this.threat = threat
    }
}
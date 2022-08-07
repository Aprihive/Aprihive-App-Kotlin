// Copyright (c) Jesulonimii 2021.
// Copyright (c) Erlite 2021.
// Copyright (c) Aprihive 2021.
// All Rights Reserved
package com.aprihive.backend

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import java.util.HashMap

interface RetrofitInterface {

    @POST("request-notify")
    fun executeRequestNotification(@Body map: HashMap<String?, String?>?): Call<Void?>?

    @POST("requests-push-notify")
    fun executeRequestPushNotification(@Body map: HashMap<String?, String?>?): Call<Void?>?

    @POST("post-removal-notify")
    fun executePostRemovalNotification(@Body map: HashMap<String?, String?>?): Call<Void?>?

    @POST("message-push-notify")
    fun executeMessagePushNotification(@Body map: HashMap<String?, String?>?): Call<Void?>?
}
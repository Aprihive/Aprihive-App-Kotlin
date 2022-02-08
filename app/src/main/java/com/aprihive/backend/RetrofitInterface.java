// Copyright (c) Jesulonimii 2021.
// Copyright (c) Erlite 2021.
// Copyright (c) Aprihive 2021.
// All Rights Reserved

package com.aprihive.backend;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RetrofitInterface {

    @POST("request-notify")
    Call<Void> executeRequestNotification(@Body HashMap<String, String> map);

    @POST("post-removal-notify")
    Call<Void> executePostRemovalNotification(@Body HashMap<String, String> map);

    @POST("message-push-notify")
    Call<Void> executeMessagePushNotification(@Body HashMap<String, String> map);


}

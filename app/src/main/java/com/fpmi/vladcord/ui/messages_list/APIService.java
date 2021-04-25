package com.fpmi.vladcord.ui.messages_list;

import com.fpmi.vladcord.ui.messages_list.Notifications.MyResponse;
import com.fpmi.vladcord.ui.messages_list.Notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {"Content-Type:application/json",
            "Authorization:key=AAAAbvT50FY:APA91bH0tlqZYBOLJIpkK_lCHBOQ5PWFLG1cMkba3KmTJfF_cblsh_I4JWPelY5psdG5NmT686MeQAeGQ1zpx7Btx8zM6pyghfDQbzJsn1IdpY0Y-S5W-2S3uYmkIhvQLkB4v3I4Yujf"}
            )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}

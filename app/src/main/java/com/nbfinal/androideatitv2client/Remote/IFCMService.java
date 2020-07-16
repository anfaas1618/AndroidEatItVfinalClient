package com.nbfinal.androideatitv2client.Remote;

import com.nbfinal.androideatitv2client.Model.FCMResponse;
import com.nbfinal.androideatitv2client.Model.FCMSendData;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMService {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAA_Oyci8w:APA91bF1NlZP6S1XlG6ywdUZ29seHr6vU6NEKD_Tj2pDXHFkYHMGbTuwuzENka6pZqY-xAESfzYqHDc1qM1dF2g4RyjM11j69jNyX5pD80P6E_3i63fmb6kb77z6WuY-ptAY8IAbcFe-"
    })
    @POST("fcm/send")
    Observable<FCMResponse> sendNotification (@Body FCMSendData body);

}
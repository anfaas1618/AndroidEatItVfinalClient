package com.nbfinal.androideatitv2client.Callback;

import com.nbfinal.androideatitv2client.Model.OrderModel;

public interface ILoadTimeFromFirebaseListener {
    void onLoadTimeSuccess(OrderModel order, long estimateTimeInMs);
    void onLoadTimeFailed(String message);
}

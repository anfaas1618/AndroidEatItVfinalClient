package com.nbclient.androideatitv2client.Callback;

import com.nbclient.androideatitv2client.Model.OrderModel;

public interface ILoadTimeFromFirebaseListener {
    void onLoadTimeSuccess(OrderModel order, long estimateTimeInMs);
    void onLoadTimeFailed(String message);
}

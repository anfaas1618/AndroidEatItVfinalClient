package com.mibtech.nirmalbakeryclient.Callback;

import com.mibtech.nirmalbakeryclient.Model.OrderModel;

public interface ILoadTimeFromFirebaseListener {
    void onLoadTimeSuccess(OrderModel order, long estimateTimeInMs);
    void onLoadTimeFailed(String message);
}

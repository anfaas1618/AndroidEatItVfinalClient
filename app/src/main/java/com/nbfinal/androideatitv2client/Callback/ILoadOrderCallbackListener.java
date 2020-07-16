package com.nbfinal.androideatitv2client.Callback;

import com.nbfinal.androideatitv2client.Model.OrderModel;

import java.util.List;

public interface ILoadOrderCallbackListener {
    void onLoadOrderSuccess(List<OrderModel> orderList);
    void onLoadOrderFailed(String message);
}

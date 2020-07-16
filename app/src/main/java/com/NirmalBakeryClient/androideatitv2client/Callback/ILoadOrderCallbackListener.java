package com.NirmalBakeryClient.androideatitv2client.Callback;

import com.NirmalBakeryClient.androideatitv2client.Model.OrderModel;

import java.util.List;

public interface ILoadOrderCallbackListener {
    void onLoadOrderSuccess(List<OrderModel> orderList);
    void onLoadOrderFailed(String message);
}

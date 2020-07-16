package com.NirmalBakeryClient.androideatitv2client.Callback;

import com.NirmalBakeryClient.androideatitv2client.Model.PopularCategoryModel;

import java.util.List;

public interface IPopularCallbackListener {
    void onPopularLoadSuccess(List<PopularCategoryModel> popularCategoryModels);
    void onPopularLoadFailed(String message);
}

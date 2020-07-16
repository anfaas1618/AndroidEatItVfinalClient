package com.nbclient.androideatitv2client.Callback;

import com.nbclient.androideatitv2client.Model.PopularCategoryModel;

import java.util.List;

public interface IPopularCallbackListener {
    void onPopularLoadSuccess(List<PopularCategoryModel> popularCategoryModels);
    void onPopularLoadFailed(String message);
}

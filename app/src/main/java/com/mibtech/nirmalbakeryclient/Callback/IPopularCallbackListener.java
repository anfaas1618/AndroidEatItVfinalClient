package com.mibtech.nirmalbakeryclient.Callback;

import com.mibtech.nirmalbakeryclient.Model.PopularCategoryModel;

import java.util.List;

public interface IPopularCallbackListener {
    void onPopularLoadSuccess(List<PopularCategoryModel> popularCategoryModels);
    void onPopularLoadFailed(String message);
}

package com.mibtech.nirmalbakeryclient.Callback;

import com.mibtech.nirmalbakeryclient.Model.CategoryModel;

import java.util.List;

public interface ICategoryCallbackListener {
    void onCategoryLoadSuccess(List<CategoryModel> categoryModels);
    void onCategoryLoadFailed(String message);
}

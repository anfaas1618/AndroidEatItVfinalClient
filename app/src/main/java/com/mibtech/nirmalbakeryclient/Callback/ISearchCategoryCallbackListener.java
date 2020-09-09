package com.mibtech.nirmalbakeryclient.Callback;

import com.mibtech.nirmalbakeryclient.Database.CartItem;
import com.mibtech.nirmalbakeryclient.Model.CategoryModel;

public interface ISearchCategoryCallbackListener {
    void onSearchCategoryFound(CategoryModel categoryModel, CartItem cartItem);
    void onSearchCategoryNotfound(String message);
}

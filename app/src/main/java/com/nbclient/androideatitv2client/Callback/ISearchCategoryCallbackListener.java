package com.nbclient.androideatitv2client.Callback;

import com.nbclient.androideatitv2client.Database.CartItem;
import com.nbclient.androideatitv2client.Model.CategoryModel;

public interface ISearchCategoryCallbackListener {
    void onSearchCategoryFound(CategoryModel categoryModel, CartItem cartItem);
    void onSearchCategoryNotfound(String message);
}

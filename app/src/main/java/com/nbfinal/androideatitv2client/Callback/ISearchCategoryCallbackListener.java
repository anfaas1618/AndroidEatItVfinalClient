package com.nbfinal.androideatitv2client.Callback;

import com.nbfinal.androideatitv2client.Database.CartItem;
import com.nbfinal.androideatitv2client.Model.CategoryModel;

public interface ISearchCategoryCallbackListener {
    void onSearchCategoryFound(CategoryModel categoryModel, CartItem cartItem);
    void onSearchCategoryNotfound(String message);
}

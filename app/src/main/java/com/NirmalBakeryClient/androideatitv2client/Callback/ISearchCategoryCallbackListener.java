package com.NirmalBakeryClient.androideatitv2client.Callback;

import com.NirmalBakeryClient.androideatitv2client.Database.CartItem;
import com.NirmalBakeryClient.androideatitv2client.Model.CategoryModel;

public interface ISearchCategoryCallbackListener {
    void onSearchCategoryFound(CategoryModel categoryModel, CartItem cartItem);
    void onSearchCategoryNotfound(String message);
}

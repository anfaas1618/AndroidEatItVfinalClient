package com.nbfinal.androideatitv2client.EventBus;

import com.nbfinal.androideatitv2client.Model.PopularCategoryModel;

public class PopularCategoryClick {

    private PopularCategoryModel popularCategoryModel;

    public PopularCategoryClick(PopularCategoryModel popularCategoryModel) {
        this.popularCategoryModel = popularCategoryModel;
    }

    public PopularCategoryModel getPopularCategoryModel() {
        return popularCategoryModel;
    }

    public void setPopularCategoryModel(PopularCategoryModel popularCategoryModel) {
        this.popularCategoryModel = popularCategoryModel;
    }
}

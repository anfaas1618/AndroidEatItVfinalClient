package com.NirmalBakeryClient.androideatitv2client.Callback;

import com.NirmalBakeryClient.androideatitv2client.Model.BestDealModel;
import java.util.List;

public interface IBestDealCallbackListener {
    void onBestDealLoadSuccess(List<BestDealModel> bestDealModels);
    void onBestDealLoadFailed(String message);
}

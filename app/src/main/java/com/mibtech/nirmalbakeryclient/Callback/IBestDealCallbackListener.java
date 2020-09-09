package com.mibtech.nirmalbakeryclient.Callback;

import com.mibtech.nirmalbakeryclient.Model.BestDealModel;
import java.util.List;

public interface IBestDealCallbackListener {
    void onBestDealLoadSuccess(List<BestDealModel> bestDealModels);
    void onBestDealLoadFailed(String message);
}

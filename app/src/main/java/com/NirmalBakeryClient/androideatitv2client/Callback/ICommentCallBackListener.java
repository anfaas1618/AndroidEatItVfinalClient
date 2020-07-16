package com.NirmalBakeryClient.androideatitv2client.Callback;

import com.NirmalBakeryClient.androideatitv2client.Model.CommentModel;

import java.util.List;

public interface ICommentCallBackListener {
    void onCommentLoadSuccess(List<CommentModel> commentModels);
    void onCommentLoadFailed(String message);
}

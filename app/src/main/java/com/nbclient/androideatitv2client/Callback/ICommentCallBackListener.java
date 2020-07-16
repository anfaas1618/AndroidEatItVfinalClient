package com.nbclient.androideatitv2client.Callback;

import com.nbclient.androideatitv2client.Model.CommentModel;

import java.util.List;

public interface ICommentCallBackListener {
    void onCommentLoadSuccess(List<CommentModel> commentModels);
    void onCommentLoadFailed(String message);
}

package com.android.codepush;

import java.io.File;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by zhang on 2016/10/27.
 */

public interface SQ_DownLoadCallBack {
    void onSuccess(File file, Call call, Response response);
    void onError(Call call, Response response, Exception e);
    void downloadProgress(long currentSize, long totalSize, float progress, long networkSpeed);

}

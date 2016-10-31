package com.android.codepush;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;

import java.io.File;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by wsq on 2016/10/26.
 */

class SQ_DownLoad  {
    SQ_HotLoad load;
    private Context downContext;
    private SQ_SettingManager settingManager;


    SQ_DownLoad(Context downContext, SQ_HotLoad lo) {
        this.downContext = downContext;
        this.load=lo;
        this.settingManager=new SQ_SettingManager(downContext);
    }

    /**
     *
     * 下载服务端最新资源文件
     * 类型 ：zip
     * */
    void downLoadZipFromServer() {
        OkGo.get(load.getServerUrl())//
                .tag(this)//
//                .params("app_code",settingManager.getParams(SQ_Constants.APP_CODE))
//                .params("app_visition",settingManager.getParams(SQ_Constants.APP_VISITION))
                .execute(new FileCallback(load.zipUrl,SQ_Constants.DOWNLOAD_FILE_NAME) {  //文件下载时，可以指定下载的文件目录和文件名
                    @Override
                    public void onSuccess(File file, Call call, Response response) {
                        // file 即为文件数据，文件保存在指定目录
                        Log.v("{okhttp}","body_Code:"+response.code());
                        Toast.makeText(downContext,"下载成功",Toast.LENGTH_SHORT).show();
                        if(response!=null && response.code()==200){
                            load.unZipFile(file);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                    }

                    @Override
                    public void downloadProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
                        //这里回调下载进度(该回调在主线程,可以直接更新ui)

                    }
                });

    }



}

package com.android.codepush;

import android.content.Context;
import android.text.TextUtils;

import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.JavaScriptModule;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by wsq on 2016/10/26.
 */

public class SQ_HotLoad implements ReactPackage {

    private String serverUrl = "http://192.168.10.55:7777/android_mvvm.zip";
    private Context downLoadContext;
    static SQ_HotLoad mCurrentInstance;
    static ReactInstanceManager reactInstanceManager;
    static String zipUrl = "sdcard/download/zip";
    static String bundleUrl = "sdcard/download/bundle";
    static boolean DONLOADANDRELOAD = false;//下载并加载，默认只是下载
    private SQ_DownLoad sq_downLoad;
    private SQ_LoadNativeMoudle sq_loadNativeMoudle;

    public SQ_HotLoad(Context context, String url) {
        this(context);
        this.serverUrl = url;
    }

    public SQ_HotLoad(Context context, String url, String _zipUrl, String _bundleUrl, boolean down_load) {
        this(context);
        this.serverUrl = url;
        this.zipUrl = _zipUrl;
        this.bundleUrl = _bundleUrl;
        this.DONLOADANDRELOAD = down_load;
    }

    public SQ_HotLoad(Context context) {
        this.downLoadContext = context;
        this.mCurrentInstance = this;

        zipUrl = downLoadContext.getFilesDir().getAbsolutePath() + "/zip";
        bundleUrl = downLoadContext.getFilesDir().getAbsolutePath() + "/bundle";
        sq_downLoad = new SQ_DownLoad(downLoadContext, this);
        File zip_file = new File(zipUrl);
        if (!zip_file.exists()) {
            zip_file.mkdir();
        }

        File bundle_file = new File(bundleUrl);
        if (!bundle_file.exists()) {
            bundle_file.mkdir();
        }
    }


    /**
     * @return 下载zip
     */
    public void startDownLoad() {
        sq_loadNativeMoudle = null;
        sq_downLoad.downLoadZipFromServer();
    }

    public void startDownLoad(SQ_LoadNativeMoudle moudle) {
        sq_loadNativeMoudle = moudle;
        sq_downLoad.downLoadZipFromServer();
    }

    public void startDownLoad(SQ_LoadNativeMoudle moudle, boolean down_load) {
        sq_loadNativeMoudle = moudle;
        DONLOADANDRELOAD = down_load;
        sq_downLoad.downLoadZipFromServer();
    }

    /**
     * @return 解压zip
     * 建议这里进行文件校验，例如MD5 签名验证等
     */
    public void unZipFile( File file) {
        //成功之后，将zip解压
        File zipFile;
        if(file!=null){
            zipFile=file;
        }else{
            zipFile = new File(zipUrl + "/" + SQ_Constants.DOWNLOAD_FILE_NAME);
        }
        try {
            //文件校验，通过之后解压
            SQ_FileUtils.unzipFile(zipFile, bundleUrl);
            if (SQ_Constants.IS_UNZIPFILE) {
                SQ_FileUtils.deleteFileOrFolderSilently(zipFile);
            }
            if (DONLOADANDRELOAD) {
                if (sq_loadNativeMoudle != null) {
                    sq_loadNativeMoudle.loadBundle();
                }
            }
        } catch (IOException e) {
            SQ_Utils.log("解压ZIP包出错，");
            SQ_Constants.IS_UNZIPFILE = false;
            e.printStackTrace();
        }
    }

    public static String getJSBundleFile() {
        return SQ_HotLoad.getJSBundleFile(SQ_Constants.DEFAULT_JS_BUNDLE_NAME);
    }

    /**
     * @param BundleFileName bundle Name
     * @method 判断是否加载assets 中的bundle
     */
    public static String getJSBundleFile(String BundleFileName) {
        if (!TextUtils.isEmpty(BundleFileName)) {
            SQ_Constants.DEFAULT_JS_BUNDLE_NAME = BundleFileName;
        }
        String jsBundleFile = bundleUrl + "/" + SQ_Constants.DOWNLOAD_FILE_NAME + "/" + SQ_Constants.DEFAULT_JS_BUNDLE_NAME;
        File file = new File(jsBundleFile);
        return file != null && file.exists() ? jsBundleFile : null;
    }

    /**
     * 判断是否有新的已经下载的bundle 如果没有 return null
     **/
    public String getNewBundleFilePath(String NewBundleFileName) {
        String newFilePath = bundleUrl + "/" + SQ_Constants.DOWNLOAD_FILE_NAME + "/" + NewBundleFileName;
        File newFile = new File(newFilePath);
        if (newFile.exists()) {
            return newFilePath;
        }
        return null;
    }

    public String getNewBundleFileName() {
        return SQ_Constants.DEFAULT_JS_BUNDLE_NAME;
    }


    public String getServerUrl() {
        return serverUrl;
    }

    static ReactInstanceManager getReactInstanceManager() {
        if (reactInstanceManager == null) {
            return null;
        }
        return reactInstanceManager = ReactInstanceManager.builder().build();
    }

    @Override
    public List<NativeModule> createNativeModules(ReactApplicationContext reactContext) {

        return Arrays.<NativeModule>asList(
                new SQ_LoadNativeMoudle(reactContext, mCurrentInstance)
        );
    }

    @Override
    public List<Class<? extends JavaScriptModule>> createJSModules() {
        return Collections.emptyList();
    }

    @Override
    public List<ViewManager> createViewManagers(ReactApplicationContext reactContext) {
        return Arrays.asList(

        );
    }
}

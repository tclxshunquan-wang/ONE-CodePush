package com.android.codepush;

/**
 * Created by wsq on 2016/10/26.
 */

class SQ_Constants {

    static final String SQ_PUSH_PREFERENCES = "sq_push";//本地数据存储
    static final String ZIP_URL = "zip_url";//zip包路径 默认是sdcard/download
    static final String BUNDLE_URL = "bundle_url";//解压之后bundle路径
    static final String DOWNLOAD_FILE_NAME = "android_mvvm";//zip名称
    static final String SQ_PUSH_HASHNAME = "Code_Hash";//校验文件名称，默认类型支持.json
    static final String LOAD_TYPE = "load_type";//加载方式
    static final String APP_CODE = "app_code";//app code
    static final String APP_VISITION = "app_visition";//app visition
    static boolean IS_UNZIPFILE = false;//zip是否解压成功
    static String DEFAULT_JS_BUNDLE_NAME = "index.android.bundle";//bundle名称

    enum LoadType {
        NEXTSTART, NEXTRESUME, IMMEDIATELY
    }

    enum UpdateType {
        IMMEDIATELY_, IMMEDIATELY_RELOAD
    }
}

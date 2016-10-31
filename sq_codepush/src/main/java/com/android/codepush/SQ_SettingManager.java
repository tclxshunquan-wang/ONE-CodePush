package com.android.codepush;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

/**
 * Created by wsq on 2016/10/26.
 */

class SQ_SettingManager {

    private SharedPreferences mSettings;

    SQ_SettingManager(Context applicationContext) {
        mSettings = applicationContext.getSharedPreferences(SQ_Constants.SQ_PUSH_PREFERENCES, Context.MODE_PRIVATE);
    }

    String getParams(String key) {
        String bundle_url = mSettings.getString(key, null);
        if (TextUtils.isEmpty(bundle_url)) {
            return "";
        }
        return bundle_url;
    }

    void saveParams(String key, String value){
        if(mSettings!=null){
            mSettings.edit().putString(key, value).commit();
        }
    }
}

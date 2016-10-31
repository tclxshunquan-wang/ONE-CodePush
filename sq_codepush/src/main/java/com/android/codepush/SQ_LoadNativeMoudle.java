package com.android.codepush;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;

import com.facebook.react.ReactActivity;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by zhang on 2016/10/26.
 */

public class SQ_LoadNativeMoudle extends ReactContextBaseJavaModule {
    private static final String REACT_APPLICATION_CLASS_NAME = "com.facebook.react.ReactApplication";
    private static final String REACT_NATIVE_HOST_CLASS_NAME = "com.facebook.react.ReactNativeHost";
    private String name = "SQ_CodePush";
    private SQ_HotLoad hotLoad;
    private SQ_SettingManager settingManager;
    private SQ_LoadNativeMoudle instance;

    @Override
    public String getName() {
        return name;
    }

    public SQ_LoadNativeMoudle(ReactApplicationContext reactContext, SQ_HotLoad _hotLoad) {
        super(reactContext);
        this.hotLoad = _hotLoad;
        this.instance = this;
        settingManager=new SQ_SettingManager(reactContext);
    }

    private void loadBundleLegacy() {
        final Activity currentActivity = getCurrentActivity();
        if (currentActivity == null) {
            // The currentActivity can be null if it is backgrounded / destroyed, so we simply
            // no-op to prevent any null pointer exceptions.
            return;
        }
//        mCodePush.invalidateCurrentInstance();

        currentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                currentActivity.recreate();
            }
        });
    }

    // Use reflection to find and set the appropriate fields on ReactInstanceManager. See #556 for a proposal for a less brittle way
    // to approach this.
    private void setJSBundle(ReactInstanceManager instanceManager, String latestJSBundleFile) throws NoSuchFieldException, IllegalAccessException {
        try {
            Field bundleLoaderField = instanceManager.getClass().getDeclaredField("mBundleLoader");
            Class<?> jsBundleLoaderClass = Class.forName("com.facebook.react.cxxbridge.JSBundleLoader");
            Method createFileLoaderMethod = null;

            Method[] methods = jsBundleLoaderClass.getDeclaredMethods();
            for (Method method : methods) {
                if (method.getName() == "createFileLoader") {
                    createFileLoaderMethod = method;
                    break;
                }
            }

            if (createFileLoaderMethod == null) {
                throw new NoSuchMethodException("Could not find a recognized 'createFileLoader' method");
            }

            int numParameters = createFileLoaderMethod.getGenericParameterTypes().length;
            Object latestJSBundleLoader;

            if (numParameters == 1) {
                // RN >= v0.34
                latestJSBundleLoader = createFileLoaderMethod.invoke(jsBundleLoaderClass, latestJSBundleFile);
            } else if (numParameters == 2) {
                // RN >= v0.31 && RN < v0.34
                latestJSBundleLoader = createFileLoaderMethod.invoke(jsBundleLoaderClass, getReactApplicationContext(), latestJSBundleFile);
            } else {
                throw new NoSuchMethodException("Could not find a recognized 'createFileLoader' method");
            }

            bundleLoaderField.setAccessible(true);
            bundleLoaderField.set(instanceManager, latestJSBundleLoader);
        } catch (Exception e) {
            // RN < v0.31
            Field jsBundleField = instanceManager.getClass().getDeclaredField("mJSBundleFile");
            jsBundleField.setAccessible(true);
            jsBundleField.set(instanceManager, latestJSBundleFile);
        }
    }

    @ReactMethod
    public void loadBundle(Promise promise) {
//
//        if (readableMap != null) {
//            if (readableMap.hasKey("AppConfig") && readableMap.getMap("AppConfig")!=null) {
//                if (readableMap.hasKey("appcode") && !readableMap.getString("appcode").isEmpty()) {
//                    settingManager.saveParams(SQ_Constants.APP_CODE,readableMap.getString("appcode"));
//                }
//                if (readableMap.hasKey("appvisition") && !readableMap.getString("appvisition").isEmpty()) {
//                    settingManager.saveParams(SQ_Constants.APP_VISITION,readableMap.getString("appvisition"));
//                }
//            }

//            if (readableMap.hasKey("LoadType") && !readableMap.getString("LoadType").isEmpty()) {
//                if (SQ_Constants.LoadType.IMMEDIATELY.equals(readableMap.getString("LoadType"))) {
//                    //下载完立刻reload
//                    settingManager.saveParams(SQ_Constants.LOAD_TYPE,"IMMEDIATELY");
//                    hotLoad.startDownLoad(instance,true);
//
//                } else if (SQ_Constants.LoadType.NEXTSTART.equals(readableMap.getString("LoadType"))) {
//                    //下次启动
//                    settingManager.saveParams(SQ_Constants.LOAD_TYPE,"NEXTSTART");
//                    hotLoad.startDownLoad(instance,false);
//
//                } else if (SQ_Constants.LoadType.NEXTRESUME.equals(readableMap.getString("LoadType"))) {
//                    //下次resume
//                    settingManager.saveParams(SQ_Constants.LOAD_TYPE,"NEXTRESUME");
//                    hotLoad.startDownLoad(instance,false);
//
//                }
//
//            }
//        } else {
            settingManager.saveParams(SQ_Constants.LOAD_TYPE,"IMMEDIATELY");
            hotLoad.startDownLoad(instance,true);
//        }

    }

    public void loadBundle() {
        //        mCodePush.clearDebugCacheIfNeeded();
        SQ_Utils.log("重新加载新的jsbundle");
        try {
            // #1) Get the ReactInstanceManager instance, which is what includes the
            //     logic to reload the current React context.
            final ReactInstanceManager instanceManager = resolveInstanceManager();
            if (instanceManager == null) {
                return;
            }

            String latestJSBundleFile = hotLoad.getNewBundleFilePath(hotLoad.getNewBundleFileName());
            if (latestJSBundleFile == null) {
                return;
            }
            SQ_Utils.log("latestJSBundleFile:" + latestJSBundleFile);
//            String latestJSBundleFile ="sdcard/download/bundle/"+ SQ_Constants.DOWNLOAD_FILE_NAME+ "/index.android.bundle";
            // #2) Update the locally stored JS bundle file path
            setJSBundle(instanceManager, latestJSBundleFile);

            // #3) Get the context creation method and fire it on the UI thread (which RN enforces)
            final Method recreateMethod = instanceManager.getClass().getMethod("recreateReactContextInBackground");
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    try {
                        recreateMethod.invoke(instanceManager);
//                        mCodePush.initializeUpdateAfterRestart();
                    } catch (Exception e) {
                        // The recreation method threw an unknown exception
                        // so just simply fallback to restarting the Activity (if it exists)
                        loadBundleLegacy();
                    }
                }
            });
        } catch (Exception e) {
            // Our reflection logic failed somewhere
            // so fall back to restarting the Activity (if it exists)
            loadBundleLegacy();
        }
    }

    // Use reflection to find the ReactInstanceManager. See #556 for a proposal for a less brittle way to approach this.
    private ReactInstanceManager resolveInstanceManager() throws NoSuchFieldException, IllegalAccessException {
        ReactInstanceManager instanceManager = SQ_HotLoad.getReactInstanceManager();
        if (instanceManager != null) {
            return instanceManager;
        }

        final Activity currentActivity = getCurrentActivity();
        if (currentActivity == null) {
            return null;
        }
        try {
            // In RN >=0.29, the "mReactInstanceManager" field yields a null value, so we try
            // to get the instance manager via the ReactNativeHost, which only exists in 0.29.
            Method getApplicationMethod = ReactActivity.class.getMethod("getApplication");
            Object reactApplication = getApplicationMethod.invoke(currentActivity);
            Class<?> reactApplicationClass = tryGetClass(REACT_APPLICATION_CLASS_NAME);
            Method getReactNativeHostMethod = reactApplicationClass.getMethod("getReactNativeHost");
            Object reactNativeHost = getReactNativeHostMethod.invoke(reactApplication);
            Class<?> reactNativeHostClass = tryGetClass(REACT_NATIVE_HOST_CLASS_NAME);
            Method getReactInstanceManagerMethod = reactNativeHostClass.getMethod("getReactInstanceManager");
            instanceManager = (ReactInstanceManager) getReactInstanceManagerMethod.invoke(reactNativeHost);
        } catch (Exception e) {
            // The React Native version might be older than 0.29, or the activity does not
            // extend ReactActivity, so we try to get the instance manager via the
            // "mReactInstanceManager" field.
            Class instanceManagerHolderClass = currentActivity instanceof ReactActivity
                    ? ReactActivity.class
                    : currentActivity.getClass();
            Field instanceManagerField = instanceManagerHolderClass.getDeclaredField("mReactInstanceManager");
            instanceManagerField.setAccessible(true);
            instanceManager = (ReactInstanceManager) instanceManagerField.get(currentActivity);
        }
        return instanceManager;
    }

    private Class tryGetClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }


}

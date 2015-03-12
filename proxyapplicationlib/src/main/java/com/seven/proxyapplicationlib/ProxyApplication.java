package com.seven.proxyapplicationlib;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public abstract class ProxyApplication extends Application {

    public abstract void initProxyApplication();

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        initProxyApplication();
    }

    @Override
    public void onCreate() {
//        super.onCreate();
        Log.i("Seven","ProxyApplication onCreate");
        String className = getApplicationName();
        Application delegat = loadClassLoader(className);
//        replaceApplicationContext(delegat);
        setBaseContext(delegat);
    }

    private String getApplicationName() {
        String className = "android.app.Application";
        String key = "DELEGATE_APPLICATION_CLASS_NAME";
        try {
            ApplicationInfo appInfo = getPackageManager().getApplicationInfo(super.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = appInfo.metaData;
            if (bundle != null && bundle.containsKey(key)) {
                className = bundle.getString(key);
                if (className.startsWith(".")) {
                    className = super.getPackageName() + className;
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return className;
    }

    private Application loadClassLoader(String className) {
        Application delegate = null;
        try {
            Class delegateClass = Class.forName(className, true, getClassLoader());
            delegate = (Application) delegateClass.newInstance();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return delegate;
    }

    private void replaceApplicationContext(Application app) {
        try {
            new Smith<Context>(this,"mBase").set(app);
            Context context = getBaseContext();
            Field loadedApkField = context.getClass().getDeclaredField("mPackageInfo");
            loadedApkField.setAccessible(true);
            Object mPackageInfo = loadedApkField.get(context);
            Field field = mPackageInfo.getClass().getDeclaredField("mApplication");
            field.setAccessible(true);
            field.set(mPackageInfo,app);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    private void setBaseContext(Application delegate) {
        try {
            Method attach = Application.class.getDeclaredMethod("attach", Context.class);
            attach.setAccessible(true);
//            attach.invoke(delegate, base);
            attach.invoke(delegate, getBaseContext());
            delegate.onCreate();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getPackageName() {
        return "";
    }
}


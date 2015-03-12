package com.seven.proxyapplicationlib;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

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
        String className = getApplicationName();
        Application delegat = loadClassLoader(className);
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

    private void replaceApplicationContext() {

    }

    private void setBaseContext(Application delegate) {
        try {
            Method attach = Application.class.getDeclaredMethod("attach", Context.class);
            attach.setAccessible(true);
//            attach.invoke(delegate, base);
            delegate.onCreate();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getPackageName() {
        return "";
    }
}


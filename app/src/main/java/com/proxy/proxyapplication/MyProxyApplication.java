package com.proxy.proxyapplication;

import android.content.Context;
import android.util.Log;

import com.seven.proxyapplicationlib.ProxyApplication;

import java.lang.reflect.Field;

import dalvik.system.PathClassLoader;

public class MyProxyApplication extends ProxyApplication {
    @Override
    public void initProxyApplication() {
        Log.i("Seven","MyProxyApplication initProxyApplication");
        Context context = getBaseContext();
        Field loadedApkField;
        Field field;
        try {
            loadedApkField= context.getClass().getDeclaredField("mPackageInfo");
            loadedApkField.setAccessible(true);
            Object mPackageInfo = loadedApkField.get(context);
            field = mPackageInfo.getClass().getDeclaredField("mClassLoader");
            field.setAccessible(true);
            //拿到originalclassloader
            Object mClassLoader = field.get(mPackageInfo);
            //创建自定义的classloader
            ClassLoader loader = new MyPathClassLoader(this,
                    this.getApplicationInfo().sourceDir,
                    (PathClassLoader) mClassLoader);
            //替换originalclassloader为自定义的classloader
            field.set(mPackageInfo, loader);
        } catch (Exception e) {
        }
    }
}

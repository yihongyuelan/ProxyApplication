package com.proxy.proxyapplication;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;

public class TestActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout layout = new LinearLayout(this);
        setContentView(layout);
        getApplicationName();
    }
    private void getApplicationName() {
        try {
            String className = "android.app.Application";
            String key = "DELEGATE_APPLICATION_CLASS_NAME";
            ApplicationInfo appInfo = getPackageManager().getApplicationInfo(super.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = appInfo.metaData;
            if (bundle != null && bundle.containsKey(key)) {
                className = bundle.getString(key);
                Log.i("Seven","name1="+className);
                if (className.startsWith(".")) {
                    className = super.getPackageName() + className;
                    Log.i("Seven","name2="+className);
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
}

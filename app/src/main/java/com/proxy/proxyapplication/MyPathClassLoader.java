package com.proxy.proxyapplication;

import android.content.Context;

import java.io.File;
import java.io.IOException;

import dalvik.system.DexFile;
import dalvik.system.PathClassLoader;

public class MyPathClassLoader extends PathClassLoader {
    private ClassLoader mClassLoader;
    private Context context;
    public MyPathClassLoader(Context context, String dexPath, PathClassLoader mClassLoader) {

        super(dexPath, mClassLoader);
        this.mClassLoader = mClassLoader;
        this.context = context;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        Class clazz = null;
        File file = null;
        try {
        file = new File("/data/data/com.proxy.proxyapplication/lib/libtest.so");
        clazz = mClassLoader.loadClass(name);
        } catch (Exception e) {
        }
        if (clazz != null) {
            return clazz;
        }
        try {
            DexFile dexFile = DexFile.loadDex(file.getAbsolutePath(), context
                    .getDir("dex", 0).getAbsolutePath(), 0);
            return dexFile.loadClass(name, ClassLoader.getSystemClassLoader());
        } catch (IOException e) {

            e.printStackTrace();
        }
        return super.findClass(name);
    }

}

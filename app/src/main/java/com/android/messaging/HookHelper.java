package com.android.messaging;

import android.app.Activity;
import android.app.Instrumentation;

import java.lang.reflect.Field;

public class HookHelper {
    public static void replaceInstrumentation(Activity activity) throws Exception {
        Class<?> k = Activity.class;
        //通过Activity.class 拿到 mInstrumentation字段
        Field field = k.getDeclaredField("mInstrumentation");
        field.setAccessible(true);
        //根据activity内mInstrumentation字段 获取Instrumentation对象
        Instrumentation instrumentation = (Instrumentation) field.get(activity);
        //创建代理对象
        Instrumentation instrumentationProxy = new ActivityProxyInstrumentation(instrumentation);
        //进行替换
        field.set(activity, instrumentationProxy);
    }
}

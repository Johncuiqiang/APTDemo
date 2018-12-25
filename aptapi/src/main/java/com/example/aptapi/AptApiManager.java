package com.example.aptapi;

import android.app.Activity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @Author : cuiqiang
 * @DATE : 2018/12/25 14:16
 * @Description :
 */
public class AptApiManager {

    private static AptApiManager INSTANCE;

    public static AptApiManager getInstance(){
        if(INSTANCE == null){
            synchronized (AptApiManager.class){
                if(INSTANCE == null){
                    INSTANCE = new AptApiManager();
                }
            }
        }
        return INSTANCE;
    }

    public void init(Activity activity){
        Class clazz = activity.getClass();
        try {
            Class bindViewClass = Class.forName(clazz.getName() + "_ViewBinding");
            Method method = bindViewClass.getMethod("bind", activity.getClass());
            method.invoke(bindViewClass.newInstance(), activity);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

//    public void inject(Object target) {
//        String className = target.getClass().getCanonicalName();
//        String helperName = className + "$$AptApiManager";
//        try {
//            IBindHelper helper = (IBindHelper) (Class.forName(helperName).getConstructor().newInstance());
//            helper.inject(target);
//        }   catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}

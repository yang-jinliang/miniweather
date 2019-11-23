package com.example.acer.weatherapplication.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by acer on 2019/11/18.
 */

public class HttpUtil {
    //与服务器交互，获取全国所有省市县的数据（从服务器端）
    public static void sendOkHttpRequest(String address, okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient();//创建http对象
        Request request = new Request.Builder().url(address).build();//创建请求对象
        client.newCall(request).enqueue(callback);//创建一个新的调用，返回的是请求处理
        //发起一条HTTP请求只需要调用sendOkHttpRequest()方法，传入请求地址，并注册一个回调来处理服务器响应就可以了
    }
}

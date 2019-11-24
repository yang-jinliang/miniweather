package com.example.acer.weatherapplication.gson;

/**
 * Created by acer on 2019/11/24.
 */

public class AQI {
    //aqi中有具体内容city，所以定义内部类
    public AQICity city;//对应aqi中的city字段
    public class AQICity{
        //city中也有具体内容，分别是aqi和pm25
        public String aqi;
        public String pm25;
    }
}

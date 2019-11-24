package com.example.acer.weatherapplication.gson;

/**
 * Created by acer on 2019/11/24.
 */

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 还要创建一个总的实例类来引用前面创建的各个实体类
 */
public class Weather {
    //在这个总的实例类中，要对其他类进行引用
    public String status;//这个是只有一个字段，没有内容的一个JSON字段，所以前面没有创建它的实体类，但是在这个总的这里要给出来
    //status字段内容是返回状态，如果返回数据成功，就返回ok，失败就会返回具体失败的原因

    public Basic basic;
    public AQI aqi;
    public Now now;
    public Suggestion suggestion;

    //由于daily_forecast是数组格式，所以用List集合来引用它
    @SerializedName("daily_forecast")
    public List<Forecast> forecastList;
}

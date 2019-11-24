package com.example.acer.weatherapplication.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by acer on 2019/11/24.
 */

/**
 * 用GSON解析数据，为了把天气信息显示出来，要将数据对应的实体类建好
 * 返回数据的格式显示basic，aqi，now，suggestion，daily_forecast的内部还有具体内容，所以定义这五个实体类
 */
public class Basic {
    //basic的数据格式中包含了city，id，update
    @SerializedName("city")//JSON中一些值字段不太适合直接作为Java字段命名，所以这里用@SerializedName注解让JSON字段和Java字段之间建立映射关系
    public String cityName;//对应city，城市名

    @SerializedName("id")
    public String weatherId;//对应id，天气的id

    //update中还包含有具体内容loc，所以定义为内部类
    public Update update;
    public class Update {
        @SerializedName("loc")
        public String updateTime;//对应update中的loc，更新数据的具体日期和时间
    }
}

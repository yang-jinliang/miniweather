package com.example.acer.weatherapplication.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by acer on 2019/11/24.
 */

public class Now {
    @SerializedName("tmp")
    public String temperature;

    @SerializedName("wind_dir")//风向（如305）
    public String windDir;

    @SerializedName("wind_deg")//风向360角度（西北）
    public String windDeg;

    @SerializedName("wind_sc")//风力
    public String windSc;

    @SerializedName("wind_spd")//风速
    public String windSpd;

    @SerializedName("hum")//相对湿度
    public String humidity;

    @SerializedName("cloud")//云量
    public String cloud;

    @SerializedName("pres")//大气压强
    public String pressure;

    @SerializedName("vis")//能见度
    public String visit;

    @SerializedName("cond")
    public More more;

    @SerializedName("fl")//体感温度
    public String feel;

    public class More{
        @SerializedName("txt")
        public String info;//对应cond中的txt，内容是天气信息文本
    }
}

package com.example.acer.weatherapplication.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by acer on 2019/11/24.
 */

public class Suggestion {
    @SerializedName("comf")
    public Comfort comfort;

    @SerializedName("cw")
    public CarWash carWash;

    //可以直接用同一字段名称表示的就不用用注解了
    public Sport sport;

    public class Comfort{
        @SerializedName("txt")
        public String info;//对应comf中的文本，内容为感受提示信息
    }

    public class CarWash{
        @SerializedName("txt")
        public String info;//对应cw中的文本，内容为洗车天气提示
    }

    public class Sport{
        @SerializedName("txt")
        public String info;//对应sport中的文本，内容为运动提示
    }
}

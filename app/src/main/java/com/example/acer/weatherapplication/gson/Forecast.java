package com.example.acer.weatherapplication.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by acer on 2019/11/24.
 */

public class Forecast {
    /**
     * 这个实体类对应的是daily_forecast
     * 但是它比较特殊，包含的是一个数组，数组的每一项都代表着未来一天的天气信息
     * 针对这种情况，我们的实体类定义的是单日的实体类
     * 在之后的声明实体类中使用集合类型来进行声明就好了
     */
    public String date;//日期

    @SerializedName("tmp")//单日声明，所以只需要看单独一项的字段就好了
    public Temperature temperature;//tmp中还有包含具体内容，所以要定义实体类

    @SerializedName("cond")
    public More more;//cond包含的是天气的更多信息，所以这里用more

    public class Temperature{
        public String max;//最高温
        public String min;//最低温
    }

    public class More{
        @SerializedName("txt_d")
        public String info;//对应cond中的txt_d，具体内容为预测未来的天气情况信息
    }
}

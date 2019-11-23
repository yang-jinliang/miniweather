package com.example.acer.weatherapplication.db;

import org.litepal.crud.DataSupport;

/**
 * Created by acer on 2019/11/18.
 */

public class County extends DataSupport {
    private int id;//县的id
    private String countyName;//县名
    private String weatherId;//该地区天气的id
    private int cityId;//县所属市的id

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public String getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }
}

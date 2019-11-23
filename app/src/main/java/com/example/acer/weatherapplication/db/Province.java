package com.example.acer.weatherapplication.db;

import org.litepal.crud.DataSupport;

/**
 * Created by acer on 2019/11/18.
 */

public class Province extends DataSupport {
    private int id;//省的id，实体类必须有的字段
    private String provinceName;//省名
    private int provinceCode;//省代号
    public int getId(){
        return id;
    }
    public void setId(int id){
        this.id = id;
    }
    public String getProvinceName(){
        return provinceName;
    }
    public void setProvinceName(String provinceName){
        this.provinceName = provinceName;
    }
    public int getProvinceCode(){
        return provinceCode;
    }
    public void setProvinceCode(int provinceCode){
        this.provinceCode = provinceCode;
    }
}

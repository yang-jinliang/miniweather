package com.example.acer.weatherapplication;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.acer.weatherapplication.db.City;
import com.example.acer.weatherapplication.db.County;
import com.example.acer.weatherapplication.db.Province;
import com.example.acer.weatherapplication.util.HttpUtil;
import com.example.acer.weatherapplication.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by acer on 2019/11/18.
 */

public class ChooseAreaFragment extends Fragment {
    //遍历省市县的数据碎片（碎片不能直接显示在界面上，要把它添加在活动里）

    public static final int LEVEL_PROVINCE = 0;//省份的等级
    public static final int LEVEL_CITY = 1;//城市的等级
    public static final int LEVEL_COUNTY = 2;//县的等级
    private ProgressDialog progressDialog;
    //定义控件
    private TextView titleText;
    private Button backButton;
    private ListView listView;


    private ArrayAdapter<String> adapter;//定义适配器
    private List<String> dataList = new ArrayList<>();//用来存放临时数据的列表

    private List<Province> provinceList;//省列表
    private List<City> cityList;//市列表
    private List<County> countyList;//县列表

    private Province selectedProvince;//选中的省份
    private City selectedCity;//选中的城市

    private int currentLevel;//当前选中的级别

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        //初始化
        View view = inflater.inflate(R.layout.choose_area, container, false);
        //获取控件实例
        titleText = (TextView) view.findViewById(R.id.title_text);
        backButton = (Button) view.findViewById(R.id.back_button);
        listView = (ListView) view.findViewById(R.id.list_view);
        //初始化ArrayAdapter，并将它设置为ListView的适配器
        adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle saveInstanceState){
        super.onActivityCreated(saveInstanceState);
        //设置listView的监听事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){//列表项选择事件
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){//点击某个省/市/县时会进入此方法
                //再根据当前级别判断接下来调用哪个方法
                if(currentLevel == LEVEL_PROVINCE){//当前选中级别为省级
                    selectedProvince = provinceList.get(position);//把该省级对象记为被选择的省
                    queryCities();//然后调用方法查询该省下属的市
                } else if(currentLevel == LEVEL_CITY){//当前选中级别为市级
                    selectedCity = cityList.get(position);//把该实际对象几位被选中的市
                    queryCounties();//然后调用方法查询该市下属的县
                }else if (currentLevel == LEVEL_COUNTY){
                    //在onItemClick()方法中加入一个if判断，如果当前级别是县级，就启动WeatherActivity
                    //并把当前选中县的天气id传递过去
                    String weatherId = countyList.get(position).getWeatherId();
                    Intent intent = new Intent(getActivity(),WeatherActivity.class);
                    intent.putExtra("weather_id",weatherId);
                    startActivity(intent);
                    getActivity().finish();
                }
            }
        });
        backButton.setOnClickListener(new View.OnClickListener(){
            @Override
            //设置按钮的监听事件
            public void onClick(View v){
                if (currentLevel == LEVEL_COUNTY){//如果当前选中级别是县级
                    queryCities();//调用方法查询县所在的市的下属县级列表，跳转过去
                }else if (currentLevel == LEVEL_CITY){//如果当前选中级别是市级
                    queryProvince();//调用方法查询市所在省的下属市级列表，跳转过去
                }
            }
        });
        queryProvince();
        //在onActivityCreated()方法的最后，调用了queryProvinces()方法，从这里开始甲在省级数据
    }

    /**
     * 查询全国所有的省，优先从数据库查询，如果没有查询到再去服务器上查询
     */
    public void queryProvince(){
        titleText.setText("中国");//首先设置布局的标题，为当前列表中等级的上一级名称
        backButton.setVisibility(View.GONE);//将返回按钮影藏起来，因为省级列表已经不能再返回了
        provinceList = DataSupport.findAll(Province.class);//调用LitePal的查询接口，从数据库中读取省级数据
        if (provinceList.size() > 0){//如果读到了就直接将数据显示在界面上
            dataList.clear();
            for (Province province : provinceList){
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_PROVINCE;
        }else {//如果从数据库中没有读到数据，就从服务器上获取
            String address = "http://guolin.tech/api/china";//组装请求地址
            queryFromServer(address, "province");//调用方法从服务器上查询数据，该方法包括了HttpUtil的访问服务器以及Utility的解析数据
        }
    }

    /**
     * 查询选中省内所有的城市，优先从数据库查询，如果没有查询到再去服务器上查询
     */
    public void queryCities(){
        titleText.setText(selectedProvince.getProvinceName());//设置布局的标题为当前省份名
        backButton.setVisibility(View.VISIBLE);//设置按钮可见，可以返回原来选省的界面
        cityList = DataSupport.where("provinceid = ?", String.valueOf(selectedProvince.getId())).find(City.class);
        //调用where方法参数是条件语句和筛选条件的值，在调用find方法查询
        if (cityList.size() > 0){
            dataList.clear();
            for (City city : cityList){
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_CITY;
        }else {//上面数据库查不到，就上服务器查
            int provinceCode = selectedProvince.getProvinceCode();
            String address = "http://guolin.tech/api/china/" + provinceCode;
            queryFromServer(address, "city");
        }
    }

    /**
     * 查询选中市内所有的县，优先从数据库查询，如果没有查询到再去服务器上查询
     */
    public void queryCounties(){
        titleText.setText(selectedCity.getCityName());
        backButton.setVisibility(View.VISIBLE);
        countyList = DataSupport.where("cityid = ?", String.valueOf(selectedCity.getId())).find(County.class);
        if (countyList.size() > 0){
            dataList.clear();
            for (County county : countyList){
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_COUNTY;
        }else {
            int provinceCode = selectedProvince.getProvinceCode();
            int cityCode = selectedCity.getCityCode();
            String address = "http://guolin.tech/api/china/" + provinceCode + "/" + cityCode;
            queryFromServer(address, "county");
        }
    }

    /**
     * 根据传入的地址和类型从服务器上查询省市县数据
     */
    public void queryFromServer(String address, final String type) {
        showProgressDialog();
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                boolean result = false;
                //调用Utility的方法解析和处理服务器返回的数据，并存储到数据库中
                if("province".equals(type)){
                    result = Utility.handleProvinceResponse(responseText);
                }else if ("city".equals(type)){
                    result = Utility.handleCityResponse(responseText, selectedProvince.getId());
                }else if ("county".equals(type)){
                    result = Utility.handleCountResponse(responseText, selectedCity.getId());
                }
                if (result){//解析完之后再次调用query方法加载数据
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {//通过runOnUiThread()方法回到主线程处理逻辑，实现从子线程切换到主线程
                            closeProgressDialog();
                            if ("province".equals(type)){//因为query方法牵涉到ui操作，因此必须在主线程中调用
                                queryProvince();
                            }else if ("city".equals(type)){
                                queryCities();
                            }else if ("county".equals(type)){
                                queryCounties();
                            }
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call call, IOException e){
                //通过runOnUiThread()方法回到主线程处理逻辑
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getContext(), "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    /**
     * 显示进度对话框
     */
    private void showProgressDialog(){
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    /**
     * 关闭进度对话框
     */
    private void closeProgressDialog(){
        if (progressDialog != null){
            progressDialog.dismiss();
        }
    }
}

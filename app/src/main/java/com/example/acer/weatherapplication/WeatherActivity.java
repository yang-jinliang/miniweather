package com.example.acer.weatherapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.acer.weatherapplication.gson.Forecast;
import com.example.acer.weatherapplication.gson.Weather;
import com.example.acer.weatherapplication.service.AutoUpdateService;
import com.example.acer.weatherapplication.util.HttpUtil;
import com.example.acer.weatherapplication.util.Utility;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {
    //在活动中请求天气数据，将数据展示到界面上

    //定义组件
    private ScrollView weatherLayout;//定义滚动视图布局
    private TextView titleCity;//定义城市标题文本域
    private TextView titleUpdateTime;
    private TextView degreeText;
    private TextView weatherInfoText;
    private LinearLayout forecastLayout;
    private TextView aqiText;
    private TextView pm25Text;
    private TextView comfortText;
    private TextView carWashText;
    private TextView sportText;
    private FrameLayout frameLayout;
    private TextView airText;
    //自己加的
    private TextView tempText;
    private TextView humText;
    private TextView feelText;
    private TextView presText;
    private TextView visText;
    private TextView cloText;
    private ImageView tempShow;
    private ImageView humShow;
    private ImageView feelShow;
    private ImageView presShow;
    private ImageView visShow;
    private ImageView cloShow;

    private ImageView aqiShow;
    private ImageView pm25Show;

    private TextView comfType;
    private TextView carwType;
    private TextView sportType;
    private ImageView comfShow;
    private ImageView sportShow;
    private ImageView carwShow;

    public SwipeRefreshLayout swipeRefresh;//定义下拉刷新布局
    private String mWeatherId;

    //定义滑动窗口布局
    public DrawerLayout drawerLayout;
    private Button navButton;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //实现背景图和状态栏融合
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            );
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_weather);
        //初始化各控件，获取各控件实例
        weatherLayout = (ScrollView) findViewById(R.id.weather_layout);
        titleCity = (TextView) findViewById(R.id.title_city);
        titleUpdateTime = (TextView) findViewById(R.id.title_update_time);
        degreeText = (TextView) findViewById(R.id.degree_text);
        weatherInfoText = (TextView) findViewById(R.id.weather_info_text);
        forecastLayout = (LinearLayout) findViewById(R.id.forecast_layout);
        aqiText = (TextView) findViewById(R.id.aqi_text);
        pm25Text = (TextView) findViewById(R.id.pm25_text);
        comfortText = (TextView) findViewById(R.id.comfort_text);
        carWashText = (TextView) findViewById(R.id.car_wash_text);
        sportText = (TextView) findViewById(R.id.sport_text);
        frameLayout = (FrameLayout) findViewById(R.id.show);
        airText = (TextView) findViewById(R.id.air_quality);

        //自己加的
        tempText = (TextView) findViewById(R.id.temp_text);
        tempShow = (ImageView) findViewById(R.id.temp_show);
        humText = (TextView) findViewById(R.id.hum_text);
        humShow = (ImageView) findViewById(R.id.hum_show);
        feelText = (TextView) findViewById(R.id.feel_text);
        feelShow = (ImageView) findViewById(R.id.feel_show);
        presText = (TextView) findViewById(R.id.pres_text);
        presShow = (ImageView) findViewById(R.id.pres_show);
        visText = (TextView) findViewById(R.id.vis_text);
        visShow = (ImageView) findViewById(R.id.vis_show);
        cloText = (TextView) findViewById(R.id.clo_text);
        cloShow = (ImageView) findViewById(R.id.clo_show);

        aqiShow = (ImageView) findViewById(R.id.aqi_show);
        pm25Show = (ImageView) findViewById(R.id.pm25_show);

        comfShow = (ImageView) findViewById(R.id.comf_show);
        carwShow = (ImageView) findViewById(R.id.carw_show);
        sportShow = (ImageView) findViewById(R.id.sport_show);
        comfType = (TextView) findViewById(R.id.comf_type);
        carwType =  (TextView) findViewById(R.id.carw_type);
        sportType = (TextView) findViewById(R.id.sport_type);

        //下拉更新操作实现代码
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);//获取实例
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);//设置下拉刷新进度条的颜色

        //滑动窗口
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navButton = (Button) findViewById(R.id.nav_button);

        //尝试从本地缓存中读取天气数据
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather", null);

        //final String weatherId;//更新，定义变量记录城市id

        if (weatherString != null) {
            //有缓存时直接解析天气
            Weather weather = Utility.handleWeatherResponse(weatherString);

            mWeatherId = weather.basic.weatherId;//更新，定义变量记录城市id

            showWeatherInfo(weather);
        } else {
            //无缓存是去服务器查询天气数据
            //第一次肯定是没缓存的，因此就会从Intent中取出天气id，并且调用requestWeather()方法从服务器返回请求的天气数据
            //再次进入时缓存已经存在，就会直接解析并显示天气数据，而不会再次发起网络请求了
            mWeatherId = getIntent().getStringExtra("weather_id");
            weatherLayout.setVisibility(View.INVISIBLE);
            //请求数据时要先将ScrollView界面进行隐藏，不然就会呈现奇怪的空数据界面
            requestWeather(mWeatherId);
        }
        //更新
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.
                OnRefreshListener() {//调用方法设置下拉监听器
            @Override
            public void onRefresh() {
                requestWeather(mWeatherId);
            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        //滑动窗口
        navButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    /**
     * 根据天气id请求天气信息
     */
    public void requestWeather(final String weatherId) {
        //使用参数中传入的天气id和我们之前申请号的APIKey拼装出一个接口地址
        String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId
                + "&key=f858f730c34443d1ade359cf62364585";
        //调用HttpUtil.sendOkHttpRequest()方法向上面拼装好的地址发出请求
        //服务器会将相应城市的天气信息以JSON格式返回
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        swipeRefresh.setRefreshing(false);//请求结束后，隐藏刷新进度条
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                //在onResponse()回调中先调用Utility.handleWeatherResponse()方法将返回的JSON数据转换成weather对象
                final Weather weather = Utility.handleWeatherResponse(responseText);
                //再将当前线程切换到主线程
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //然后进行判断，如果服务器返回的status状态是ok，就说明请求天气成功了
                        if (weather != null && "ok".equals(weather.status)) {
                            //请求成功后将返回的数据缓存到SharedPreferences当中
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather", responseText);
                            editor.apply();

                            mWeatherId = weather.basic.weatherId;
                            //调用showWeatherInfo()方法进行内容显示
                            showWeatherInfo(weather);
                        } else {
                            Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        }
                        swipeRefresh.setRefreshing(false);//更新
                    }
                });
            }
        });
    }

    /**
     * 处理并展示Weather实体类中的数据
     */
    private void showWeatherInfo(Weather weather) {
        //进行内容展示的方法
        //首先从weather对象中获取数据
        String cityName = weather.basic.cityName;
        String updateTime = "最近更新:"+weather.basic.update.updateTime.split(" ")[1];
        String degree = weather.now.temperature + "℃";
        String weatherInfo = weather.now.more.info;

        //将获取到的数据显示在控件上
        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
        forecastLayout.removeAllViews();

        //自己加的，显示温湿度，气压体感云量能见度
        tempShow.setImageResource(R.drawable.temp);
        humShow.setImageResource(R.drawable.hum);
        feelShow.setImageResource(R.drawable.feel);
        presShow.setImageResource(R.drawable.pres);
        visShow.setImageResource(R.drawable.visit);
        cloShow.setImageResource(R.drawable.cloud);
        tempText.setText(weather.now.temperature + "℃");
        humText.setText(weather.now.humidity + "％");
        feelText.setText(weather.now.feel + "℃");
        presText.setText(weather.now.pressure + "百帕");
        visText.setText(weather.now.visit + "km");
        cloText.setText(weather.now.cloud + "％");

        //未来几天的天气预报用一个for循环来处理每天的天气信息
        for (Forecast forecast : weather.forecastList) {
//            在循环中动态加载forecast_item.xml布局，并设置相应的数据
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item, forecastLayout, false);
            TextView dateText = (TextView) view.findViewById(R.id.date_text);
            TextView infoText = (TextView) view.findViewById(R.id.info_text);
            TextView maxText = (TextView) view.findViewById(R.id.max_text);
            TextView minText = (TextView) view.findViewById(R.id.min_text);
            //然后将数据添加到父布局当中
            dateText.setText(forecast.date);
            infoText.setText(forecast.more.info);
            maxText.setText(forecast.temperature.max);
            minText.setText(forecast.temperature.min);
            forecastLayout.addView(view);
        }
        if (weather.aqi != null) {
            airText.setText("空气质量    "+weather.aqi.city.qlty);
            aqiText.setText(weather.aqi.city.aqi);
            pm25Text.setText(weather.aqi.city.pm25);
            aqiShow.setImageResource(R.drawable.aqi);
            pm25Show.setImageResource(R.drawable.pm);
        }
        String comfort = weather.suggestion.comfort.info;
        String carWash = weather.suggestion.carWash.info;
        String sport = weather.suggestion.sport.info;
        String comf_type = "舒适度：" + weather.suggestion.comfort.brf;
        String carw_type = "洗车指数：" + weather.suggestion.carWash.brf;
        String sport_type = "运动建议 ：" +weather.suggestion.sport.brf;
        comfortText.setText(comfort);
        carWashText.setText(carWash);
        sportText.setText(sport);
        comfType.setText(comf_type);
        carwType.setText(carw_type);
        sportType.setText(sport_type);
        comfShow.setImageResource(R.drawable.shushi);
        carwShow.setImageResource(R.drawable.carwash);
        sportShow.setImageResource(R.drawable.sport);

        //自己加的

        //设置背景图片
        if (("晴").equals(weatherInfo)) {
            frameLayout.setBackgroundResource(R.drawable.daysun);
        }
        if (("多云").equals(weatherInfo) || ("多云转晴").equals(weatherInfo) || ("多云转阴").equals(weatherInfo)) {
            frameLayout.setBackgroundResource(R.drawable.cloudbg);
        }
        if (("阴").equals(weatherInfo) || ("阴转多云").equals(weatherInfo)) {
            frameLayout.setBackgroundResource(R.drawable.yingbg);
        }
        if (("小雪").equals(weatherInfo) || ("中雪").equals(weatherInfo) || ("大雪").equals(weatherInfo)) {
            frameLayout.setBackgroundResource(R.drawable.snowing);
        }
        if (("小雨").equals(weatherInfo) || ("中雨").equals(weatherInfo)) {
            frameLayout.setBackgroundResource(R.drawable.rainbg);
        }
        if (("大雨").equals(weatherInfo) || ("暴雨").equals(weatherInfo)) {
            frameLayout.setBackgroundResource(R.drawable.dabaoyubg);
        }

        //设置完成所有的布局后，将ScrollView变可见
        weatherLayout.setVisibility(View.VISIBLE);

        //自动更新
        Intent intent = new Intent(this, AutoUpdateService.class);
        startService(intent);

    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Weather Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}
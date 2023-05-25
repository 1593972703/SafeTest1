package com.example.safetest;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapActivity extends AppCompatActivity {
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;


    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        TextView textView = findViewById(R.id.text);
        AMapLocationClient.setApiKey("9b42205a33ba569bcc7992ce1483b8b4");
        //可以通过类implement方式实现AMapLocationListener接口，也可以通过创造接口类对象的方法实现
//以下为后者的举例：
        AMapLocationListener mLocationListener = new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation amapLocation) {
                String address = amapLocation.getAddress();
                StringBuilder sb = new StringBuilder();
                sb.append("当前的位置信息: ");
                sb.append(address + "\n");
                sb.append("经度：" + amapLocation.getLongitude() + "\n");
                sb.append("纬度：" + amapLocation.getLatitude() + "\n");
                textView.setText(sb.toString());

                Log.d("TAg", "onLocationChanged: " + address);
            }
        };
//初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
//设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);

        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();

        if (null != mLocationClient) {
            //设置场景模式后最好调用一次stop，再调用start以保证场景模式生效
            mLocationClient.stopLocation();
            mLocationClient.startLocation();
        }

//设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);

        //获取一次定位结果：
//该方法默认为false。
        mLocationOption.setOnceLocation(true);

//获取最近3s内精度最高的一次定位结果：
//设置setOnceLocationLatest(boolean b)接口为true，启动定位时SDK会返回最近3s内精度最高的一次定位结果。如果设置其为true，setOnceLocation(boolean b)接口也会被设置为true，反之不会，默认为false。
        mLocationOption.setOnceLocationLatest(true);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);

        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
//启动定位
        mLocationClient.startLocation();


    }


    //定义一个更新显示的方法
    private String updateShow() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //从GPS获取最近的定位信息
        @SuppressLint("MissingPermission") Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        boolean isGpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (location != null) {
            StringBuilder sb = new StringBuilder();
            // 定义位置解析
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            try {
                // 获取经纬度对于的位置
                // getFromLocation(纬度, 经度, 最多获取的位置数量)
                List<Address> addresses = geocoder.getFromLocation(
                        LocationUtils.getInstance(this).showLocation().getLatitude(),
                        LocationUtils.getInstance(this).showLocation().getLongitude(), 1);
                // 得到第一个经纬度位置解析信息
                Address address = addresses.get(0);
                // 获取到详细的当前位置
                // Address里面还有很多方法你们可以自行实现去尝试。比如具体省的名称、市的名称...
                sb.append("当前的位置信息: ");
                String info =
                        address.getAddressLine(0) + // 获取国家名称
                                address.getAddressLine(1) + // 获取省市县(区)
                                address.getAddressLine(2);
                sb.append(info + "\n");
                sb.append("经度：" + address.getLongitude() + "\n");
                sb.append("纬度：" + address.getLatitude() + "\n");

                Toast.makeText(this, sb.toString(), Toast.LENGTH_SHORT).show();

                ;  // 获取镇号(地址名称)
                // 赋值
                return sb.toString();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();


            }

        }
        return "";
    }

    public void back(View view) {
        finish();
    }
}

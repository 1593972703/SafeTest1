package com.example.safetest;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;


public class WebActivity extends AppCompatActivity {

    private String type;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        try {
            type = getIntent().getStringExtra("type");
        } catch (Exception e) {
            e.printStackTrace();
        }

        String networkState = NetworkUtils.getNetworkState(this);
        String netType = "";
        switch (networkState) {
            case NetworkUtils.NETWORK_OTHER:
                netType = NetworkUtils.NETWORK_OTHER;
                break;
            case NetworkUtils.NETWORK_2G:
            case NetworkUtils.NETWORK_3G:
            case NetworkUtils.NETWORK_4G:
            case NetworkUtils.NETWORK_5G:
                netType = "移动网络";
                break;
            case NetworkUtils.NETWORK_WIFI:
                netType = NetworkUtils.NETWORK_WIFI;
                break;
        }

        if (netType.equals(NetworkUtils.NETWORK_OTHER)) {
            Toast.makeText(this, NetworkUtils.NETWORK_OTHER, Toast.LENGTH_SHORT).show();
            return;
        }
        if (!type.equals(netType)) {
            String te = "本机已连接网络为" + netType + ",当前限制只有" + type + "可加载网页";
            Toast.makeText(this, te, Toast.LENGTH_SHORT).show();
            return;
        }

        WebView webView = findViewById(R.id.web);
//如果页面中使用了JavaScript，不加改代码页面不显示。
        webView.getSettings().setJavaScriptEnabled(true);
//加载页面时如果不加改代码，页面会跳转到系统自带浏览器显示。
        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;//返回值为true时在WebView中打开，为false时调用浏览器打开
            }
        });
        webView.loadUrl("https://www.baidu.com");
    }

    public void back(View view) {
        finish();
    }
}

package com.cs65.colorpal.views.activities;

import android.os.Bundle;
import android.webkit.WebView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.cs65.colorpal.R;

public class ColorInfoActivity extends AppCompatActivity {
    public static final String COLOR_TAG = "COLOR_TAG";
    private String colorHexValue = "";
    private WebView webView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_info);
        colorHexValue = getIntent().getStringExtra(COLOR_TAG);
        String apiUrl = "https://www.thecolorapi.com/id?hex="+colorHexValue+"&format=html";
        webView = findViewById(R.id.color_info_webview);
        webView.loadUrl(apiUrl);

    }
}

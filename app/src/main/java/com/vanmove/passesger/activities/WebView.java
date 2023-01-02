package com.vanmove.passesger.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.vanmove.passesger.R;
import com.vanmove.passesger.utils.CONSTANTS;

public class WebView extends AppCompatActivity {

    private android.webkit.WebView wb;
    ProgressBar progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        ImageView iv_back = findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        progressDialog = findViewById(R.id.progress);



        wb = findViewById(R.id.my_web);

        wb.getSettings().setJavaScriptEnabled(true);
        wb.getSettings().setLoadWithOverviewMode(true);
        wb.getSettings().setUseWideViewPort(true);
        wb.getSettings().setDomStorageEnabled(true);
        wb.getSettings().setPluginState(WebSettings.PluginState.ON);
        wb.getSettings().setAppCacheEnabled(false);
        wb.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        wb.setWebViewClient(new MyWebViewClient());
        wb.loadUrl( CONSTANTS.Help_Link);

    }




    private class MyWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(android.webkit.WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageStarted(android.webkit.WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);


            progressDialog.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageFinished(android.webkit.WebView view, String url) {
            super.onPageFinished(view, url);
            progressDialog.setVisibility(View.GONE);

        }
    }

}
package com.teammanagementapp.ankush.streamer.activity.anime;

import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.teammanagementapp.ankush.streamer.R;

public class KissAnime extends AppCompatActivity {

    private WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kiss_anime);

        webView=findViewById(R.id.webview);
        setWebVew();
        webView.loadUrl("http://www.kissanime.ru");
        webView.getSettings().setJavaScriptEnabled(true);
    }

    private void setWebVew(){

        webView.setWebViewClient(new WebViewClient(){
            @Nullable
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {

                Log.e("resous",request.getUrl().toString());


                return super.shouldInterceptRequest(view, request);



            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

            }

            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);
                // Removes element which id = 'mastHead'
                view.loadUrl("javascript:(function() { " +
                        "(elem = document.getElementById('adsIfrme1')).style.visibility = \"hidden\"; " +
                        "})()");
            }
        });


    }




}

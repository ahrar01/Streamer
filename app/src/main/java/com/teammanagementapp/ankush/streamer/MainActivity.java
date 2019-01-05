package com.teammanagementapp.ankush.streamer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import org.adblockplus.libadblockplus.android.webview.AdblockWebView;

public class MainActivity extends AppCompatActivity {

    private ProgressBar progress;
    private EditText url;
    private Button ok;
    private Button back;
    private Button forward;
    private Button settings;
    private AdblockWebView webView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }



    private void bindControls()
    {
        url = (EditText) findViewById(R.id.main_url);
        ok = (Button) findViewById(R.id.main_ok);
        back = (Button) findViewById(R.id.main_back);
        forward = (Button) findViewById(R.id.main_forward);
        settings = (Button) findViewById(R.id.main_settings);
        progress = (ProgressBar) findViewById(R.id.main_progress);
        webView = (AdblockWebView) findViewById(R.id.main_webview);
    }


}

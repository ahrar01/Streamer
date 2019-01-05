package com.teammanagementapp.ankush.streamer;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import org.adblockplus.libadblockplus.android.settings.AdblockHelper;
import org.adblockplus.libadblockplus.android.webview.AdblockWebView;

public class MainActivity extends AppCompatActivity {

    private ProgressBar progress;
    private EditText url;
    private Button ok;
    private Button back;
    private Button forward;
    private Button settings;
    private AdblockWebView webView;
    public static final boolean USE_EXTERNAL_ADBLOCKENGINE = false;
    public static final boolean DEVELOPMENT_BUILD = true;


    private WebChromeClient webChromeClient = new WebChromeClient() {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            progress.setProgress(newProgress);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // binding controls to elements from xml to java
        bindControls();

        initControls();


    }

    private void bindControls() {
        url = (EditText) findViewById(R.id.main_url);
        ok = (Button) findViewById(R.id.main_ok);
        back = (Button) findViewById(R.id.main_back);
        forward = (Button) findViewById(R.id.main_forward);
        settings = (Button) findViewById(R.id.main_settings);
        progress = (ProgressBar) findViewById(R.id.main_progress);
        webView = (AdblockWebView) findViewById(R.id.main_webview);
        webView.loadUrl("https://www.google.com");
    }

    private void setProgressVisible(boolean visible) {
        progress.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }

    private void updateButtons() {
        //clicklabe only if changing is possible
        back.setEnabled(webView.canGoBack());
        forward.setEnabled(webView.canGoForward());
    }

    private void initControls() {
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadUrl();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadPrev();
            }
        });

        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadForward();
            }
        });

        if (USE_EXTERNAL_ADBLOCKENGINE) {
            settings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    navigateSettings();
                }
            });
        } else {
            // no external ablock is there so hiding the setting button

            settings.setVisibility(View.GONE);
        }

        initAdblockWebView();

        setProgressVisible(false);
        updateButtons();

        // to get debug/warning log output
        webView.setDebugMode(DEVELOPMENT_BUILD);

        // render as fast as we can
        webView.setAllowDrawDelay(0);

        // to show that external WebViewClient is still working
        webView.setWebViewClient(webViewClient);

        // to show that external WebChromeClient is still working
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                progress.setProgress(newProgress);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                getSupportActionBar().setTitle(title);
            }
        });
    }


    private WebViewClient webViewClient = new WebViewClient() {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            setProgressVisible(true);

            // show updated URL (because of possible redirection)
            MainActivity.this.url.setText(url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            setProgressVisible(false);
            updateButtons();
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            updateButtons();
        }
    };

    private void initAdblockWebView() {
        if (USE_EXTERNAL_ADBLOCKENGINE) {
            // external AdblockEngine
            webView.setProvider(AdblockHelper.get().getProvider());
        } else {
            // AdblockWebView will create internal AdblockEngine instance

        }
    }

    private void loadPrev() {
        hideSoftwareKeyboard();
        if (webView.canGoBack()) {
            webView.goBack();
        }
    }

    private void hideSoftwareKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(url.getWindowToken(), 0);
    }

    private void loadForward() {
        hideSoftwareKeyboard();
        if (webView.canGoForward()) {
            webView.goForward();
        }
    }

    private void loadUrl() {
        hideSoftwareKeyboard();
        webView.loadUrl(prepareUrl(url.getText().toString()));
    }

    private String prepareUrl(String url) {
        if (!url.startsWith("http"))
            url = "http://" + url;

        // make sure url is valid URL
        return url;
    }

    private void navigateSettings() {
        //startActivity(new Intent(this, SettingsActivity.class));
    }


    @Override
    protected void onDestroy() {
        webView.dispose(null);

        super.onDestroy();
    }
}

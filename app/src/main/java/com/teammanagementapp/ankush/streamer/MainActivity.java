package com.teammanagementapp.ankush.streamer;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.support.v7.widget.SearchView;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.adblockplus.libadblockplus.android.settings.AdblockHelper;
import org.adblockplus.libadblockplus.android.webview.AdblockWebView;

public class MainActivity extends AppCompatActivity {

    private ProgressBar progress;
    private EditText url;
    private AdblockWebView webView;
    public static final boolean USE_EXTERNAL_ADBLOCKENGINE = false;
    public static final boolean DEVELOPMENT_BUILD = true;

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
        progress = (ProgressBar) findViewById(R.id.main_progress);
        webView = (AdblockWebView) findViewById(R.id.main_webview);
        webView.loadUrl("https://www.google.com");

        url.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    loadUrl(url.getText().toString());
                    return true;
                }
                return false;
            }
        });
    }

    private void setProgressVisible(boolean visible) {
        progress.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }

    private void initControls() {

        initAdblockWebView();

        setProgressVisible(false);

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

        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                Uri myUri = Uri.parse(url);
                Intent superIntent = new Intent(Intent.ACTION_VIEW);
                superIntent.setData(myUri);
                startActivity(superIntent);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater myMenuInflater = getMenuInflater();
        myMenuInflater.inflate(R.menu.super_menu, menu);

        /*
         **Search Bar
         */
        final MenuItem searchViewItem = menu.findItem(R.id.action_search);
        final SearchView searchViewAndroidActionBar = (SearchView) MenuItemCompat.getActionView(searchViewItem);
        searchViewAndroidActionBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchViewAndroidActionBar.clearFocus();
                searchViewItem.collapseActionView();
                loadUrl(query);


                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.backArrow:
                loadPrev();
                break;

            case R.id.forwardArrow:
                loadForward();
                break;

            case R.id.download:

                break;

            case R.id.Settings:

                break;
        }
        return true;
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
            //attaching my own js script here

        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
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
        } else {
            Toast.makeText(this, "Can't go further!", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadUrl(String query) {
        hideSoftwareKeyboard();
        webView.loadUrl(prepareUrl(query));
    }

    private String prepareUrl(String query) {

        if (query.startsWith("http"))
            return query;
        else

        if (query.startsWith("www"))
            return  "http://" + query;
        // make sure url is valid URL
        if(Patterns.WEB_URL.matcher(query).matches()){
            return "http://www." +query;
        }else{
                String s[]=query.split(" ");
                query="";
                for (String tmp:s) {
                    if (query.equals("")){
                        query=query + tmp;
                    }else{
                        query=query + "+" +tmp;
                    }
                }
            return "https://www.google.com/search?q=" + query;
        }
    }


    @Override
    protected void onDestroy() {
        webView.dispose(null);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("Exit App");
            dialog.setMessage("Browser has nothing to go back, so what next?");
            dialog.setPositiveButton("EXIT ME", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            dialog.setCancelable(false);
            dialog.setNegativeButton("STAY HERE", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).show();

        }
    }
}

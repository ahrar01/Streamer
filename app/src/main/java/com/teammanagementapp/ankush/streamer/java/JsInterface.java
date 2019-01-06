package com.teammanagementapp.ankush.streamer.java;

import android.webkit.JavascriptInterface;

public class JsInterface {


    public JsInterface() {
    }

    @JavascriptInterface
    public String testNativeMethod() {
        return "Java method called!!";
    }


}

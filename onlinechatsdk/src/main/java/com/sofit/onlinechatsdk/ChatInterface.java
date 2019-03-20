package com.sofit.onlinechatsdk;

import android.webkit.JavascriptInterface;

class ChatInterface {

    private ChatListener mListener;

    ChatInterface(ChatListener mListener) {
        this.mListener = mListener;
    }

    @JavascriptInterface
    public void action(String name, String jsonData) {
        if (this.mListener != null) {
            this.mListener.onEvent(name, jsonData);
        }
    }
}
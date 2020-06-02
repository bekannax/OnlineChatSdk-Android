package com.sofit.onlinechatsdk;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.RequiresApi;

class ChatWebViewClient extends WebViewClient {

    private Activity parent;

    ChatWebViewClient(Activity parent, ChatView chat) {
        this.parent = parent;
    }

    @RequiresApi(21)
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        if (this.parent == null) {
            return false;
        }
        try {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, request.getUrl());
            browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            this.parent.startActivity(browserIntent);
        } catch (Exception e) {
            Log.e("onlinechat.sdk", e.toString());
        }
        return true;
    }
}
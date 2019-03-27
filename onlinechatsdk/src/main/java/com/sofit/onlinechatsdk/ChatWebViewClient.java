package com.sofit.onlinechatsdk;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

class ChatWebViewClient extends WebViewClient {

    private Activity parent;
    private ChatView chat;

    ChatWebViewClient(Activity parent, ChatView chat) {
        this.parent = parent;
        this.chat = chat;
    }

    @RequiresApi(21)
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if (this.parent == null) {
            return false;
        }
        try {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            this.parent.startActivity(browserIntent);
        } catch (Exception e) {
            Log.e("onlinechat.sdk", e.toString());
        }
        return true;
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        if (this.chat != null) {
            this.chat.setFinished(true);
            this.chat.callJs();
        }
    }
}
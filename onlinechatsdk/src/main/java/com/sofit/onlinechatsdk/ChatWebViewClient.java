package com.sofit.onlinechatsdk;

import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.RequiresApi;

public class ChatWebViewClient extends WebViewClient {

    private final ChatView chat;

    ChatWebViewClient(ChatView chat) {
        this.chat = chat;
    }

    @RequiresApi(21)
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        if (this.chat == null) {
            return false;
        }
        this.chat.onEvent(ChatView.event_linkPressed, MyJsonObject.create().Put("link", request.getUrl().toString()).toString() );
        return true;
    }
}
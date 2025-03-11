package com.sofit.onlinechatsdk;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

public class ChatWebViewClient extends WebViewClient {

    private final Context context;
    private final ChatView chat;
    private ProgressDialog progressDialog;

    ChatWebViewClient(Context context, ChatView chat) {
        this.context = context;
        this.chat = chat;
    }

    private void showMessage(String text) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_layout, null);
        TextView textView = dialogView.findViewById(R.id.text_view_message);
        textView.setText(text);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);
        builder.setPositiveButton("ОК", (dialog, which) -> {
            chat.closeSupport();
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
//        Log.d(ChatView.logTag, "ChatWebViewClient :: onPageStarted");
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        chat.injectCss();
        super.onPageFinished(view, url);
        chat.hideLoading();
//        Log.d(ChatView.logTag, "ChatWebViewClient :: onPageFinished");
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

    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        super.onReceivedError(view, request, error);
        chat.hideLoading();
        if (chat.isFinished()) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            showMessage("Loading error: " + error.getDescription().toString());
        } else {
            showMessage("Loading error");
        }
//        Log.d(ChatView.logTag, "ChatWebViewClient :: onReceivedError :: " + error.getDescription());
    }
}
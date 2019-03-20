package com.sofit.onlinechatsdk;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

class ChatChromeClient extends WebChromeClient {

    private Activity parent;
    private ValueCallback<Uri[]> filePathCallback;
    private WebView webView;
    private FileChooserParams fileChooserParams;

    ChatChromeClient(Activity parent) {
        this.parent = parent;
    }

    private boolean checkStoragePermission() {
        if (this.parent == null) {
            return false;
        }
        if (Build.VERSION.SDK_INT >= 23 &&
            ContextCompat.checkSelfPermission(this.parent, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this.parent, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            return false;
        }
        return true;
    }

    void onReceiveValue(Uri uri) {
        if (this.filePathCallback != null) {
            this.filePathCallback.onReceiveValue(new Uri[]{uri});
        }
    }

    void onShowFileChooser() {
        if (this.filePathCallback == null) {
            return;
        }
        onShowFileChooser(this.webView, this.filePathCallback, fileChooserParams);
    }

    @Override
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
        if (this.parent == null) {
            return false;
        }
        this.webView = webView;
        this.filePathCallback = filePathCallback;
        this.fileChooserParams = fileChooserParams;
        if (checkStoragePermission()) {
            Intent pickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
            pickerIntent.setType("*/*");
            this.parent.startActivityForResult(pickerIntent, 0);
        }
        return true;
    }
}
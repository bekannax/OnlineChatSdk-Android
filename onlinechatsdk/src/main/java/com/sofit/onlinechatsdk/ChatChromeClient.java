package com.sofit.onlinechatsdk;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import java.util.ArrayList;
import java.util.List;

import androidx.core.app.ActivityCompat;

public class ChatChromeClient extends WebChromeClient {

    private Activity parent;
    private ValueCallback<Uri[]> filePathCallback;
    private WebView webView;
    private FileChooserParams fileChooserParams;

    ChatChromeClient(Activity parent) {
        this.parent = parent;
    }

//    private boolean checkStoragePermission() {
//        if (this.parent == null) {
//            return false;
//        }
//        if (Build.VERSION.SDK_INT >= 23 &&
//            ContextCompat.checkSelfPermission(this.parent, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
//        {
//            ActivityCompat.requestPermissions(this.parent, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
//            return false;
//        }
//        return true;
//    }

    private boolean checkStoragePermission() {
        if (this.parent == null) {
            return false;
        }

        List<String> PERMISSIONS_APP = new ArrayList<>();

        if (android.os.Build.VERSION.SDK_INT >= 33) {
            int permissionReadMediaImages = ActivityCompat.checkSelfPermission(this.parent, Manifest.permission.READ_MEDIA_IMAGES);
            if (permissionReadMediaImages != PackageManager.PERMISSION_GRANTED) {
                PERMISSIONS_APP.add(Manifest.permission.READ_MEDIA_IMAGES);
            }
            int permissionReadMediaAudio = ActivityCompat.checkSelfPermission(this.parent, Manifest.permission.READ_MEDIA_AUDIO);
            if (permissionReadMediaAudio != PackageManager.PERMISSION_GRANTED) {
                PERMISSIONS_APP.add(Manifest.permission.READ_MEDIA_AUDIO);
            }

            int permissionReadMediaVideo = ActivityCompat.checkSelfPermission(this.parent, Manifest.permission.READ_MEDIA_VIDEO);
            if (permissionReadMediaVideo != PackageManager.PERMISSION_GRANTED) {
                PERMISSIONS_APP.add(Manifest.permission.READ_MEDIA_VIDEO);
            }

            int permissionPostNotifications = ActivityCompat.checkSelfPermission(this.parent, Manifest.permission.POST_NOTIFICATIONS);
            if (permissionPostNotifications != PackageManager.PERMISSION_GRANTED) {
                PERMISSIONS_APP.add(Manifest.permission.POST_NOTIFICATIONS);
            }

        } else {
            int permissionWrite = ActivityCompat.checkSelfPermission(this.parent, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permissionWrite != PackageManager.PERMISSION_GRANTED) {
                PERMISSIONS_APP.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }

            int permissionRead = ActivityCompat.checkSelfPermission(this.parent, Manifest.permission.READ_EXTERNAL_STORAGE);
            if (permissionRead != PackageManager.PERMISSION_GRANTED) {
                PERMISSIONS_APP.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }

        if (!PERMISSIONS_APP.isEmpty()) {
            String[] permArr = new String[PERMISSIONS_APP.size()];
            PERMISSIONS_APP.toArray(permArr);
            ActivityCompat.requestPermissions(
                this.parent,
                permArr,
                0
            );
            return false;
        }

        return true;
    }

    void onReceiveValue(Uri uri) {
        if (this.filePathCallback != null) {
            if (uri == null) {
                uri = Uri.parse("");
            }
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
//        if (checkStoragePermission()) {
            Intent pickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
            pickerIntent.setType("*/*");
            this.parent.startActivityForResult(pickerIntent, 0);
//        }
        return true;
    }
}
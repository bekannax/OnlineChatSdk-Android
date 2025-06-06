package com.sofit.onlinechatsdk;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

public class ChatChromeClient extends WebChromeClient {

    private Activity parent;
    private ValueCallback<Uri[]> filePathCallback;
    private WebView webView;
    private FileChooserParams fileChooserParams;
    private ChatFileChooser chatFileChooser;
    private PermissionRequest currentRequest = null;
    private Map<String, Boolean> currentRequestResources = new HashMap<>();

    ChatChromeClient(Activity parent) {
        this.parent = parent;
    }

    ChatChromeClient(Activity parent, ChatFileChooser chatFileChooser) {
        this.parent = parent;
        this.chatFileChooser = chatFileChooser;
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


    @Override
    public void onPermissionRequest(PermissionRequest request) {
//        super.onPermissionRequest(request);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            Log.d(ChatView.logTag, "onPermissionRequest :: " + Arrays.toString(request.getResources()));
            checkMediaPermission(request);
        }
    }

    public void onRequestPermissionsGranted() {
        if (currentRequest != null && !currentRequestResources.isEmpty()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                String[] requestResources = new String[currentRequestResources.size()];
                int i = 0;
                for(Map.Entry<String, Boolean> entry : currentRequestResources.entrySet()) {
                    requestResources[i] = entry.getKey();
                }
                currentRequest.grant(requestResources);
                currentRequestResources.clear();
                currentRequest = null;
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void updateCurrentRequest(PermissionRequest request) {
        for (String res: request.getResources()) {
            currentRequestResources.put(res, true);
        }
        currentRequest = request;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void checkMediaPermission(PermissionRequest request) {
        if (this.parent == null) {
            return; //  false
        }
        List<String> PERMISSIONS_APP = new ArrayList<>();
        int permissionCamera = ActivityCompat.checkSelfPermission(this.parent, Manifest.permission.CAMERA);
        if (permissionCamera != PackageManager.PERMISSION_GRANTED) {
            PERMISSIONS_APP.add(Manifest.permission.CAMERA);
            updateCurrentRequest(request);
        }
        int permissionRecordAudio = ActivityCompat.checkSelfPermission(this.parent, Manifest.permission.RECORD_AUDIO);
        if (permissionRecordAudio != PackageManager.PERMISSION_GRANTED) {
            PERMISSIONS_APP.add(Manifest.permission.RECORD_AUDIO);
            updateCurrentRequest(request);
        }
        if (!PERMISSIONS_APP.isEmpty()) {
            String[] permArr = new String[PERMISSIONS_APP.size()];
            PERMISSIONS_APP.toArray(permArr);
            ActivityCompat.requestPermissions(
                this.parent,
                permArr,
                0
            );
//            return; //  false
        } else {
            request.grant(request.getResources());
        }
//        return; //  true
    }

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
            if (chatFileChooser == null) {
                Intent pickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
                pickerIntent.setType("*/*");
                this.parent.startActivityForResult(pickerIntent, 0);
            } else {
                chatFileChooser.onShowFileChooser(webView, filePathCallback, fileChooserParams);
            }
//        }
        return true;
    }

    public void setOnShowFileChooser(ChatFileChooser chatFileChooser) {
        this.chatFileChooser = chatFileChooser;
    }

//    @Override
//    public void onProgressChanged(WebView view, int newProgress) {
////        super.onProgressChanged(view, newProgress);
//
//    }
}
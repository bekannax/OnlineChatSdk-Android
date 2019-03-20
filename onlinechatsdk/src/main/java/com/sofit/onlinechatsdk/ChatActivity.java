package com.sofit.onlinechatsdk;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;

public abstract class ChatActivity extends Activity {

    private ChatView chatView;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData();
                if (this.chatView != null) {
                    this.chatView.onReceiveValue(uri);
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (this.chatView != null) {
                this.chatView.onShowFileChooser();
            }
        }
    }

    public void setChatView(ChatView chatView) {
        this.chatView = chatView;
    }

    public ChatView getChatView() {
        return this.chatView;
    }
}
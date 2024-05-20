package com.sofit.onlinechatsdk;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;

import androidx.annotation.NonNull;

public abstract class ChatActivity extends Activity implements ChatViewHelper {

    private ChatView chatView;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData();
                if (this.chatView != null) {
                    this.chatView.onReceiveValue(uri);
                    return;
                }
            }
        }
        if (this.chatView != null) {
            this.chatView.onReceiveValue(null);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (chatView != null) {
            chatView.destroy();
        }
        chatView = null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (this.chatView != null) {
                this.chatView.onShowFileChooser();
            }
        }
    }

    @Override
    public void onLinkPressed(String link) {
        ChatView chat = getChatView();
        if (chat != null) {
            chat.openLink(link);
        }
    }

    @Override
    public void onCloseSupport() {
        finish();
    }

    @Override
    public void setChatView(ChatView chatView) {
        this.chatView = chatView;
    }

    @Override
    public ChatView getChatView() {
        return this.chatView;
    }

    @Override
    public void setChatViewById(int id) {
        ChatView findChatView = findViewById(id);
        if (findChatView != null) {
            setChatView(findChatView);
        }
    }

    @Override
    public void onDestroyChatView() {
        if (chatView != null) {
            chatView.destroy();
            chatView = null;
        }
    }
}

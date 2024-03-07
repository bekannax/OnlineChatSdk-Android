package com.sofit.onlinechatsdk;

import android.net.Uri;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public abstract class ChatActivity extends AppCompatActivity {

    private ChatView chatView;
    ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
        new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri uri) {
                chatView.onReceiveValue(uri);
            }
        });

    public void onLinkPressed(String link) {
        ChatView chat = getChatView();
        if (chat != null) {
            chat.openLink(link);
        }
    }

    public void onCloseSupport() {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (chatView != null) {
            chatView.destroy();
        }
        chatView = null;
    }

    public void setChatView(ChatView chatView) {
        this.chatView = chatView;
        this.chatView.setOnShowFileChooser((webView, filePathCallback, fileChooserParams) -> {
            mGetContent.launch("*/*");
        });
    }

    public ChatView getChatView() {
        return this.chatView;
    }
}
package com.sofit.onlinechatsdk;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public abstract class ChatAppCompatActivity extends AppCompatActivity implements ChatViewHelper {

    private ChatView chatView;

    ActivityResultLauncher<String> mGetContent = registerForActivityResult(
        new ActivityResultContracts.GetContent(),
        uri -> getChatView().onReceiveValue(uri)
    );

    @Override
    protected void onResume() {
        super.onResume();
        onChatWasOpen();
    }

    @Override
    protected void onStop() {
        super.onStop();
        onChatWasClosed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        onDestroyChatView();
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
    public void onDestroyChatView() {
        if (chatView != null) {
            chatView.destroy();
            chatView = null;
        }
    }

    @Override
    public void setChatView(ChatView chatView) {
        this.chatView = chatView;
        this.chatView.setOnShowFileChooser((webView, filePathCallback, fileChooserParams) -> {
            mGetContent.launch("*/*");
        });
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
}
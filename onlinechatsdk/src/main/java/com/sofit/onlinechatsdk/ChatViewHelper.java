package com.sofit.onlinechatsdk;

public interface ChatViewHelper {
    ChatView getChatView();
    void setChatView(ChatView chatView);
    void setChatViewById(int id);
    void onLinkPressed(String link);
    void onDestroyChatView();
    void onCloseSupport();
}

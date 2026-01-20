package com.sofit.onlinechatsdk.chatapi.models;

public class ChatApiGetClientUnreadMessageCountResponseModel {
    public ChatApiResponseErrorModel error;
    public int result;
    public ChatApiGetClientUnreadMessageCountResponseModel(ChatApiResponseErrorModel error) {
        this.error = error;
        this.result = 0;
    }
    public ChatApiGetClientUnreadMessageCountResponseModel(int result) {
        this.error = null;
        this.result = result;
    }
}

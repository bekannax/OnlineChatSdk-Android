package com.sofit.onlinechatsdk.chatapi.models;

public class ChatApiResponseModel {
    public ChatApiResponseErrorModel error;
    public String result;
    public ChatApiResponseModel(ChatApiResponseErrorModel error) {
        this.error = error;
        result = "";
    }
    public ChatApiResponseModel(String result) {
        this.error = null;
        this.result = result;
    }
}

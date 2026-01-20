package com.sofit.onlinechatsdk.chatapi.models;

public class ChatApiResponseErrorModel {
    public int code;
    public String descr;
    public ChatApiResponseErrorModel(int code, String descr) {
        this.code = code;
        this.descr = descr;
    }
}
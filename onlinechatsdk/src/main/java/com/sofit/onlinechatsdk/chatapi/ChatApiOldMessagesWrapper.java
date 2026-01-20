package com.sofit.onlinechatsdk.chatapi;

public class ChatApiOldMessagesWrapper {

    private MyJsonObject result;
    private MyJsonArray dataArray;
    private MyJsonObject data;
    private MyJsonArray messages;

    public ChatApiOldMessagesWrapper(MyJsonObject response) {
        this.result = response;
        this.dataArray = response.GetJsonArray("result");
        this.data = this.dataArray.GetJsonObject(0);
        this.messages = this.data.GetJsonArray("messages");
    }

    public MyJsonArray getMessages() {
        return this.messages;
    }

    public void setMessages(MyJsonArray messages) {
        this.messages = messages;
    }

    public MyJsonObject getResult() {
        this.data.Put("messages", this.messages);
        this.dataArray.Put(0, this.data);
        this.result.Put("result", this.dataArray);
        return this.result;
    }
}
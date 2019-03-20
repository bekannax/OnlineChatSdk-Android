package com.sofit.onlinechatsdk;

public interface ChatListener {
    void onEvent(String name, String data);
}
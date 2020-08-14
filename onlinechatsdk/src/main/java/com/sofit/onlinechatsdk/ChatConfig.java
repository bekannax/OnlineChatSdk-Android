package com.sofit.onlinechatsdk;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

class ChatConfig {

    private static ChatConfig instance = null;
    private static final String configKeyApiToken = "apiToken";
    private static final String configKeyClientId = "clientId";
    private static final String configKeyLastDateTimeNewMessage = "lastDateTimeNewMessage";

    private final String configKey = "onlineChatSdkConfig";
    private final Map<String, Object> cache;
    private SharedPreferences config;

    private ChatConfig(Context context) {
        this.cache = new HashMap<>();
        this.config = context.getSharedPreferences(this.configKey, Context.MODE_PRIVATE);
    }

    private static ChatConfig getInstance(Context context) {
        ChatConfig localInstance = instance;
        if (localInstance == null) {
            synchronized (ChatConfig.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new ChatConfig(context);
                }
            }
        }
        return localInstance;
    }

    public static void setLastDateTimeNewMessage(String value, Context context) {
        getInstance(context).setConfig(ChatConfig.configKeyLastDateTimeNewMessage, value);
    }

    public static String getLastDateTimeNewMessage(Context context) {
        return getInstance(context).getConfigString(ChatConfig.configKeyLastDateTimeNewMessage);
    }

    public static void setClientId(String clientId, Context context) {
        getInstance(context).setConfig(ChatConfig.configKeyClientId, clientId);
    }

    public static String getClientId(Context context) {
        return getInstance(context).getConfigString(ChatConfig.configKeyClientId);
    }

    public static void setApiToken(String token, Context context) {
        getInstance(context).setConfig(ChatConfig.configKeyApiToken, token);
    }

    public static String getApiToken(Context context) {
        return getInstance(context).getConfigString(ChatConfig.configKeyApiToken);
    }

    private ChatConfig setConfig(Map<String, String> configs) {
        Iterator<Map.Entry<String, String>> entries = configs.entrySet().iterator();
        SharedPreferences.Editor editor = this.config.edit();
        while (entries.hasNext()) {
            Map.Entry<String, String> entry = entries.next();
            editor.putString(entry.getKey(), entry.getValue());
        }
        editor.apply();
        return this;
    }

    private ChatConfig setConfig(String key, String value) {
        SharedPreferences.Editor editor = this.config.edit();
        editor.putString(key, value);
        editor.apply();
        setCache(key, null);
        return this;
    }

    private ChatConfig setConfig(String key, int value) {
        SharedPreferences.Editor editor = this.config.edit();
        editor.putInt(key, value);
        editor.apply();
        setCache(key, null);
        return this;
    }

    private String getConfigString(String key) {
        String value = getCacheString(key);
        if (!value.isEmpty()) {
            return value;
        }
        value = this.config.getString(key, "");
        if (!value.isEmpty()) {
            setCache(key, value);
        }
        return value;
    }

    private int getConfigInt(String key) {
        int value = getCacheInt(key);
        if (value > 0) {
            return value;
        }
        value = this.config.getInt(key, 0);
        if (value > 0) {
            setCache(key, value);
        }
        return value;
    }

    private void setCache(String key, String value) {
        this.cache.put(key, value);
    }

    private void setCache(String key, int value) {
        this.cache.put(key, value);
    }

    private String getCacheString(String key) {
        Object value = this.cache.get(key);
        if (value == null) {
            return "";
        }
        return (String) value;
    }

    private int getCacheInt(String key) {
        Object value = this.cache.get(key);
        if (value == null) {
            return 0;
        }
        return (int) value;
    }
}
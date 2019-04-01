package com.sofit.onlinechatsdk;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ChatApi {

    private Connection post(String url, JSONObject params) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        Connection connection = new Connection(url, headers);
        if (!connection.IsConnected()) {
            return null;
        }
        connection.Send(params != null ? params.toString() : MyJsonObject.create().toString());
        return connection;
    }

    public JSONObject send(String token, String method, JSONObject params) {
        Connection connection = this.post(String.format("https://admin.verbox.ru/api/chat/%s/%s", token, method), params);
        if (connection == null) {
            return MyJsonObject.create();
        }
        if (connection.jsonResponse != null) {
            return connection.jsonResponse;
        }
        return MyJsonObject.create();
    }

    public JSONObject message(String token, JSONObject params) {
        return send(token, "message", params);
    }

    public static JSONObject getNewMessages(String token, String clientId) {
        SimpleDateFormat dtFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        MyJsonObject dateRange = MyJsonObject.create();
        dateRange.Put("start", dtFormat.format(new Date(System.currentTimeMillis() - 86400000 * 14)));
        dateRange.Put("stop", dtFormat.format(new Date(System.currentTimeMillis())));

        MyJsonObject params = MyJsonObject.create();
        params.Put("client", MyJsonObject.create().Put("clientId", clientId));
        params.Put("sender", "operator");
        params.Put("status", "unreaded");
        params.Put("dateRange", dateRange);

        return new ChatApi().message(token, params);
    }
}

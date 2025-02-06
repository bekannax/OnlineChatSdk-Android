package com.sofit.onlinechatsdk;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ChatApi {

    private Connection post(String url, String token, JSONObject params) {
        Map<String, String> headers = new HashMap<>();
        headers.put("X-Token", token);
        headers.put("Content-Type", "application/json");
        Connection connection = new Connection(url, headers);
        if (!connection.IsConnected()) {
            return null;
        }
        connection.Send(params != null ? params.toString() : MyJsonObject.create().toString());
        return connection;
    }

    public JSONObject send(String token, String method, JSONObject params) {
        Connection connection = this.post(String.format("https://admin.verbox.ru/json/v1.0/%s", method), token, params);
        if (connection == null) {
            return MyJsonObject.create();
        }
        if (connection.jsonResponse != null) {
            return connection.jsonResponse;
        }
        return MyJsonObject.create();
    }

    public JSONObject clientSetInfo(String token, JSONObject params) {
        return send(token, "chat/client/setInfo", params);
    }

    public JSONObject message(String token, JSONObject params) {
        return send(token, "chat/message/getList", params);
    }

//    public static func setInfo(_ token: String, _ params: Dictionary<String, Any>, callback: @escaping (NSDictionary?) -> Void) {
//        (ChatApi()).setInfo(token, params: params, callback: callback)
//    }

    public static JSONObject setInfo(String token, JSONObject params) {
        return (new ChatApi()).clientSetInfo(token, params);
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

        MyJsonObject result = (MyJsonObject) new ChatApi().message(token, params);
        MyJsonArray dataArray = result.GetJsonArray("data");
        MyJsonObject data = dataArray.GetJsonObject(0);
        MyJsonArray messages = data.GetJsonArray("messages");
        if (messages.length() == 0) {
            return result;
        }
        MyJsonArray realMessages = MyJsonArray.create();
        for (int i = 0; i < messages.length(); i++) {
            MyJsonObject message = messages.GetJsonObject(i);
            if (message.GetBoolean("isVisibleForClient", true)) {
                realMessages.Put(message);
            }
        }
        if (realMessages.length() == 0) {
            return MyJsonObject.create("{\"ok\":true,\"data\":[]}");
        }
        data.Put("messages", realMessages);
        dataArray.Put(0, data);
        result.Put("data", dataArray);
        return result;
    }
}

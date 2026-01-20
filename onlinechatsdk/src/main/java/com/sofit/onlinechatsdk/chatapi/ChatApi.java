package com.sofit.onlinechatsdk.chatapi;

import android.content.Context;

import com.sofit.onlinechatsdk.ChatConfig;
import com.sofit.onlinechatsdk.CheckConnection;
import com.sofit.onlinechatsdk.chatapi.models.ChatApiGetClientUnreadMessageCountResponseModel;
import com.sofit.onlinechatsdk.chatapi.models.ChatApiResponseErrorModel;
import com.sofit.onlinechatsdk.chatapi.models.ChatApiResponseModel;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ChatApi {

    private final String orgId;
    private final String siteId;
    private final String clientId;
    private final CheckConnection checkConnection = new CheckConnection();
    private final String apiUrl = "https://%s/comet/%s?orgId=%s&siteId=%s&clientId=%s";

    public ChatApi(Context context) {
        this.orgId = ChatConfig.getOrgId(context);
        this.siteId = ChatConfig.getSiteId(context);
        this.clientId = ChatConfig.getClientId(context);
    }

    private ChatApiResponseModel requestGet(String sUrl) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        ChatApiResponseModel result;
        try {
            URL url = new URL(sUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = connection.getInputStream();
                if (inputStream == null) {
                    result = new ChatApiResponseModel(
                        new ChatApiResponseErrorModel(
                        0,"empty response :: inputStream == null"
                        )
                    );
                } else {
                    StringBuilder buffer = new StringBuilder();
                    reader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line).append("\n");
                    }
                    result = new ChatApiResponseModel(
                        buffer.toString().trim()
                    );
                }
            } else {
                result = new ChatApiResponseModel(
                    new ChatApiResponseErrorModel(
                        responseCode,"HTTP error code: " + responseCode
                    )
                );
            }
        } catch (Exception e) {
            result = new ChatApiResponseModel(
                new ChatApiResponseErrorModel(
                    0,"URL failed: " + e.getMessage()
                )
            );
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {/**/}
            }
        }
        return result;
    }

    private ChatApiResponseErrorModel checkAuthParams() {
        String error = "";
        if (orgId.isEmpty()) {
            error = "orgId.isEmpty() ";
        }
        if (siteId.isEmpty()) {
            error += "siteId.isEmpty() ";
        }
        if (clientId.isEmpty()) {
            error += "clientId.isEmpty()";
        }
        if (error.isEmpty()) {
            return null;
        }
        return new ChatApiResponseErrorModel(0, "auth params not init: " + error);
    }

    public ChatApiGetClientUnreadMessageCountResponseModel getClientUnreadMessageCount() {
        ChatApiResponseErrorModel checkAuthParamsError = checkAuthParams();
        if (checkAuthParamsError != null) {
            return new ChatApiGetClientUnreadMessageCountResponseModel(
                checkAuthParamsError
            );
        }
//        https://widget.apibcknd.com/comet/getClientUnreadMessageCount?orgId=&siteId=&clientId=
        String url = String.format(
            apiUrl,
            checkConnection.getApiDomain(),
            "getClientUnreadMessageCount",
            orgId,
            siteId,
            clientId
        );
        ChatApiResponseModel response = requestGet(url);
        if (response.error != null) {
            return new ChatApiGetClientUnreadMessageCountResponseModel(
                response.error
            );
        }
        if (response.result.isEmpty()) {
            return new ChatApiGetClientUnreadMessageCountResponseModel(0);
        }
        try {
            return new ChatApiGetClientUnreadMessageCountResponseModel(
                Integer.parseInt(response.result)
            );
        } catch (Exception e) {
            return new ChatApiGetClientUnreadMessageCountResponseModel(
                new ChatApiResponseErrorModel(
                    0,"failed to parse result: " + e.getMessage()
                )
            );
        }
    }
}

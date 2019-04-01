package com.sofit.onlinechatsdk;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

class Connection {

    private final String CHARSET = "UTF-8";

    private boolean http;
    private String url;
    private HttpURLConnection connection;
    private BufferedWriter writer;
    private boolean connected;

    JSONObject jsonResponse;

    Connection(String url, Map<String, String> headers) {
        this.http = false;
        this.url = url;
        this.connected = connect(headers);
    }

    private HttpURLConnection connection(Map<String, String> headers) {
        HttpURLConnection connection;
        try {
            if (!this.http) {
                connection = (HttpsURLConnection) new URL(this.url).openConnection();
            } else {
                connection = (HttpURLConnection) new URL(this.url).openConnection();
            }
            if (headers != null && headers.size() > 0) {
                for(Map.Entry<String, String> entry : headers.entrySet()) {
                    connection.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }
            connection.setConnectTimeout(30000);
            connection.setReadTimeout(30000);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            return connection;
        } catch (Exception e) {
            return null;
        }
    }

    private BufferedWriter writer(HttpURLConnection connection) {
        if (connection == null) {
            return null;
        }
        try {
            return new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), CHARSET));
        } catch (Exception e) {
            return null;
        }
    }

    private boolean connect(Map<String, String> headers) {
        this.connection = connection(headers);
        this.writer = writer(this.connection);
        return this.writer != null;
    }

    private boolean write(String s) {
        try {
            this.writer.write(s);
            this.writer.flush();
            this.writer.close();
            return true;
        }catch (Exception e){
            this.connection.disconnect();
            return false;
        }
    }

    private String read() {
        StringBuilder sb = new StringBuilder();
        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(this.connection.getInputStream(), CHARSET));
            int c;
            while ((c = reader.read()) != -1) {
                sb.append((char) c);
            }
            reader.close();
        }catch (Exception e){
            if (sb.length() > 0) {
                sb.setLength(0);
            }
            return null;
        }
        if(sb.length() == 0)
            return "";
        String res = sb.toString().trim();
        sb.setLength(0);
        return res;
    }

    void Send(String query) {
        if (!this.connected) {
            return;
        }
        if (!write(query)) {
            return;
        }
        String response = read();
        int code;
        try {
            code = this.connection.getResponseCode();
        } catch (Exception  e) {
            code = -1;
        }
        this.connection.disconnect();
        this.connection = null;
        if (code == 200 && response != null && !response.isEmpty()) {
            this.jsonResponse = MyJsonObject.create(response);
        }
    }

    boolean IsConnected() {
        return connected;
    }
}
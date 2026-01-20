package com.sofit.onlinechatsdk.chatapi;

import org.json.JSONArray;
import org.json.JSONException;

public class MyJsonArray extends JSONArray {

    private MyJsonArray() {
        super();
    }

    private MyJsonArray(String p_json) throws JSONException {
        super(p_json);
    }

    String GetString(int index) {
        try {
            return getString(index);
        } catch (Exception e) {/**/}
        return "";
    }

    public MyJsonObject GetJsonObject(int index) {
        return MyJsonObject.create(GetString(index));
    }

    public MyJsonArray Put(MyJsonObject object) {
        this.put(object);
        return this;
    }

    public MyJsonArray Put(int index, MyJsonObject object) {
        try {
            this.put(index, object);
        } catch (Exception e) {/**/}
        return this;
    }

    public static MyJsonArray create(){
        try {
            return new MyJsonArray();
        } catch (Exception e) {
            return create();
        }
    }

    public static MyJsonArray create(String json) {
        if (json == null || json.isEmpty())
            return MyJsonArray.create();
        try {
            return new MyJsonArray(json);
        } catch (Exception e){/**/}
        return MyJsonArray.create();
    }
}

package com.sofit.onlinechatsdk;

import org.json.JSONException;
import org.json.JSONObject;

public class MyJsonObject extends JSONObject {

    private MyJsonObject() {
        super();
    }

    private MyJsonObject(String p_json) throws JSONException {
        super(p_json);
    }

    MyJsonObject Put(String key, Object obj){
        try{
            put(key, obj);
            return this;
        }catch (Exception e){/**/}
        return this;
    }

    int GetInteger(String key) {
        try {
            return getInt(key);
        } catch (Exception e) {/**/}
        return 0;
    }

    public String GetString(String key) {
        try {
            return getString(key);
        } catch (Exception e) {/**/}
        return "";
    }

    boolean GetBoolean(String key, boolean defValue) {
        try {
            return getBoolean(key);
        } catch (Exception e) {/**/}
        return defValue;
    }

    MyJsonArray GetJsonArray(String key) {
        return MyJsonArray.create(GetString(key));
    }

    MyJsonObject GetJsonObject(String key) {
        return MyJsonObject.create(GetString(key));
    }

    static MyJsonObject create(){
        try {
            return new MyJsonObject();
        } catch (Exception e) {
            return create();
        }
    }

    public static MyJsonObject create(String json) {
        if (json == null || json.isEmpty())
            return MyJsonObject.create();
        try {
            return new MyJsonObject(json);
        } catch (Exception e){/**/}
        return MyJsonObject.create();
    }
}

package com.sofit.onlinechatsdk;

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

    MyJsonObject GetJsonObject(int index) {
        return MyJsonObject.create(GetString(index));
    }

    MyJsonArray Put(MyJsonObject object) {
        this.put(object);
        return this;
    }

    MyJsonArray Put(int index, MyJsonObject object) {
        try {
            this.put(index, object);
        } catch (Exception e) {/**/}
        return this;
    }

    static MyJsonArray create(){
        try {
            return new MyJsonArray();
        } catch (Exception e) {
            return create();
        }
    }

    static MyJsonArray create(String json) {
        if (json == null || json.isEmpty())
            return MyJsonArray.create();
        try {
            return new MyJsonArray(json);
        } catch (Exception e){/**/}
        return MyJsonArray.create();
    }
}

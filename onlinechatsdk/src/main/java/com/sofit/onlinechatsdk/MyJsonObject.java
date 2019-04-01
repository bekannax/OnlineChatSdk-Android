package com.sofit.onlinechatsdk;

import org.json.JSONException;
import org.json.JSONObject;

class MyJsonObject extends JSONObject {

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

    static MyJsonObject create(){
        try {
            return new MyJsonObject();
        } catch (Exception e) {
            return create();
        }
    }

    static MyJsonObject create(String json) {
        if (json == null || json.isEmpty())
            return MyJsonObject.create();
        try {
            return new MyJsonObject(json);
        } catch (Exception e){/**/}
        return MyJsonObject.create();
    }
}

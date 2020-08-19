package com.sofit.onlinechatsdk;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

class ChatSimpleDateFormat extends SimpleDateFormat {

    ChatSimpleDateFormat() {
        super("yyyy-MM-dd HH:mm:ss");
        this.setTimeZone( TimeZone.getTimeZone("GMT+03:00") );
    }

    public String getCurrent() {
        return this.format( new Date(System.currentTimeMillis()) );
    }
}

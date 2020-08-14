package com.sofit.onlinechatsdk;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

class ChatSimpleDateFormat extends SimpleDateFormat {

    ChatSimpleDateFormat(String pattern) {
        super(pattern);
        this.setTimeZone( TimeZone.getTimeZone("GMT+03:00") );
    }
}

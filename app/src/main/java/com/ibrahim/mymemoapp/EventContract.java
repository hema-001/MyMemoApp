package com.ibrahim.mymemoapp;

import android.provider.BaseColumns;

public class EventContract {
    private EventContract(){};

    public static class EventEntry implements BaseColumns{
        public static final String TABLE_NAME = "events";
        public static final String COL_NAME_TITLE = "title";
        public static final String COL_NAME_DATE = "date";
        public static final String COL_NAME_TIME = "time";
        public static final String COL_NAME_PLACE = "place";
        public static final String COL_NAME_PRIORITY = "priority";

    }
}

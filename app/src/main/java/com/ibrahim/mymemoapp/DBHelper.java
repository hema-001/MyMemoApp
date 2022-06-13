package com.ibrahim.mymemoapp;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Event.db";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + EventContract.EventEntry.TABLE_NAME + " (" +
                    EventContract.EventEntry._ID + " INTEGER PRIMARY KEY," +
                    EventContract.EventEntry.COL_NAME_TITLE + " TEXT," +
                    EventContract.EventEntry.COL_NAME_DATE + " TEXT," +
                    EventContract.EventEntry.COL_NAME_TIME + " TEXT," +
                    EventContract.EventEntry.COL_NAME_PLACE + " TEXT," +
                    EventContract.EventEntry.COL_NAME_PRIORITY + " INTEGER ) ";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + EventContract.EventEntry.TABLE_NAME;

    public DBHelper (Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(SQL_DELETE_ENTRIES);
        onCreate(sqLiteDatabase);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
    public ArrayList<EventDAO> listEvents() {
        String sql = "select * from " + EventContract.EventEntry.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<EventDAO> storeContacts = new ArrayList<>();
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            do {
                int id = Integer.parseInt(cursor.getString(0));
                String title = cursor.getString(1);
                String date = cursor.getString(2);
                String time = cursor.getString(3);
                String place = cursor.getString(4);
                int priority = cursor.getInt(5);
                storeContacts.add(new EventDAO(id, title, date, time, place, priority));
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        return storeContacts;
    }
}

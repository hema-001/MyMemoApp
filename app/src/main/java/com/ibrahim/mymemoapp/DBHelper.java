package com.ibrahim.mymemoapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

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
        ArrayList<EventDAO> eventsList = new ArrayList<>();
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            do {
                int id = Integer.parseInt(cursor.getString(0));
                String title = cursor.getString(1);
                String date = cursor.getString(2);
                String time = cursor.getString(3);
                String place = cursor.getString(4);
                int priority = cursor.getInt(5);
                eventsList.add(new EventDAO(id, title, date, time, place, priority));
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        return eventsList;
    }

    public int addEvent(EventDAO event, Context context){
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(EventContract.EventEntry.COL_NAME_TITLE, event.getTitle());
        values.put(EventContract.EventEntry.COL_NAME_DATE, event.getDate());
        values.put(EventContract.EventEntry.COL_NAME_TIME, event.getTime());
        values.put(EventContract.EventEntry.COL_NAME_PLACE, event.getPlace());
        values.put(EventContract.EventEntry.COL_NAME_PRIORITY, event.getPriority());

        long newRowId = db.insert(EventContract.EventEntry.TABLE_NAME, null, values);
        return (int) newRowId;
    }

    public int updateEvent(EventDAO event, Context context){
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // New values for columns
        ContentValues values = new ContentValues();
        values.put(EventContract.EventEntry.COL_NAME_TITLE, event.getTitle());
        values.put(EventContract.EventEntry.COL_NAME_DATE, event.getDate());
        values.put(EventContract.EventEntry.COL_NAME_TIME, event.getTime());
        values.put(EventContract.EventEntry.COL_NAME_PLACE, event.getPlace());
        values.put(EventContract.EventEntry.COL_NAME_PRIORITY, event.getPriority());

        // Which row to update, based on the ID column
        String selection = EventContract.EventEntry._ID + " = ?";

        // value to replace the "?" in the selection statement
        String[] selectionArgs = { String.valueOf(event.getId())};

        int count = db.update(
                EventContract.EventEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);
        return count;
    }
    public int deleteEvent(String id, Context context){
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Define 'where' part of query.
        String selection = EventContract.EventEntry._ID + " = ?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = { id };
        // Issue SQL statement.
        int deletedRows = db.delete(EventContract.EventEntry.TABLE_NAME, selection, selectionArgs);

        return deletedRows;
    };
}

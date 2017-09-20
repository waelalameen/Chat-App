package com.alameen.wael.hp.chatapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "num.db";
    private static final String TABLE_NAME = "num_table";
    private static final String id = "1";

    Database(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String query = "CREATE TABLE "+TABLE_NAME+" (id INTEGER PRIMARY KEY AUTOINCREMENT, message TEXT, time TEXT, idd TEXT)";
        sqLiteDatabase.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public boolean insert(String message, String time, String id) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("message", message);
        contentValues.put("time", time);
        contentValues.put("idd", id);
        long res = sqLiteDatabase.insert(TABLE_NAME, null, contentValues);
        return res != -1;
    }

    public Cursor show() {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String query = "SELECT message, time, idd FROM "+TABLE_NAME;
        return sqLiteDatabase.rawQuery(query, null);
    }
}

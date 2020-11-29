package com.sun.sunaccount;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by sunronggui on 2017/9/4.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final int VERSION=1;
    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }
    public DatabaseHelper(Context context, String name)
    {
        this(context,name,VERSION);
    }
    public DatabaseHelper(Context context, String name, int version)
    {
        this(context,name,null,version);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table myaccount(class VARCHAR,account VARCHAR,password VARCHAR)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        System.out.println("update a database");
    }
}

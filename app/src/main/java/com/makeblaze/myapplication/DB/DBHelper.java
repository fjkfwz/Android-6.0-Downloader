package com.makeblaze.myapplication.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by jz on 2015/10/30.
 * DATABASE Helper
 */
public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "download.db";
    private static DBHelper dbHelper;
    private static int VERSION = 1;
    private static final String SQL_CREATE = "create table thread_info(_id integer primary key autoincrement, " +
            "thread_id integer,url text,start integer,end integer, finished integer)";
    private static final String SQL_DROP = "drop table if exits thread_info";

    public static DBHelper getInstance(Context context) {
        if (dbHelper == null) {
            dbHelper = new DBHelper(context);
        }
        return dbHelper;
    }

    private DBHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DROP);
        db.execSQL(SQL_CREATE);
    }
}

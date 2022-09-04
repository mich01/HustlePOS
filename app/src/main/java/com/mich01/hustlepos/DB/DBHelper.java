package com.mich01.hustlepos.DB;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper
{
    Context context;
    static final String DATABASE_NAME = "mystore.db";
    private static final int DATABASE_VERSION = 1;

    private DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context=context;
    }
    @SuppressLint("StaticFieldLeak")
    private static DBHelper instance = null;
    public static DBHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DBHelper(context);
        }
        return instance;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create Table Items(ID INTEGER primary key AUTOINCREMENT NOT NULL, ItemName TEXT, PurchasePrice TEXT, SellingPrice TEXT, Quantity INTEGER,img BLOB, Timestamp DATETIME DEFAULT CURRENT_TIMESTAMP)");
        db.execSQL("create Table Sales(ID INTEGER primary key AUTOINCREMENT NOT NULL, ItemID INTEGER,TransactionID TEXT, Price INTEGER, Quantity INTEGER, Timestamp DATETIME DEFAULT CURRENT_TIMESTAMP)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists Items");
        db.execSQL("drop table if exists Items");
        onCreate(db);
    }
}
package com.example.clientorder;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class QuestionsDatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "User Info";
    private static final int DB_VERSION = 1;



    public QuestionsDatabaseHelper(Context context)
    {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        updateMyDatabase(db, 0, DB_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        updateMyDatabase(db,oldVersion, newVersion);
    }

    private void updateMyDatabase(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        if (oldVersion < 2) {
            db.execSQL("CREATE TABLE BASKET (_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "NAME TEXT,"
                    + "PRICE DOUBLE);");

            db.execSQL("CREATE TABLE HISTORY (_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "NAME TEXT,"
                    + "PRICE DOUBLE);");

            db.execSQL("CREATE TABLE ADDRESS (_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "PHONE TEXT,"
                    + "ADRESS1 TEXT,"
                    + "ADRESS2 TEXT,"
                    + "ADRESS3 TEXT);");


            //Created By Szymon Moń



        }
    }
}



package com.adi.ho.jackie.bubblestocks.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by JHADI on 3/22/16.
 */
public class MyStockDatabase  extends SQLiteOpenHelper{

    public static final String DATABASE_NAME = "MYSTOCKDB.db";
    public static final String STOCKTABLE_NAME = "EXAMPLESTOCKTABLE";
    public static final int DATABASE_VERSION = 1;
    public static final String DAY_HIGH = "DAYHIGH";
    public static final String DAY_LOW = "DAYLOW";
    public static final String DAY_OPEN = "DAYOPEN";
    public static final String DAY_CLOSE = "DAYCLOSE";
    public static final String DATE = "DATE";
    public static final String[] STOCK_COLUMNS = {DATE,DAY_OPEN,DAY_CLOSE,DAY_HIGH,DAY_LOW};


    public MyStockDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //db.execSQL();

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

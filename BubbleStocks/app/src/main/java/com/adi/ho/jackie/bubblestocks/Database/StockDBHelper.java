package com.adi.ho.jackie.bubblestocks.Database;

/**
 * Created by JHADI on 3/22/16.
 */

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class StockDBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "stockDB.db";
    public static final String TABLE_STOCKS = "STOCKS";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_STOCK_SYMBOL = "stock_symbol";
    public static final String COLUMN_STOCK_NAME = "stock_name";
    public static final String COLUMN_STOCK_PRICE = "price";

    public static final String[] ALL_COLUMNS = new String[]{COLUMN_ID, COLUMN_STOCK_NAME, COLUMN_STOCK_SYMBOL, COLUMN_STOCK_PRICE};

    public StockDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_PRODUCTS_TABLE = "CREATE TABLE " +
                TABLE_STOCKS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY, "
                + COLUMN_STOCK_SYMBOL + " TEXT, "
                + COLUMN_STOCK_NAME + " TEXT,"
                + COLUMN_STOCK_PRICE + " REAL)";
        db.execSQL(CREATE_PRODUCTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STOCKS);
        onCreate(db);
    }

    public long addStock(ContentValues values) {

        SQLiteDatabase db = getWritableDatabase();
        long rowId = db.insert(TABLE_STOCKS, null, values);
        db.close();

        return rowId;
    }

    public int updateStockById(String id, ContentValues values) {
        SQLiteDatabase db = getWritableDatabase();

        int numRowsChanged = db.update(TABLE_STOCKS, values, COLUMN_ID + " = ?", new String[]{id});
        db.close();

        return numRowsChanged;
    }

    public Cursor getAllStocks() {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(TABLE_STOCKS, ALL_COLUMNS, null, null, null, null, null);

        return cursor;
    }

    public Cursor getStockById(String id) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(TABLE_STOCKS, ALL_COLUMNS, COLUMN_ID + " = ?", new String[]{id}, null, null, null);

        return cursor;
    }

}

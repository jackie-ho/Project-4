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
    public static final String COLUMN_STOCK_TRACKED = "tracked";
    public static final String COLUMN_STOCK_OPENPRICE = "openprice";


    public static final String[] ALL_COLUMNS = new String[]{COLUMN_ID, COLUMN_STOCK_SYMBOL, COLUMN_STOCK_PRICE, COLUMN_STOCK_TRACKED};
    public StockDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_PRODUCTS_TABLE = "CREATE TABLE " +
                TABLE_STOCKS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_STOCK_SYMBOL + " TEXT, "
                + COLUMN_STOCK_PRICE + " TEXT, "
                + COLUMN_STOCK_OPENPRICE + " TEXT, "
                + COLUMN_STOCK_TRACKED + " INTEGER)";
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

        //Id is equivalent to time of trade
        int numRowsChanged = db.update(TABLE_STOCKS, values, COLUMN_ID + " = ? ", new String[]{id});
        db.close();

        return numRowsChanged;
    }

    public Cursor getAllStocks() {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(TABLE_STOCKS, ALL_COLUMNS, null, null, null, null, null);

        return cursor;
    }

    public Cursor getStockPriceChange(String id) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(TABLE_STOCKS, ALL_COLUMNS, COLUMN_STOCK_PRICE +" = ? ", new String[]{id}, null, null, null);

        return cursor;
    }

    public void addStockToTracked(String stockSymbol){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_STOCK_TRACKED, 1);
        db.update(TABLE_STOCKS, values, COLUMN_STOCK_SYMBOL + " = ? ", new String[]{stockSymbol});
    }

    public Cursor getTrackedStocks(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_STOCKS, ALL_COLUMNS, COLUMN_STOCK_TRACKED + " = ? ", new String[]{"1"}, null,null,null);
        return cursor;
    }

}

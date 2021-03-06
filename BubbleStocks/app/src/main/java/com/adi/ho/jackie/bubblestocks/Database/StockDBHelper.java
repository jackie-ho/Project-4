package com.adi.ho.jackie.bubblestocks.database;

/**
 * Created by JHADI on 3/22/16.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

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
    public static final String COLUMN_VOLUME = "volume";


    public static final String[] ALL_COLUMNS = new String[]{COLUMN_ID, COLUMN_STOCK_SYMBOL, COLUMN_STOCK_PRICE, COLUMN_STOCK_OPENPRICE, COLUMN_VOLUME, COLUMN_STOCK_TRACKED};
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
                + COLUMN_VOLUME + " TEXT, "
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
        Log.v("DATABASE", "Inserted stock into rowId: " + rowId);

        return rowId;
    }

    public int updateStockById(String uriLastPathSegment, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = getWritableDatabase();

        //Id is equivalent to time of trade
        int numRowsChanged = db.update(TABLE_STOCKS, values, selection, selectionArgs);

        return numRowsChanged;
    }

    public Cursor getAllStocks() {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(TABLE_STOCKS, ALL_COLUMNS, null, null, null, null, null);
        return cursor;
    }

    public Cursor getStockPriceChange(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(TABLE_STOCKS, projection, selection
                , selectionArgs , null, null, null);
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

    public Cursor queryStocks(String selections, String[] selectionArgs){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_STOCKS, ALL_COLUMNS, selections, selectionArgs, null, null, null);
        return cursor;
    }

    public void deleteStock(String selection, String[] selectionArgs){
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_STOCKS,selection,selectionArgs);

    }

}

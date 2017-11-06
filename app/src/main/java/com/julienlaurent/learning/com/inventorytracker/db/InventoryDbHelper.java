package com.julienlaurent.learning.com.inventorytracker.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by djoum on 10/29/17.
 */

public class InventoryDbHelper extends SQLiteOpenHelper {
    public static String DATABASE_NAME = "inventory";
    public static int DATABASE_VERSION = 1;

    public InventoryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(InventoryContract.SQL_CREATE_PRODUCT_ENTRIES_TABLE);
        db.execSQL(InventoryContract.SQL_CREATE_SUPPLIER_ENTRIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            db.execSQL(InventoryContract.SQL_DELETE_PRODUCT_ENTRY_TABLE);
            db.execSQL(InventoryContract.SQL_CREATE_SUPPLIER_ENTRIES_TABLE);
            onCreate(db);
        }
    }
}

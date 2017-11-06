package com.julienlaurent.learning.com.inventorytracker.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by djoum on 10/29/17.
 */

public class InventoryProvider extends ContentProvider {
    /**
     * helper constant to create the uriMatcher
     */
    public static final int PRODUCT_LIST = 100;
    public static final int PRODUCT_ID = 101;
    public static final int PRODUCT_NAME = 102;
    public static final int SUPPLIER_LIST = 200;
    public static final int SUPPLIER_ID = 201;

    private InventoryDbHelper mDbHelper;
    private SQLiteDatabase mDatabase;
    private String LOG_TAG = InventoryProvider.class.getSimpleName();

    public static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(InventoryContract.AUTHORITY, InventoryContract.PATH_PRODUCTS, PRODUCT_LIST);
        matcher.addURI(InventoryContract.AUTHORITY, InventoryContract.PATH_PRODUCTS + "/#", PRODUCT_ID);
        matcher.addURI(InventoryContract.AUTHORITY, InventoryContract.PATH_PRODUCTS + "/*", PRODUCT_NAME);
        matcher.addURI(InventoryContract.AUTHORITY, InventoryContract.PATH_SUPPLIERS, SUPPLIER_LIST);
        matcher.addURI(InventoryContract.AUTHORITY, InventoryContract.PATH_SUPPLIERS + "/#", SUPPLIER_ID);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new InventoryDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection,
                        @Nullable String selection, @Nullable String[] selectionArgs,
                        @Nullable String sortOrder) {
        mDatabase = mDbHelper.getReadableDatabase();
        Cursor queryResult = null;
        switch (buildUriMatcher().match(uri)) {
            case PRODUCT_LIST:
                queryResult = mDatabase.query(InventoryContract.ProductEntry.TABLE_NAME,
                    projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case PRODUCT_ID:
                selection = InventoryContract.ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                queryResult = mDatabase.query(InventoryContract.ProductEntry.TABLE_NAME,
                    projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case PRODUCT_NAME:
                selection = InventoryContract.ProductEntry._ID + "=?";
                queryResult = mDatabase.query(InventoryContract.ProductEntry.TABLE_NAME,
                    projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case SUPPLIER_LIST:
                queryResult = mDatabase.query(InventoryContract.SupplierEntry.TABLE_NAME,
                    projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case SUPPLIER_ID:
                selection = InventoryContract.SupplierEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                queryResult = mDatabase.query(InventoryContract.SupplierEntry.TABLE_NAME,
                    projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("cannot query uri" + uri);
        }
        queryResult.setNotificationUri(getContext().getContentResolver(), uri);
        return queryResult;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (buildUriMatcher().match(uri)) {
            case PRODUCT_LIST:
                return InventoryContract.ProductEntry.CONTENT_LIST_TYPE_PRODUCTS;
            case PRODUCT_ID:
                return InventoryContract.ProductEntry.CONTENT_ITEM_TYPE_PRODUCTS;
            case SUPPLIER_LIST:
                return InventoryContract.SupplierEntry.CONTENT_LIST_TYPE_SUPPLIERS;
            case SUPPLIER_ID:
                return InventoryContract.SupplierEntry.CONTENT_ITEM_TYPE_SUPPLIER;
            default:
                throw new IllegalStateException("Unknown Uri" + uri + " which matches " + buildUriMatcher());
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        Uri uriReturn;
        switch (buildUriMatcher().match(uri)) {
            case PRODUCT_LIST:
                uriReturn = insertProduct(uri, values);
                break;
            case SUPPLIER_LIST:
                uriReturn = insertSupplier(uri, values);
                break;
            default:
                throw new IllegalStateException("Unknown Uri" + uri + " which matches " + buildUriMatcher());
        }
        return uriReturn;
    }

    private Uri insertProduct(Uri uri, ContentValues values) {
        mDatabase = mDbHelper.getWritableDatabase();
        final long insert = mDatabase.insert(InventoryContract.ProductEntry.TABLE_NAME, null, values);
        if (insert == -1) {
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, insert);
    }

    private Uri insertSupplier(Uri uri, ContentValues values) {
        mDatabase = mDbHelper.getWritableDatabase();
        final long insert = mDatabase.insert(InventoryContract.SupplierEntry.TABLE_NAME, null, values);
        if (insert == -1) {
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, insert);
    }

    @Override
    public int delete(@NonNull Uri uri,
                      @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        int rowDeleted = 0;
        switch (buildUriMatcher().match(uri)) {
            case PRODUCT_LIST:
                deleteRecord(uri, selection, selectionArgs);
                break;
            case PRODUCT_ID:
                selection = InventoryContract.ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowDeleted = deleteRecord(uri, selection, selectionArgs);
                break;
            case SUPPLIER_LIST:
                rowDeleted = deleteAllSupplier(uri);
                break;
            case SUPPLIER_ID:
                selection = InventoryContract.SupplierEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                break;
            default:
                throw new IllegalStateException("Unknown Uri" + uri + " which matches " + buildUriMatcher());
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowDeleted;
    }

    private int deleteRecord(Uri uri, String selection, String[] selectionArgs) {
        mDatabase = mDbHelper.getWritableDatabase();
        return mDatabase.delete(InventoryContract.ProductEntry.TABLE_NAME, selection, selectionArgs);
    }

    private int deleteAllSupplier(Uri uri) {
        mDatabase = mDbHelper.getWritableDatabase();
        return mDatabase.delete(InventoryContract.SupplierEntry.TABLE_NAME, null, null);
    }

    @Override
    public int update(@NonNull Uri uri,
                      @Nullable ContentValues values,
                      @Nullable String selection, @Nullable String[] selectionArgs) {
        int rowUpdated = 0;
        switch (buildUriMatcher().match(uri)) {
            case PRODUCT_LIST:
                break;
            case PRODUCT_ID:
                selection = InventoryContract.ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowUpdated = updateProduct(uri, values, selection, selectionArgs);
                break;
            case PRODUCT_NAME:
                selection = InventoryContract.ProductEntry.COLUMN_PRODUCT_NAME + "=?";
                rowUpdated = updateProductByName(uri, values, selection, selectionArgs);
                break;
            case SUPPLIER_LIST:
                break;
            case SUPPLIER_ID:
                break;
            default:
                throw new IllegalStateException("Unknown Uri" + uri + " which matches " + buildUriMatcher());
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowUpdated;
    }

    private int updateProduct(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int update = 0;
        if (values.containsKey(InventoryContract.ProductEntry.COLUMN_PRODUCT_QUANTITY)) {
            int quantity = values.getAsInteger(InventoryContract.ProductEntry.COLUMN_PRODUCT_QUANTITY);
            mDatabase = mDbHelper.getWritableDatabase();
            update = mDatabase.update(InventoryContract.ProductEntry.TABLE_NAME, values, selection, selectionArgs);
        }
        return update;
    }

    private int updateProductByName(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int update = 0;
        if (values.containsKey(InventoryContract.ProductEntry.COLUMN_PRODUCT_NAME) &&
            values.containsKey(InventoryContract.ProductEntry.COLUMN_PRODUCT_QUANTITY) &&
            values.containsKey(InventoryContract.ProductEntry.COLUMN_PRODUCT_PRICE)) {
            mDatabase = mDbHelper.getWritableDatabase();
            update = mDatabase.update(InventoryContract.ProductEntry.TABLE_NAME, values, selection, selectionArgs);
        }
        return update;
    }


}

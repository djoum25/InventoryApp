package com.julienlaurent.learning.com.inventorytracker.db;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by djoum on 10/29/17.
 */

public class InventoryContract {

    /**
     * PATH for the supplier table
     */
    public static final String PATH_SUPPLIERS = "suppliers";

    /**
     * PATH for the products table
     */
    public static final String PATH_PRODUCTS = "products";


    public static final String AUTHORITY =
        "com.julienlaurent.learning.com.inventorytracker";
    public static final Uri BASE_CONTENT_URI =
        Uri.parse("content://" + AUTHORITY);

    //Constant to create the supplier table
    public static final String SQL_CREATE_SUPPLIER_ENTRIES_TABLE =
        "CREATE TABLE " + SupplierEntry.TABLE_NAME + " ("
            + SupplierEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
            + SupplierEntry.COLUMN_SUPPLIER_NAME + " TEXT NOT NULL, "
            + SupplierEntry.COLUMN_SUPPLIER_PHONE_NUMBER + " TEXT, "
            + SupplierEntry.COLUMN_SUPPLIER_FAX_NUMBER + " TEXT, "
            + SupplierEntry.COLUMN_SUPPLIER_EMAIL + " TEXT, "
            + SupplierEntry.COLUMN_SUPPLIER_FULL_ADDRESS + " TEXT);";

    public static final String SQL_CREATE_PRODUCT_ENTRIES_TABLE =
        "CREATE TABLE " + ProductEntry.TABLE_NAME + " ("
            + ProductEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
            + ProductEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, "
            + ProductEntry.COLUMN_PRODUCT_QUANTITY + " INTEGER NOT NULL, "
            + ProductEntry.COLUMN_PRODUCT_PRICE + " REAL, "
            + ProductEntry.COLUMN_PRODUCT_SUPPLIER_ID + " INTEGER, "
            + ProductEntry.COLUMN_PRODUCT_IMAGE + " TEXT);";

    public static final String SQL_DELETE_SUPPLIER_ENTRY_TABLE =
        "DROP TABLE IF EXISTS " + SupplierEntry.TABLE_NAME;

    public static final String SQL_DELETE_PRODUCT_ENTRY_TABLE =
        "DROP TABLE IF EXISTS " + ProductEntry.TABLE_NAME;

    private InventoryContract() {
    }

    //this class contains info for supplier table
    public static class SupplierEntry implements BaseColumns {

        /**
         * The MIME type of the content uri for a list of suppliers
         */
        public static final String CONTENT_LIST_TYPE_SUPPLIERS
            = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + AUTHORITY + "/" + PATH_SUPPLIERS;

        /**
         * The MIME type of the content uri for a list of Supplier
         */

        public static final String CONTENT_ITEM_TYPE_SUPPLIER
            = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + AUTHORITY + "/" + PATH_SUPPLIERS;

        /**
         * CONTENT URI FOR THE SUPPLIER TABLE
         */
        public static final Uri SUPPLIER_CONTENT_URI
            = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_SUPPLIERS);

        /**
         * constant items to create
         * suppliers tables
         */
        public static final String TABLE_NAME = "suppliers";
        public static final String COLUMN_SUPPLIER_NAME = "name";
        public static final String COLUMN_SUPPLIER_PHONE_NUMBER = "phone";
        public static final String COLUMN_SUPPLIER_FAX_NUMBER = "fax";
        public static final String COLUMN_SUPPLIER_EMAIL = "email";
        public static final String COLUMN_SUPPLIER_FULL_ADDRESS = "address";
    }

    /**
     * this class contains info for products table
     */
    public static class ProductEntry implements BaseColumns {

        /**
         * The MIME type of the content uri for a list of products
         */
        public static final String CONTENT_LIST_TYPE_PRODUCTS =
            ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + AUTHORITY + "/" + PATH_PRODUCTS;

        /**
         * The MIME type of the content uri for a single products
         */
        public static final String CONTENT_ITEM_TYPE_PRODUCTS
            = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + AUTHORITY + "/" + PATH_PRODUCTS;

        /**
         * CONTENT URI FOR the product table
         */
        public static final Uri PRODUCT_CONTENT_URI
            = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PRODUCTS);

        /**
         * constants items to cream the product table
         */
        public static final String TABLE_NAME = "products";
        public static final String COLUMN_PRODUCT_NAME = "name";
        public static final String COLUMN_PRODUCT_QUANTITY = "quantity";
        public static final String COLUMN_PRODUCT_PRICE = "price";
        public static final String COLUMN_PRODUCT_SUPPLIER_ID = "supplier_id";
        //will save a string reference of the image into the db
        public static final String COLUMN_PRODUCT_IMAGE = "image";
    }
}

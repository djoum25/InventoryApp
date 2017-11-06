package com.julienlaurent.learning.com.inventorytracker;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.widget.Toast;

import com.julienlaurent.learning.com.inventorytracker.db.InventoryContract;
import com.julienlaurent.learning.com.inventorytracker.model.Product;

public class Utility {
    static void updateItemSold(Context mContext, long id) {
        final Cursor query = mContext.getContentResolver()
            .query(ContentUris.withAppendedId(InventoryContract.ProductEntry.PRODUCT_CONTENT_URI, id),
                new String[]{InventoryContract.ProductEntry.COLUMN_PRODUCT_QUANTITY,
                    InventoryContract.ProductEntry.COLUMN_PRODUCT_NAME},
                null, null, null);
        if (query != null) {
            try {
                while (query.moveToNext()) {
                    int quantity = query.getInt
                        (query.getColumnIndexOrThrow(InventoryContract.ProductEntry
                            .COLUMN_PRODUCT_QUANTITY));
                    if (quantity >= 1) {
                        quantity -= 1;
                        final int row = updateRow(mContext, quantity, id);
                        if (row > 0) {
                            Toast.makeText(mContext, mContext.getString(R.string.success_sold,
                                1, query.getString(query.getColumnIndexOrThrow
                                    (InventoryContract.ProductEntry.COLUMN_PRODUCT_NAME))),
                                Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(mContext, mContext.getString(R.string.no_more_left)
                                + query.getString(query.getColumnIndexOrThrow
                                (InventoryContract.ProductEntry.COLUMN_PRODUCT_NAME))
                                + mContext.getString(R.string.in_stock),
                            Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (Exception e) {
                e.getLocalizedMessage();
            } finally {
                query.close();
            }
        }
    }

    public static int updateRow(Context mContext, int quantity, long id) {
        ContentValues values = new ContentValues();
        values.put(InventoryContract.ProductEntry.COLUMN_PRODUCT_QUANTITY, quantity);
        return mContext.getContentResolver()
            .update(ContentUris.withAppendedId(
                InventoryContract.ProductEntry.PRODUCT_CONTENT_URI, id),
                values, null, null);
    }

    public static ContentValues productValues(Product product) {
        ContentValues values = new ContentValues();
        values.put(InventoryContract.ProductEntry.COLUMN_PRODUCT_NAME, product.getProductName());
        values.put(InventoryContract.ProductEntry.COLUMN_PRODUCT_PRICE, product.getProductPrice());
        values.put(InventoryContract.ProductEntry.COLUMN_PRODUCT_QUANTITY, product.getProductQuantity());
        values.put(InventoryContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER_ID, product.getProductSupplierId());
        values.put(InventoryContract.ProductEntry.COLUMN_PRODUCT_IMAGE, product.getProductImageResources());
        return values;
    }


    /*
    * this line of code is taken from https://stackoverflow
    * .com/questions/6936898/converting-image-to-bitmap-in-android
    */
    public static Bitmap setImage(Context context, Uri mFileUri) {
        try {
            Bitmap bitmap;
            bitmap = BitmapFactory.decodeStream(context
                .getContentResolver().openInputStream(mFileUri));
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

package com.julienlaurent.learning.com.inventorytracker;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.Nullable;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.julienlaurent.learning.com.inventorytracker.db.InventoryContract;
import com.julienlaurent.learning.com.inventorytracker.model.Product;

import java.text.NumberFormat;
import java.util.Locale;

public class ProductAdapter extends CursorAdapter {
    private NumberFormat format = NumberFormat.getCurrencyInstance(Locale.US);
    private Context mContext;

    public ProductAdapter(Context context, Cursor c) {
        super(context, c);
        mContext = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(mContext).inflate(R.layout.product_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, final Cursor cursor) {
        TextView productName = view.findViewById(R.id.product_name_content);
        TextView productPrice = view.findViewById(R.id.product_price_content);
        TextView productQuantity = view.findViewById(R.id.product_quantity_content);
        final String name;
        final double price;
        final int quantity;
        try {
            name = cursor.getString(cursor.getColumnIndexOrThrow(
                InventoryContract.ProductEntry.COLUMN_PRODUCT_NAME));
            price = cursor.getDouble(cursor.getColumnIndexOrThrow(
                InventoryContract.ProductEntry.COLUMN_PRODUCT_PRICE));
            quantity = cursor.getInt(cursor.getColumnIndexOrThrow(
                InventoryContract.ProductEntry.COLUMN_PRODUCT_QUANTITY));
            final long id = cursor.getLong(cursor.getColumnIndexOrThrow(
                InventoryContract.ProductEntry._ID));
            productName.setText(name);
            productPrice.setText(format.format(price));
            productQuantity.setText(String.valueOf(quantity));
            ImageButton remove = view.findViewById(R.id.remove_product);
            remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utility.updateItemSold(mContext, id);
                }
            });
        } catch (IllegalArgumentException e) {
            Toast.makeText(mContext, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Nullable
    @Override
    public CharSequence[] getAutofillOptions() {
        return new CharSequence[0];
    }

    public int editProduct(Product product) {
        return 0;
    }
}

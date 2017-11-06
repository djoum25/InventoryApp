package com.julienlaurent.learning.com.inventorytracker.fragments;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.julienlaurent.learning.com.inventorytracker.ProductAdapter;
import com.julienlaurent.learning.com.inventorytracker.R;
import com.julienlaurent.learning.com.inventorytracker.db.InventoryContract;

public class CurrentProducts extends Fragment
    implements LoaderManager.LoaderCallbacks<Cursor>,
    AdapterView.OnItemClickListener {

    OnCurrentProductSelectedListener mOnCurrentProductSelectedListener;
    private ProductAdapter mAdapter;

    public CurrentProducts() {
    }

    public static CurrentProducts newInstance() {
        return new CurrentProducts();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_current_products, container, false);
        getLoaderManager().initLoader(1, null, this);
        LinearLayout emptyview = view.findViewById(R.id.emptyview);
        ListView productList = view.findViewById(R.id.list_of_product);
        productList.setOnItemClickListener(this);
        mAdapter = new ProductAdapter(getContext(), null);
        productList.setAdapter(mAdapter);
        productList.setEmptyView(emptyview);
        final View header = LayoutInflater.from(getContext()).inflate(R.layout.list_header, null);
        productList.addHeaderView(header);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mOnCurrentProductSelectedListener = (OnCurrentProductSelectedListener) context;
            mOnCurrentProductSelectedListener.setActionBarTitleForCurrentProducts();
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "Must be implemented");
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sort = InventoryContract.ProductEntry.COLUMN_PRODUCT_NAME + " ASC";
        String[] projection = new String[]{
            InventoryContract.ProductEntry._ID,
            InventoryContract.ProductEntry.COLUMN_PRODUCT_NAME,
            InventoryContract.ProductEntry.COLUMN_PRODUCT_QUANTITY,
            InventoryContract.ProductEntry.COLUMN_PRODUCT_PRICE,
            InventoryContract.ProductEntry.COLUMN_PRODUCT_IMAGE
        };
        return new CursorLoader(getContext(),
            InventoryContract.ProductEntry.PRODUCT_CONTENT_URI,
            projection,
            null, null, sort);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (id != -1) {//header of the list return -1 when click
            ProductDetails details = ProductDetails.newInstance(id);
            getActivity().getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack(null)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                .replace(R.id.container, details)
                .commit();
        }
    }

    public interface OnCurrentProductSelectedListener {
        void setActionBarTitleForCurrentProducts();
    }
}

package com.julienlaurent.learning.com.inventorytracker.fragments;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.julienlaurent.learning.com.inventorytracker.R;
import com.julienlaurent.learning.com.inventorytracker.Utility;
import com.julienlaurent.learning.com.inventorytracker.db.InventoryContract;
import com.julienlaurent.learning.com.inventorytracker.model.Product;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProductDetails#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProductDetails extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {
    private static final String ARG_PARAM1 = "param1";
    private final int PRODUCT_LOADER_ID = 1;
    private onProductDetailListenerSelect mlistener;
    private ImageView mProductImageDetail;
    private TextView mProductNameDetail;
    private TextView mProductQuantityDetails;
    private TextView mProductPriceDetail;
    private TextView mProductSupplierDetails;
    private long mProductId;
    private Button mOrderFromSupplier;
    private Button mDeleteAllRecord;

    public ProductDetails() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param id Parameter 1.
     * @return A new instance of fragment ProductDetails.
     */

    public static ProductDetails newInstance(long id) {
        ProductDetails fragment = new ProductDetails();
        Bundle args = new Bundle();
        args.putLong(ARG_PARAM1, id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mProductId = getArguments().getLong(ARG_PARAM1);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof onProductDetailListenerSelect) {
            mlistener = (onProductDetailListenerSelect) context;
            mlistener.setActionBarForProductSelected();
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        } else {
            throw new RuntimeException(context.toString() + " must be implemented");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().getLoaderManager().initLoader(PRODUCT_LOADER_ID, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_details, container, false);
        assignViews(view);
        return view;
    }

    private void assignViews(View view) {
        mProductImageDetail = view.findViewById(R.id.product_image_detail);
        mProductNameDetail = view.findViewById(R.id.product_name_detail);
        mProductQuantityDetails = view.findViewById(R.id.product_quantity_details);
        mProductPriceDetail = view.findViewById(R.id.product_price_detail);
        mProductSupplierDetails = view.findViewById(R.id.product_supplier_details);
        Button addProductDetail = view.findViewById(R.id.add_product_detail);
        addProductDetail.setOnClickListener(this);
        Button removeProductDetail = view.findViewById(R.id.remove_product_detail);
        removeProductDetail.setOnClickListener(this);
        mOrderFromSupplier = view.findViewById(R.id.order_from_supplier);
        mOrderFromSupplier.setOnClickListener(this);
        mDeleteAllRecord = view.findViewById(R.id.delete_all_record);
        mDeleteAllRecord.setOnClickListener(this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] productProjection = new String[]{
            InventoryContract.ProductEntry.COLUMN_PRODUCT_NAME,
            InventoryContract.ProductEntry.COLUMN_PRODUCT_QUANTITY,
            InventoryContract.ProductEntry.COLUMN_PRODUCT_PRICE,
            InventoryContract.ProductEntry.COLUMN_PRODUCT_IMAGE,
            InventoryContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER_ID
        };
        return new CursorLoader(getContext(),
            ContentUris.withAppendedId(InventoryContract.ProductEntry
                .PRODUCT_CONTENT_URI, mProductId), productProjection,
            null, null, null);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data == null || data.getCount() < 1)
            return;

        while (data.moveToNext()) {
            final String name = data.getString(data.getColumnIndexOrThrow
                (InventoryContract.ProductEntry.COLUMN_PRODUCT_NAME));
            int quantity = data.getInt(data.getColumnIndexOrThrow
                (InventoryContract.ProductEntry.COLUMN_PRODUCT_QUANTITY));
            double price = data.getDouble(data.getColumnIndexOrThrow
                (InventoryContract.ProductEntry.COLUMN_PRODUCT_PRICE));
            final long supplierId = data.getLong(data.getColumnIndexOrThrow
                (InventoryContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER_ID));
            final String image = data.getString(data.getColumnIndexOrThrow
                (InventoryContract.ProductEntry.COLUMN_PRODUCT_IMAGE));
            Product product = new Product(name, quantity, price, supplierId, image);
            mProductNameDetail.setText(product.getProductName());
            mProductQuantityDetails.setText(String.valueOf(quantity));
            mProductPriceDetail.setText(String.valueOf(price));
            mProductSupplierDetails.setText(String.valueOf(supplierId));
            mProductSupplierDetails.setText(getSupplierName(supplierId));
            mOrderFromSupplier.setText(getString(R.string.order_more, product.getProductName()));
            mDeleteAllRecord.setText(getString(R.string.delete_record, product.getProductName()));

            if (image != null) {
                Uri uri = Uri.parse(image);
                mProductImageDetail.setImageBitmap(Utility.setImage(getContext(), uri));
            }
        }
        getActivity().getLoaderManager().destroyLoader(PRODUCT_LOADER_ID);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String getSupplierName(long supplierId) {
        String result = null;
        String[] projection = {InventoryContract.SupplierEntry.COLUMN_SUPPLIER_NAME};

        final Cursor query1 = getActivity().getContentResolver()
            .query(ContentUris.withAppendedId(InventoryContract.SupplierEntry.SUPPLIER_CONTENT_URI,
                supplierId), projection, null, null, null);
        try {
            if (query1 != null) {
                while (query1.moveToNext()) {
                    result = query1.getString(
                        query1.getColumnIndexOrThrow(InventoryContract.SupplierEntry.COLUMN_SUPPLIER_NAME));
                }
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } finally {
            if (query1 != null)
                query1.close();
        }
        return result;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.remove_product_detail:
                //operation minus
                quantityToaddOrRemove('-');
                break;
            case R.id.add_product_detail:
                //operation add
                quantityToaddOrRemove('+');
                break;
            case R.id.order_from_supplier:
                OrderFromSupplier();
                break;
            case R.id.delete_all_record:
                Toast.makeText(getContext(), getRecordName(), Toast.LENGTH_SHORT).show();
                deleteRecordWarning();
                break;
        }
    }

    public void addOrRemoveAProduct(char operation, int num) {
        String message = null;
        final Cursor query = getContext().getContentResolver()
            .query(ContentUris.withAppendedId(
                InventoryContract.ProductEntry.PRODUCT_CONTENT_URI, mProductId),
                new String[]{InventoryContract.ProductEntry.COLUMN_PRODUCT_QUANTITY,
                    InventoryContract.ProductEntry.COLUMN_PRODUCT_NAME},
                null, null, null);
        if (query != null) {
            try {
                while (query.moveToNext()) {
                    int quantity = query.getInt
                        (query.getColumnIndexOrThrow(InventoryContract.ProductEntry
                            .COLUMN_PRODUCT_QUANTITY));
                    if (quantity < 1 && operation == '-') {
                        Toast.makeText(getContext(), getContext().getString(R.string.no_more_left)
                                + query.getString(query.getColumnIndexOrThrow
                                (InventoryContract.ProductEntry.COLUMN_PRODUCT_NAME))
                                + getContext().getString(R.string.in_stock),
                            Toast.LENGTH_SHORT).show();
                    } else if (quantity >= 1 && operation == '-') {
                        if (quantity < num) {
                            Toast.makeText(getContext(), getString(
                                R.string.not_enough, num), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        quantity -= num;
                        message = "remove";
                    } else if (operation == '+') {
                        quantity += num;
                        message = "added";
                    }
                    final int row = Utility.updateRow(getContext(), quantity, mProductId);
                    if (row > 0) {
                        mProductQuantityDetails.setText(String.valueOf(quantity));
                    }
                }
            } catch (Exception e) {
                e.getLocalizedMessage();
            } finally {
                query.close();
            }
        }
    }

    public void OrderFromSupplier() {
        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setGravity(Gravity.CENTER);
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCancelable(false)
            .setTitle(getRecordName() + " Order")
            .setMessage("How many " + getRecordName() + " would you like to order?")
            .setView(input)
            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (!TextUtils.isEmpty(input.getText().toString()) && input.getText().toString().length() > 0) {
                        int quantityOrder = Integer.parseInt(input.getText().toString());
                        sendEmailToSupplier(quantityOrder, getRecordName());
                    } else {
                        input.setError(getString(R.string.error));
                        dialog.dismiss();
                        OrderFromSupplier();
                    }
                }
            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    public void deleteRecordWarning() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.warning)
            .setMessage(getString(R.string.about_to_delete, getRecordName()))
            .setCancelable(false)
            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    final int delete = getContext().getContentResolver().delete(
                        ContentUris.withAppendedId(InventoryContract.ProductEntry
                            .PRODUCT_CONTENT_URI, mProductId), null, null);
                    if (delete > 0) {
                        Toast.makeText(getContext(), R.string.delete_success, Toast.LENGTH_SHORT).show();
                        getActivity().getSupportFragmentManager().popBackStack();
                    }
                }
            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    public String getRecordName() {
        String name = null;
        final Cursor query = getContext().getContentResolver().query(
            ContentUris.withAppendedId(InventoryContract.ProductEntry
                .PRODUCT_CONTENT_URI, mProductId),
            new String[]{InventoryContract.ProductEntry.COLUMN_PRODUCT_NAME},
            null, null, null);
        try {
            if (query != null) {
                while (query.moveToNext()) {
                    name = query.getString(query.getColumnIndexOrThrow(
                        InventoryContract.ProductEntry.COLUMN_PRODUCT_NAME));
                }
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } finally {
            if (query != null)
                query.close();
        }
        return name;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void sendEmailToSupplier(int quantity, String productName) {
        final String supplierName = getSupplierName(mProductId);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_EMAIL, supplierName + "@supplieremail.com");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Order " + productName);
        intent.putExtra(Intent.EXTRA_TEXT, "I would like to order " + quantity + " " + productName);
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            startActivity(intent);
        }
        Toast.makeText(getContext(), "mail to supplier id", Toast.LENGTH_SHORT).show();
    }

    public void quantityToaddOrRemove(final char operation) {
        String title = null;
        if (operation == '-') {
            title = "Remove";
        } else if (operation == '+') {
            title = "Add";
        }
        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setGravity(Gravity.CENTER);
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCancelable(false)
            .setTitle(title)
            .setMessage(getString(R.string.add_num))
            .setView(input)
            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    int num = Integer.parseInt(input.getText().toString());
                    addOrRemoveAProduct(operation, num);
                }
            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    public interface onProductDetailListenerSelect {
        void setActionBarForProductSelected();
    }
}

package com.julienlaurent.learning.com.inventorytracker.fragments;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.julienlaurent.learning.com.inventorytracker.R;
import com.julienlaurent.learning.com.inventorytracker.Utility;
import com.julienlaurent.learning.com.inventorytracker.db.InventoryContract;
import com.julienlaurent.learning.com.inventorytracker.model.Product;

import java.io.File;
import java.util.Date;

import static android.app.Activity.RESULT_OK;

public class AddNewProduct extends Fragment implements
    View.OnClickListener, AdapterView.OnItemSelectedListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final int SUPPLIER_CURSOR_LODER_ID = 100;
    private static final int TAKE_PICTURE_REQUEST_ID = 0;
    private static final int TAKE_PICTURE_INTENT_ID = 1;
    private static final int SELECT_PICTURE_INTENT_ID = 2;
    OnAddNewProductListener mOnAddNewProductListener;
    private EditText mProductName;
    private EditText mProductQuantity;
    private EditText mProductPrice;
    private Button mProductCaptureImage;
    private ImageView mProductImage;
    private long supplierID;
    private Spinner mProductSupplier;
    private SimpleCursorAdapter mAdapter;
    private Uri mFileUri;
    private boolean mEditMode;
    private Product mEditProduct;
    private TextView addNewProductTitle;


    public AddNewProduct() {
        // Required empty public constructor
    }

    public static AddNewProduct newInstance() {
        return new AddNewProduct();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private static File getOutputMediaFile() {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES), "CameraDemo");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return new File(mediaStorageDir.getPath() + File.separator +
            "IMG_" + timeStamp + ".jpg");
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_new_product, container, false);

        assignViews(view);
        getLoaderManager().initLoader(SUPPLIER_CURSOR_LODER_ID, null, this);
        mAdapter = new SimpleCursorAdapter(
            getContext(), android.R.layout.simple_spinner_item,
            null, new String[]{InventoryContract.SupplierEntry.COLUMN_SUPPLIER_NAME},
            new int[]{android.R.id.text1}
        );
        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mProductSupplier.setAdapter(mAdapter);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mOnAddNewProductListener = (OnAddNewProductListener) context;
            mOnAddNewProductListener.setActionBarTitleForAddNewProduct();
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + getString(R.string.must_implement));
        }
    }

    public void insertProduct(Product product) throws IllegalArgumentException {
        ContentValues values = Utility.productValues(product);
        String name = values.getAsString(InventoryContract.ProductEntry.COLUMN_PRODUCT_NAME);
        int quatity = values.getAsInteger(InventoryContract.ProductEntry.COLUMN_PRODUCT_QUANTITY);
        double price = values.getAsDouble(InventoryContract.ProductEntry.COLUMN_PRODUCT_PRICE);
        String image = values.getAsString(InventoryContract.ProductEntry.COLUMN_PRODUCT_IMAGE);
        if (TextUtils.isEmpty(name) || name.length() < 1) {
            throw new IllegalArgumentException(getString(R.string.product_name_required));
        } else if (quatity < 1) {
            throw new IllegalArgumentException(getString(R.string.quantity_required));
        } else if (price < 1) {
            throw new IllegalArgumentException(getString(R.string.price_required_provider));
        } else if (TextUtils.isEmpty(image) || image.length() < 1) {
            throw new IllegalArgumentException(getString(R.string.picture_required_provider));
        } else {
            final Uri newUri = getContext().getContentResolver()
                .insert(InventoryContract.ProductEntry.PRODUCT_CONTENT_URI, values);
            if (newUri == null) {
                Toast.makeText(getContext(), R.string.failled_to, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), R.string.success_add,
                    Toast.LENGTH_SHORT).show();
            }
        }
    }

    public int upDateProduct(Product product) {
        ContentValues values = Utility.productValues(product);
        String name = values.getAsString(InventoryContract.ProductEntry.COLUMN_PRODUCT_NAME);
        int quatity = values.getAsInteger(InventoryContract.ProductEntry.COLUMN_PRODUCT_QUANTITY);
        if (TextUtils.isEmpty(name) || name.length() < 1) {
            throw new IllegalArgumentException(getString(R.string.product_name_required));
        } else if (quatity < 1) {
            throw new IllegalArgumentException(getString(R.string.quantity_required));
        } else {
            return getContext().getContentResolver()
                .update(ContentUris.withAppendedId(
                    InventoryContract.ProductEntry.PRODUCT_CONTENT_URI,
                    mEditProduct.getProductID()), values, null,
                    new String[]{String.valueOf(mEditProduct.getProductID())});
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.save_product:
                if (!mEditMode) {
                    try {
                        if (!isProductNameExist(getProductInfo().getProductName())) {
                            insertProduct(getProductInfo());
                            clearProductInputs();
                        } else {
                            modifyProductdialog(getProductInfo());
                        }
                    } catch (Exception e) {
                        Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    try {
                        final int i = upDateProduct(getProductInfo());
                        if (i != -1) {
                            Toast.makeText(getContext(), R.string.update_success, Toast.LENGTH_SHORT).show();
                            clearProductInputs();
                        } else {
                            Toast.makeText(getContext(), R.string.failled_update, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.product_capture_image:
                if (ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    mProductCaptureImage.setEnabled(false);
                    mProductCaptureImage.setTextColor(Color.RED);
                    mProductCaptureImage.setBackgroundColor(Color.GRAY);
                    ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE}, TAKE_PICTURE_REQUEST_ID);
                } else {
                    startIntenTotakePicture();
                }
                break;
            case R.id.add_supplier_new_product:
                AddSupplier addSupplier = AddSupplier.newInstance(true);
                getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .addToBackStack(null)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                    .replace(R.id.container, addSupplier)
                    .commit();
                break;
            case R.id.product_browse_image:
                if (ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, SELECT_PICTURE_INTENT_ID);
                } else {
                    startIntentSelectPicure();
                }
                break;
        }
    }

    private void clearProductInputs() {
        mFileUri = null;
        mProductImage.setImageBitmap(Utility.setImage(getContext(), null));
        mProductName.setText(null);
        mProductQuantity.setText(null);
        mProductPrice.setText(null);
        if (!mProductCaptureImage.isEnabled()) {
            mProductCaptureImage.setEnabled(true);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        supplierID = id;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        supplierID = 0;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        final String[] projection = new String[]{InventoryContract.SupplierEntry._ID,
            InventoryContract.SupplierEntry.COLUMN_SUPPLIER_NAME};
        return new CursorLoader(getActivity(), InventoryContract.SupplierEntry.SUPPLIER_CONTENT_URI,
            projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void assignViews(View view) {
        mProductName = view.findViewById(R.id.product_name);
        mProductQuantity = view.findViewById(R.id.product_quantity);
        mProductPrice = view.findViewById(R.id.product_price);
        mProductSupplier = view.findViewById(R.id.product_supplier);
        mProductSupplier.setOnItemSelectedListener(this);
        mProductCaptureImage = view.findViewById(R.id.product_capture_image);
        mProductCaptureImage.setOnClickListener(this);
        Button productBrowseImage = view.findViewById(R.id.product_browse_image);
        productBrowseImage.setOnClickListener(this);
        Button saveProduct = view.findViewById(R.id.save_product);
        mProductImage = view.findViewById(R.id.product_image);
        saveProduct.setOnClickListener(this);
        addNewProductTitle = view.findViewById(R.id.add_new_product_title);
        ImageButton addSupplier = view.findViewById(R.id.add_supplier_new_product);
        addSupplier.setOnClickListener(this);
    }

    private Product getProductInfo() throws Exception {
        int quantity;
        double price;
        String name;
        String imageResource = null;
        if (mFileUri != null) {
            imageResource = mFileUri.toString();
        }
        try {
            name = mProductName.getText().toString();
            quantity = Integer.parseInt(mProductQuantity.getText().toString());
            price = Double.parseDouble(mProductPrice.getText().toString());
            return new Product(name, quantity, price, supplierID, imageResource);
        } catch (NumberFormatException e) {
            throw new Exception(getString(R.string.check_input));
        }
    }

    /**
     * check to see if the
     *
     * @param productName name is already in the db
     * @return true/false wheter ixisted
     */
    private boolean isProductNameExist(String productName) {
        String[] projection = {
            InventoryContract.ProductEntry._ID,
            InventoryContract.ProductEntry.COLUMN_PRODUCT_NAME,
            InventoryContract.ProductEntry.COLUMN_PRODUCT_QUANTITY,
            InventoryContract.ProductEntry.COLUMN_PRODUCT_PRICE,
            InventoryContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER_ID,
            InventoryContract.ProductEntry.COLUMN_PRODUCT_IMAGE
        };
        String selection = InventoryContract.ProductEntry.COLUMN_PRODUCT_NAME + "=?";
        String[] selectionArg = {productName};
        final Cursor query = getContext().getContentResolver()
            .query(InventoryContract.ProductEntry.PRODUCT_CONTENT_URI,
                projection, selection, selectionArg, null);
        if (query != null && query.getCount() > 0) {
            try {
                while (query.moveToNext()) {
                    mEditProduct = new Product(
                        query.getLong(query.getColumnIndexOrThrow(
                            InventoryContract.ProductEntry._ID)),
                        query.getString(query.getColumnIndexOrThrow(
                            InventoryContract.ProductEntry.COLUMN_PRODUCT_NAME)),
                        query.getInt(query.getColumnIndexOrThrow(
                            InventoryContract.ProductEntry.COLUMN_PRODUCT_QUANTITY)),
                        query.getDouble(query.getColumnIndexOrThrow(
                            InventoryContract.ProductEntry.COLUMN_PRODUCT_PRICE)),
                        query.getLong(query.getColumnIndexOrThrow(
                            InventoryContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER_ID)),
                        query.getString(query.getColumnIndexOrThrow(
                            InventoryContract.ProductEntry.COLUMN_PRODUCT_IMAGE))
                    );
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } finally {
                query.close();
            }
            return true;
        }
        return false;
    }

    /**
     * display a dialog in case the
     * name of the product is existed
     * is already in the db
     */

    public void modifyProductdialog(final Product product) {
        new AlertDialog.Builder(getContext())
            .setTitle(R.string.warning)
            .setCancelable(false)
            .setMessage(product.getProductName() + getString(R.string.added_product_warning))
            .setPositiveButton(R.string.confirm_yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mOnAddNewProductListener.setActionBarToModifyMode("Edit " + product.getProductName());
                    mProductName.setText(mEditProduct.getProductName());
                    mProductQuantity.setText(String.valueOf(mEditProduct.getProductQuantity()));
                    mProductPrice.setText(String.valueOf(mEditProduct.getProductPrice()));
                    addNewProductTitle.setText(R.string.edit_product);
                    if (mEditProduct.getProductImageResources() != null) {
                        Uri uri = Uri.parse(mEditProduct.getProductImageResources());
                        mProductImage.setImageURI(uri);
                    }
                    mEditMode = true;
                }
            }).setNegativeButton(R.string.confirm_no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == TAKE_PICTURE_REQUEST_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                mProductCaptureImage.setEnabled(true);
                mProductCaptureImage.setTextColor(Color.WHITE);
                mProductCaptureImage.setBackground(getResources()
                    .getDrawable(R.drawable.background_button));
                startIntenTotakePicture();
            }
        } else if (requestCode == SELECT_PICTURE_INTENT_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                startIntentSelectPicure();
            }
        }
    }

    private void startIntentSelectPicure() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent
            .createChooser(intent, "Select Product iamge"), SELECT_PICTURE_INTENT_ID);
    }

    /*
    this part of code is taken from
    https://androidkennel.org/?s=take+picture
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void startIntenTotakePicture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mFileUri = Uri.fromFile(getOutputMediaFile());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mFileUri);
        startActivityForResult(intent, TAKE_PICTURE_INTENT_ID);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == TAKE_PICTURE_INTENT_ID) {
            if (resultCode == RESULT_OK) {
                mProductImage.setImageBitmap(Utility.setImage(getContext(), mFileUri));
            }
        } else if (requestCode == SELECT_PICTURE_INTENT_ID) {
            if (resultCode == RESULT_OK) {
                if (data != null && data.getData() != null) {
                    mFileUri = data.getData();
                    mProductImage.setImageBitmap(Utility.setImage(getContext(), mFileUri));
                }
            }
        }
    }

    public interface OnAddNewProductListener {
        void setActionBarTitleForAddNewProduct();

        void setActionBarToModifyMode(String text);
    }
}

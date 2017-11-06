package com.julienlaurent.learning.com.inventorytracker.fragments;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.julienlaurent.learning.com.inventorytracker.R;
import com.julienlaurent.learning.com.inventorytracker.db.InventoryContract;
import com.julienlaurent.learning.com.inventorytracker.model.Supplier;

public class AddSupplier extends Fragment implements View.OnClickListener {

    private static final String ARG_PARAM1 = "param1";
    /**
     * view
     */
    private EditText mSupplierName;
    private EditText mSupplierPhone;
    private EditText mSupplierFax;
    private EditText mSupplierEmail;
    private EditText mSupplierAddressLine1;
    private EditText mSupplierAddressLine2;
    private EditText mSupplierCity;
    private EditText mSupplierZipCode;
    private EditText mSupplierState;
    private EditText mSupplierCountry;
    private OnSupplierFragmentInteractionListener mListener;
    private boolean status;

    public AddSupplier() {
        // Required empty public constructor
    }

    public static AddSupplier newInstance(boolean status) {
        AddSupplier fragment = new AddSupplier();
        Bundle args = new Bundle();
        args.putBoolean(ARG_PARAM1, status);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            status = getArguments().getBoolean(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_supplier,
            container, false);
        assignViews(view);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSupplierFragmentInteractionListener) {
            mListener = (OnSupplierFragmentInteractionListener) context;
            mListener.setActionBarForAddSupplierFragment();
        } else {
            throw new RuntimeException(context.toString()
                + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void assignViews(View view) {
        mSupplierName = view.findViewById(R.id.supplier_name);
        mSupplierPhone = view.findViewById(R.id.supplier_phone);
        mSupplierFax = view.findViewById(R.id.supplier_fax);
        mSupplierEmail = view.findViewById(R.id.supplier_email);
        mSupplierAddressLine1 = view.findViewById(R.id.supplier_address_line_1);
        mSupplierAddressLine2 = view.findViewById(R.id.supplier_address_line_2);
        mSupplierCity = view.findViewById(R.id.supplier_city);
        mSupplierZipCode = view.findViewById(R.id.supplier_zip_code);
        mSupplierState = view.findViewById(R.id.supplier_state);
        mSupplierCountry = view.findViewById(R.id.supplier_country);
        Button saveSupplier = view.findViewById(R.id.save_supplier);
        saveSupplier.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.save_supplier:
                try {
                    if (!isSupplierExist(getSupplierInfo().getSupplierName())) {
                        insertSupplier(getSupplierInfo());
                        clearTheInput();
                    } else {
                        Toast.makeText(getContext(), R.string.supplier_exist_already,
                            Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
                if (status) {
                    getActivity().getSupportFragmentManager().popBackStack();
                }
                break;
        }
    }

    private void insertSupplier(Supplier supplier) throws IllegalArgumentException {
        ContentValues values = new ContentValues();
        values.put(InventoryContract.SupplierEntry.COLUMN_SUPPLIER_NAME, supplier.getSupplierName());
        values.put(InventoryContract.SupplierEntry.COLUMN_SUPPLIER_FULL_ADDRESS, supplier.getSupplierFullAddress());
        values.put(InventoryContract.SupplierEntry.COLUMN_SUPPLIER_EMAIL, supplier.getSupplierEmail());
        values.put(InventoryContract.SupplierEntry.COLUMN_SUPPLIER_FAX_NUMBER, supplier.getSupplierFaxNumber());
        values.put(InventoryContract.SupplierEntry.COLUMN_SUPPLIER_PHONE_NUMBER, supplier.getSupplierPhoneNumber());
        String name = values.getAsString(InventoryContract.SupplierEntry.COLUMN_SUPPLIER_NAME);
        if (TextUtils.isEmpty(name) || name.length() < 1) {
            throw new IllegalArgumentException(getString(R.string.name_required));
        } else {
            final Uri newUri = getContext().getContentResolver()
                .insert(InventoryContract.SupplierEntry.SUPPLIER_CONTENT_URI, values);
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(getContext(), "Failled to add "
                    + supplier.getSupplierName(), Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(getContext(), "Successfully added " + supplier.getSupplierName(),
                    Toast.LENGTH_SHORT).show();
            }
        }
    }

    private Supplier getSupplierInfo() throws Exception {
        Supplier supplier;
        try {
            String name = mSupplierName.getText().toString();
            String phone = mSupplierPhone.getText().toString();
            String fax = mSupplierFax.getText().toString();
            String email = mSupplierEmail.getText().toString();
            String add1 = mSupplierAddressLine1.getText().toString();
            String add2 = mSupplierAddressLine2.getText().toString();
            String city = mSupplierCity.getText().toString();
            String zip = mSupplierZipCode.getText().toString();
            String state = mSupplierState.getText().toString();
            String country = mSupplierCountry.getText().toString();
            supplier = new Supplier(name, phone, fax, email, add1, add2, city, zip, state, country);
        } catch (Exception e) {
            throw new Exception(e);
        }
        return supplier;
    }

    private void clearTheInput() {
        mSupplierName.setText(null);
        mSupplierPhone.setText(null);
        mSupplierFax.setText(null);
        mSupplierEmail.setText(null);
        mSupplierAddressLine1.setText(null);
        mSupplierAddressLine2.setText(null);
        mSupplierCity.setText(null);
        mSupplierZipCode.setText(null);
        mSupplierState.setText(null);
        mSupplierCountry.setText(null);
    }

    /**
     * check to see if the name of
     * the supplier is not already
     * in the db
     */
    public boolean isSupplierExist(String supplier) {
        String selection = InventoryContract.SupplierEntry.COLUMN_SUPPLIER_NAME + "=?";
        String[] selectionArgs = {supplier};
        final Cursor query = getContext().getContentResolver()
            .query(InventoryContract.SupplierEntry.SUPPLIER_CONTENT_URI,
                new String[]{InventoryContract.SupplierEntry.COLUMN_SUPPLIER_NAME},
                selection, selectionArgs, null);
        if (query != null && query.getCount() > 0) {
            query.close();
            return true;
        }
        return false;
    }

    public interface OnSupplierFragmentInteractionListener {
        void setActionBarForAddSupplierFragment();
    }
}

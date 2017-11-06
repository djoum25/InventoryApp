package com.julienlaurent.learning.com.inventorytracker;

import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.julienlaurent.learning.com.inventorytracker.db.InventoryContract;
import com.julienlaurent.learning.com.inventorytracker.db.InventoryDbHelper;
import com.julienlaurent.learning.com.inventorytracker.fragments.AddNewProduct;
import com.julienlaurent.learning.com.inventorytracker.fragments.AddSupplier;
import com.julienlaurent.learning.com.inventorytracker.fragments.CurrentProducts;
import com.julienlaurent.learning.com.inventorytracker.fragments.ProductDetails;

public class MainActivity extends AppCompatActivity implements
    AddNewProduct.OnAddNewProductListener,
    CurrentProducts.OnCurrentProductSelectedListener,
    AddSupplier.OnSupplierFragmentInteractionListener,
    ProductDetails.onProductDetailListenerSelect {
    Toolbar mToolbar;
    private SQLiteDatabase mDatabase;
    private FloatingActionButton fab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("Product");
        setSupportActionBar(mToolbar);

        //load the list of current product activity
        CurrentProducts currentProducts = CurrentProducts.newInstance();
        getSupportFragmentManager()
            .beginTransaction()
            .addToBackStack(null)
            .setTransition(FragmentTransaction.TRANSIT_ENTER_MASK)
            .replace(R.id.container, currentProducts)
            .commit();

        fab = findViewById(R.id.add_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddNewProduct addNewProduct = AddNewProduct.newInstance();
                getSupportFragmentManager()
                    .beginTransaction()
                    .addToBackStack(null)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                    .replace(R.id.container, addNewProduct)
                    .commit();
            }
        });

        InventoryDbHelper mhelper = new InventoryDbHelper(this);
        mDatabase = mhelper.getWritableDatabase();
    }

    @Override
    public void onBackPressed() {
        int countFrag = getSupportFragmentManager().getBackStackEntryCount();
        if (countFrag == 2) {
            fab.setVisibility(View.VISIBLE);
        }
        if (countFrag > 1) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
            finish();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mDatabase.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                return true;
            case R.id.action_add:
                action_add();
                return true;
            case R.id.action_delete:
                confirmDeleteAllProduct();
                return true;
            case R.id.action_delete_supplier:
                confirmDeleteAllSupplier();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void confirmDeleteAllSupplier() {
        new AlertDialog.Builder(this)
            .setCancelable(false)
            .setTitle(getString(R.string.warning))
            .setMessage(R.string.delete_supplier_message)
            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    MainActivity.this.getContentResolver().delete(InventoryContract
                        .SupplierEntry.SUPPLIER_CONTENT_URI, null, null);
                }
            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    @Override
    public void setActionBarTitleForAddNewProduct() {
        if (mToolbar != null) {
            mToolbar.setTitle(R.string.add_new_product);
        }
        fab.setVisibility(View.GONE);
    }

    @Override
    public void setActionBarToModifyMode(String text) {
        if (mToolbar != null) {
            mToolbar.setTitle(text);
        }
        fab.setVisibility(View.GONE);
    }

    @Override
    public void setActionBarTitleForCurrentProducts() {
        if (mToolbar != null) {
            mToolbar.setTitle(R.string.product);
        }
    }

    @Override
    public void setActionBarForAddSupplierFragment() {
        if (mToolbar != null) {
            mToolbar.setTitle(R.string.add_new_supplier);
        }
        fab.setVisibility(View.GONE);
    }

    public void action_add() {
        String[] addItems = {getString(R.string.product_choice), getString(R.string.supplier_choice)};
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.action_add_title)
            .setCancelable(true)
            .setItems(addItems, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case 0:
                            AddNewProduct newProduct = AddNewProduct.newInstance();
                            getSupportFragmentManager()
                                .beginTransaction()
                                .addToBackStack(null)
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                                .replace(R.id.container, newProduct)
                                .commit();
                            break;
                        case 1:
                            AddSupplier newSupplier = AddSupplier.newInstance(false);
                            getSupportFragmentManager()
                                .beginTransaction()
                                .addToBackStack(null)
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                                .replace(R.id.container, newSupplier)
                                .commit();
                            break;
                    }
                }
            }).show();
    }

    @Override
    public void setActionBarForProductSelected() {
        if (mToolbar != null) {
            mToolbar.setTitle("Product details");
        }
        fab.setVisibility(View.GONE);
    }

    public void confirmDeleteAllProduct() {
        new AlertDialog.Builder(this)
            .setCancelable(false)
            .setTitle(getString(R.string.warning))
            .setMessage(getString(R.string.delete_product_message))
            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    MainActivity.this.getContentResolver().delete(InventoryContract
                        .ProductEntry.PRODUCT_CONTENT_URI, null, null);
                }
            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }
}

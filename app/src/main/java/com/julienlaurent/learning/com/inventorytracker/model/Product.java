package com.julienlaurent.learning.com.inventorytracker.model;

/**
 * Created by djoum on 10/30/17.
 */

public class Product {

    private long productID;
    private String productName;
    private int productQuantity;
    private double productPrice;
    private long productSupplierId;
    private String productImageResources;

    public Product() {
    }


    public Product(String productName, int productQuantity, double productPrice, long productSupplierId, String productImageResources) {
        this.productName = productName;
        this.productQuantity = productQuantity;
        this.productPrice = productPrice;
        this.productSupplierId = productSupplierId;
        this.productImageResources = productImageResources;
    }

    public Product(long productID, String productName,
                   int productQuantity, double productPrice,
                   long productSupplierId, String imageRessource) {
        this.productID = productID;
        this.productName = productName;
        this.productQuantity = productQuantity;
        this.productPrice = productPrice;
        this.productSupplierId = productSupplierId;
        this.productImageResources = imageRessource;
    }

    public long getProductID() {
        return productID;
    }

    public String getProductName() {
        return productName;
    }

    public int getProductQuantity() {
        return productQuantity;
    }

    public double getProductPrice() {
        return productPrice;
    }

    public long getProductSupplierId() {
        return productSupplierId;
    }

    public String getProductImageResources() {
        return productImageResources;
    }
}

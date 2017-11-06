package com.julienlaurent.learning.com.inventorytracker.model;

/**
 * Created by djoum on 10/30/17.
 */

public class Supplier {
    private long supplierId;
    private String supplierName;
    private String supplierPhoneNumber;
    private String supplierFaxNumber;
    private String supplierEmail;
    private String supplierFullAddress;

    public Supplier(long supplierId, String supplierName) {
        this.supplierId = supplierId;
        this.supplierName = supplierName;
    }

    public Supplier(long supplierId, String supplierName, String supplierPhoneNumber,
                    String supplierFaxNumber, String supplierEmail, String supplierFullAddress) {
        this.supplierId = supplierId;
        this.supplierName = supplierName;
        this.supplierPhoneNumber = supplierPhoneNumber;
        this.supplierFaxNumber = supplierFaxNumber;
        this.supplierEmail = supplierEmail;
        this.supplierFullAddress = supplierFullAddress;
    }

    public Supplier(String supplierName, String supplierPhoneNumber,
                    String supplierFaxNumber, String supplierEmail,
                    String supplierAddressLine1, String supplierAddressLine2,
                    String supplierCity, String supplierZip, String supplierState,
                    String supplierCountry) {
        this.supplierName = supplierName;
        this.supplierPhoneNumber = supplierPhoneNumber;
        this.supplierFaxNumber = supplierFaxNumber;
        this.supplierEmail = supplierEmail;

        createSupplierFullAddress(supplierAddressLine1, supplierAddressLine2,
            supplierCity, supplierZip, supplierState, supplierCountry);
    }

    //Just combine the string in order not to create a class or table address
    public void createSupplierFullAddress(String address1, String address2,
                                          String city, String zip, String state, String country) {
        StringBuilder builder = new StringBuilder(address1).append(" ")
            .append(address2).append(" ")
            .append(city).append(" ").append(state).append(" ")
            .append(zip).append(" ").append(country);
        supplierFullAddress = builder.toString();
    }

    public String getSupplierName() {
        return supplierName;
    }

    public String getSupplierPhoneNumber() {
        return supplierPhoneNumber;
    }

    public String getSupplierFaxNumber() {
        return supplierFaxNumber;
    }

    public String getSupplierEmail() {
        return supplierEmail;
    }

    public String getSupplierFullAddress() {
        return supplierFullAddress;
    }

    public long getSupplierId() {
        return supplierId;
    }

    @Override
    public String toString() {
        return supplierName;
    }


}

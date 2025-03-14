package com.nssi.chuaphilippinescorp;

public class ItemModel {
    private String id;
    private String supplierName;
    private String barcode;
    private String Quantity;
    private String dateStamp;

    public ItemModel() {
    }

    public ItemModel( String supplierName, String barcode, String quantity, String dateStamp) {
        this.supplierName = supplierName;
        this.barcode = barcode;
        Quantity = quantity;
        this.dateStamp = dateStamp;
    }
    public ItemModel( String id ,String supplierName, String barcode, String quantity, String dateStamp) {
        this.supplierName = supplierName;
        this.barcode = barcode;
        Quantity = quantity;
        this.dateStamp = dateStamp;
        this.id = id;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getQuantity() {
        return Quantity;
    }

    public void setQuantity(String quantity) {
        Quantity = quantity;
    }

    public String getDateStamp() {
        return dateStamp;
    }

    public void setDateStamp(String dateStamp) {
        this.dateStamp = dateStamp;
    }
}

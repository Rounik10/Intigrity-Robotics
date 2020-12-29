package com.example.intigritirobotics;

public class OrderDetailItemsModel {
    private String ProductID;
    private  String ProductPrice;
    private  String ProductQty;
    private String ProductRating;

    public OrderDetailItemsModel(String productID, String productPrice, String productQty, String productRating) {
        ProductID = productID;
        ProductPrice = productPrice;
        ProductQty = productQty;
        ProductRating = productRating;
    }

    public String getProductID() {
        return ProductID;
    }

    public void setProductID(String productID) {
        ProductID = productID;
    }

    public String getProductPrice() {
        return ProductPrice;
    }

    public void setProductPrice(String productPrice) {
        ProductPrice = productPrice;
    }

    public String getProductQty() {
        return ProductQty;
    }

    public void setProductQty(String productQty) {
        ProductQty = productQty;
    }

    public String getProductRating() {
        return ProductRating;
    }

    public void setProductRating(String productRating) {
        ProductRating = productRating;
    }
}

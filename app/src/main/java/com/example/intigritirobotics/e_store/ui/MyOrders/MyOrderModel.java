package com.example.intigritirobotics.e_store.ui.MyOrders;

public class MyOrderModel {
    private  String OrderID;
    private String OrderDate;
    private  String ProductID;
    private  String ProductStatus;
    private String ProductQty;
    private String ProductPrices;

    public MyOrderModel(String orderID, String orderDate, String productID, String productStatus, String productQty, String productPrices) {
        OrderID = orderID;
        OrderDate = orderDate;
        ProductID = productID;
        ProductStatus = productStatus;
        ProductQty =  productQty;
        ProductPrices = productPrices;
    }

    public String getProductPrices() {return ProductPrices;}

    public void setProductPrices(String productPrices) {ProductPrices = productPrices;}

    public String getProductQty() {return ProductQty;}

    public void setProductQty(String productQty) {ProductQty = productQty;}

    public String getOrderID() {
        return OrderID;
    }

    public void setOrderID(String orderID) {
        OrderID = orderID;
    }

    public String getOrderDate() {
        return OrderDate;
    }

    public void setOrderDate(String orderDate) {
        OrderDate = orderDate;
    }

    public String getProductID() {
        return ProductID;
    }

    public void setProductID(String productID) {
        ProductID = productID;
    }

    public String getProductStatus() {
        return ProductStatus;
    }

    public void setProductStatus(String productStatus) {
        ProductStatus = productStatus;
    }
}

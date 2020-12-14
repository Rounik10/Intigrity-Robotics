package com.example.intigritirobotics.ui.MyOrders;

public class MyOrderModel {
    private  String OrderID;
    private String OrderDate;
    private  String ProductID;
    private  String ProductStatus;

    public MyOrderModel(String orderID, String orderDate, String productID, String productStatus) {
        OrderID = orderID;
        OrderDate = orderDate;
        ProductID = productID;
        ProductStatus = productStatus;
    }

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

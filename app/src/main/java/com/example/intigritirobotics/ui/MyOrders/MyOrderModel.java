package com.example.intigritirobotics.ui.MyOrders;

public class MyOrderModel {
    private  String OrderID;
    private String OrderDate;
    private  String ProductTitle;
    private  String ProductPic;
    private  String ProductStatus;


    public MyOrderModel(String orderID, String orderDate, String productTitle, String productPic, String productStatus) {
        OrderID = orderID;
        OrderDate = orderDate;
        ProductTitle = productTitle;
        ProductPic = productPic;
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

    public String getProductTitle() {
        return ProductTitle;
    }

    public void setProductTitle(String productTitle) {
        ProductTitle = productTitle;
    }

    public String getProductPic() {
        return ProductPic;
    }

    public void setProductPic(String productPic) {
        ProductPic = productPic;
    }

    public String getProductStatus() {
        return ProductStatus;
    }

    public void setProductStatus(String productStatus) {
        ProductStatus = productStatus;
    }
}

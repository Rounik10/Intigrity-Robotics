package com.example.intigritirobotics;

public class ViewAllModel {
    private String Id;
    private String Image;
    private String Title;
    private float TotalRating;
    private int FinalPrice;

    public ViewAllModel(String id, String image, String title, float totalRating, int finalPrice) {
        Id = id;
        Image = image;
        Title = title;
        TotalRating = totalRating;
        FinalPrice = finalPrice;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public float getTotalRating() {
        return TotalRating;
    }

    public void setTotalRating(int totalRating) {
        TotalRating = totalRating;
    }

    public int getFinalPrice() {
        return FinalPrice;
    }

    public void setFinalPrice(int finalPrice) {
        FinalPrice = finalPrice;
    }

}

package com.example.intigritirobotics;

public class ViewAllModel {
    private String Id;
    private final String Image;
    private String Title;
    private final float TotalRating;
    private final int FinalPrice;

    public ViewAllModel(String id, String image, String title, float totalRating, int finalPrice) {
        Id = id;
        Image = image;
        Title = title;
        TotalRating = totalRating;
        FinalPrice = finalPrice;
    }

    @Override
    public String toString() {
        return Id + "\n" + Image + "\n" + Title + "\n" + TotalRating + "\n" + FinalPrice;
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

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public float getTotalRating() {
        return TotalRating;
    }

    public int getFinalPrice() {
        return FinalPrice;
    }

}

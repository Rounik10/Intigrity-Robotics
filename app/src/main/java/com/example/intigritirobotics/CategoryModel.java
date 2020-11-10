package com.example.intigritirobotics;



public class CategoryModel {
    private  String  index;
    private String Image;
    private  String Title;


    public CategoryModel(String index, String image, String title) {
        this.index = index;
        Image = image;
        Title = title;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
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
}

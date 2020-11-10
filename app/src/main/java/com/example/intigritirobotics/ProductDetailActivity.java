package com.example.intigritirobotics;

import androidx.appcompat.app.AppCompatActivity;

import android.media.Image;
import android.os.Bundle;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.models.SlideModel;

import java.util.ArrayList;
import java.util.List;

public class ProductDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        ImageSlider imageSlider = findViewById(R.id.imgSlider);
        List<SlideModel> slideModelList = new ArrayList<>();
        slideModelList.add(new SlideModel(R.drawable.cart,"Image 1"));
        slideModelList.add(new SlideModel(R.drawable.email,"Image 2"));
        slideModelList.add(new SlideModel(R.drawable.forgot4,"Image 3"));
        slideModelList.add(new SlideModel(R.drawable.feedback,"Image 4"));
        imageSlider.setImageList(slideModelList,false);
    }
}
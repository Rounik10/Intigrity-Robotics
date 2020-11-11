package com.example.intigritirobotics;

import androidx.appcompat.app.AppCompatActivity;

import android.media.Image;
import android.os.Bundle;
import android.widget.ImageView;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.material.slider.Slider;

import java.util.ArrayList;
import java.util.List;

public class ProductDetailActivity extends AppCompatActivity {

    private ImageSlider imageSlider;
    private  List<SlideModel> slideModelList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

         imageSlider = findViewById(R.id.imgSlider);
      slideModelList.add(new SlideModel(R.drawable.cart,null));
       slideModelList.add(new SlideModel(R.drawable.email,null));
        slideModelList.add(new SlideModel(R.drawable.forgot4,null));
        slideModelList.add(new SlideModel(R.drawable.feedback,null));
        imageSlider.setImageList(slideModelList, false);
    }
}

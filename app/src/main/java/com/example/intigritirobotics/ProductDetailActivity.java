package com.example.intigritirobotics;

import android.os.Bundle;
import android.view.Menu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.material.tabs.TabLayout;
import java.util.ArrayList;
import java.util.List;

public class ProductDetailActivity extends AppCompatActivity {

    private final List<SlideModel> slideModelList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_product_detail);
        ImageSlider imageSlider = findViewById(R.id.imgSlider);
        slideModelList.add(new SlideModel(R.drawable.cart, ScaleTypes.FIT));
        slideModelList.add(new SlideModel(R.drawable.email,ScaleTypes.FIT));
        slideModelList.add(new SlideModel(R.drawable.forgot4,ScaleTypes.FIT));
        slideModelList.add(new SlideModel(R.drawable.feedback,ScaleTypes.FIT));
        imageSlider.setImageList(slideModelList);

        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("Some Details");
        arrayList.add("Specs");
        arrayList.add("Other Details");

        TabLayout tabLayout = findViewById(R.id.detailTabs);
        ViewPager viewPager = findViewById(R.id.viewPager);

        prepareViewPager(viewPager, arrayList);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void prepareViewPager(ViewPager viewPager, ArrayList<String> arrayList) {
        TabLayoutAdapter adapter = new TabLayoutAdapter(getSupportFragmentManager());
        productDetailFragment fragment = new productDetailFragment();
        for(int i=0; i< arrayList.size(); i++) {
            Bundle bundle = new Bundle();
            bundle.putString("title",arrayList.get(i));
            fragment.setArguments(bundle);
            adapter.addFragment(fragment,arrayList.get(i));
            fragment = new productDetailFragment();
        }
        viewPager.setAdapter(adapter);
    }

}
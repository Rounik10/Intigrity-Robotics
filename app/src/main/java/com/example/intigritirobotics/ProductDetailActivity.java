package com.example.intigritirobotics;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ProductDetailActivity extends AppCompatActivity {

    private final List<SlideModel> slideModelList = new ArrayList<>();
    private String price, rating, index, title, id;
    public FirebaseFirestore firebaseFirestore;
    Button addToCartButton;
    String productPath;
    String userPath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toolbar toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        price = intent.getStringExtra("Price");
        Toast.makeText(this, price, Toast.LENGTH_SHORT).show();
        rating = intent.getStringExtra("Rating");
        title = intent.getStringExtra("Title");
        index = intent.getStringExtra("Index");
        id = intent.getStringExtra("ID");
        firebaseFirestore = FirebaseFirestore.getInstance();
        setContentView(R.layout.activity_product_detail);


        productPath = "/CATEGORY/"+"1AcKQNSDSQqnpvA5e4vN"+"/products/"+id;
        userPath = "";
        loadProductDetails();
        addToCartButton = findViewById(R.id.addToCartButton);
        addToCartButton.setOnClickListener(view -> addItemToCart());
    }

    private void addItemToCart() {
        Map<String, String> idMap = new HashMap<>();
        idMap.put("Id", id);
        firebaseFirestore.collection("USERS")
                .document("cNulLD3zkhRYH64gZhaLNpU0cc02")
                .collection("My Cart").add(idMap);

        Toast.makeText(this, "Item added to the cart", Toast.LENGTH_SHORT).show();
    }

    private void prepareViewPager(ViewPager viewPager, ArrayList<String> arrayList, String details, String spec, String other) {
        TabLayoutAdapter adapter = new TabLayoutAdapter(getSupportFragmentManager());
        productDetailFragment fragment = new productDetailFragment();
        for(int i=0; i< arrayList.size(); i++) {
            Bundle bundle = new Bundle();
            if(i==0) bundle.putString("title",details);
            else if(i==1) bundle.putString("title", spec);
            else bundle.putString("title",other);
            fragment.setArguments(bundle);
            adapter.addFragment(fragment,arrayList.get(i));
            fragment = new productDetailFragment();
        }
        viewPager.setAdapter(adapter);
    }

    private void loadProductDetails() {

        int product_index = 0;

        firebaseFirestore.collection("/CATEGORY/1AcKQNSDSQqnpvA5e4vN/products")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot doc = Objects.requireNonNull(task.getResult()).getDocuments().get(product_index);
                        loadDataToProduct(doc);
                    } else {
                        Log.w("Product", "Error getting documents.", task.getException());
                    }
                });

    }

    private void loadDataToProduct(DocumentSnapshot product) {
        String in_stock = Objects.requireNonNull(product.get("in_stock")).toString();

        // In stock
        if(in_stock.equals("false")) {
            TextView inStockText = findViewById(R.id.inStockText);
            inStockText.setBackgroundColor(Color.RED);
            inStockText.setText("Out of Stock");
            Button buyNow = findViewById(R.id.buyNowButton);
            buyNow.setEnabled(false);
            Button addToCart = findViewById(R.id.addToCartButton);
            addToCart.setEnabled(false);
        }

        // Slider
        ImageSlider imageSlider = findViewById(R.id.imgSlider);

        String[] productPicUrls = product.get("product_pic").toString().split(", ");

        for(String imgUrl: productPicUrls) slideModelList.add(new SlideModel(imgUrl, null));


        imageSlider.setImageList(slideModelList);


        TextView briefText = findViewById(R.id.itemBriefDetail);
        briefText.setText(Objects.requireNonNull(product.get("product_title")).toString());

        // Tabs
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("Some Details");
        arrayList.add("Specs");
        arrayList.add("Other Details");

        String details = Objects.requireNonNull(product.get("details")).toString();
        String spec = Objects.requireNonNull(product.get("specs")).toString();
        String other = Objects.requireNonNull(product.get("other")).toString();

        TabLayout tabLayout = findViewById(R.id.detailTabs);
        ViewPager viewPager = findViewById(R.id.viewPager);

        prepareViewPager(viewPager, arrayList, details, spec, other);
        tabLayout.setupWithViewPager(viewPager);

        // Set Price
        TextView priceImageView = findViewById(R.id.PriceText);
        String priceText = "Rs."+ price +"/-";
        priceImageView.setText(priceText);

        TextView MRP = findViewById(R.id.cut_price);
        String mrpText = "Rs. " + Objects.requireNonNull(product.get("MRP")).toString() + "/-";
        MRP.setText(mrpText);
        MRP.setPaintFlags(MRP.getPaintFlags()|Paint.STRIKE_THRU_TEXT_FLAG);

        // Set Ratings
        TextView avgRatingText = findViewById(R.id.averageRatingText);
        avgRatingText.setText(getAvg(product));

        String total = Objects.requireNonNull(product.get("total_ratings")).toString();
        TextView totalRatings = findViewById(R.id.number_of_rating_text);
        totalRatings.setText(total);

        setProgressInRacingBars(findViewById(R.id.stars1), 1, product);
        setProgressInRacingBars(findViewById(R.id.stars2), 2, product);
        setProgressInRacingBars(findViewById(R.id.stars3), 3, product);
        setProgressInRacingBars(findViewById(R.id.stars4), 4, product);
        setProgressInRacingBars(findViewById(R.id.stars5), 5, product);

    }

    private String getAvg(DocumentSnapshot product) {
        float sum = 0, total = 0, temp;
        for(int i=1;i<=5;i++) {
            temp = Integer.parseInt(Objects.requireNonNull(product.get(i + "_stars")).toString());
            sum += i*temp;
            total += temp;
        }
        String average = ""+sum/total;
        if(average.length()>3) average = average.substring(0,3);
        return average;
    }

    void setProgressInRacingBars(ProgressBar progressBar, int starNo, DocumentSnapshot product) {
        int stars = Integer.parseInt(Objects.requireNonNull(product.get(starNo + "_stars")).toString());
        int total = Integer.parseInt(Objects.requireNonNull(product.get("total_ratings")).toString());
        progressBar.setMax(total);
        progressBar.setProgress(stars);
    }

}
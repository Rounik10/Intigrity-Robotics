package com.example.intigritirobotics;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.example.intigritirobotics.MainHomeActivity.currentUserUId;

public class ProductDetailActivity extends AppCompatActivity {

    private final List<SlideModel> slideModelList = new ArrayList<>();
    private String price, title, id;
    private int total, prev_rating;
    public FirebaseFirestore firebaseFirestore;
    Button addToCartButton;
    String userPath;
    int is_app_starting;
    private TextView totalRatings;
    private RatingBar ratingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toolbar toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        price = intent.getStringExtra("Price");
        title = intent.getStringExtra("Title");
        id = intent.getStringExtra("ID");
        firebaseFirestore = FirebaseFirestore.getInstance();
        setContentView(R.layout.activity_product_detail);

        is_app_starting = 0;
        ratingBar = (RatingBar) findViewById(R.id.rating_stars);
        totalRatings = (TextView) findViewById(R.id.number_of_rating_text);

        userPath = "";
        loadProductDetails();
        addToCartButton = findViewById(R.id.addToCartButton);
        addToCartButton.setOnClickListener(view -> addItemToCart());

        ratingBar.setOnRatingBarChangeListener((ratingBar, v, b) -> {

            is_app_starting++;
            Map<String, String> map = new HashMap<>();
            map.put("Rating", "" + v);

            firebaseFirestore.document("USERS/" + currentUserUId + "/My Ratings/" + id).set(map);

            DocumentReference docRef = firebaseFirestore.document("PRODUCTS/" + id);

            docRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && is_app_starting > 1) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    String s = documentSnapshot.get((v + "").substring(0, 1) + "_star").toString();
                    int x = Integer.parseInt(s);

                    if (prev_rating != 0) {
                        int y = Integer.parseInt(documentSnapshot.get(prev_rating + "_star").toString()) - 1;
                        docRef.update(prev_rating + "_star", "" + y);
                    }
                    Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
                    x++;
                    docRef.update((v + "").substring(0, 1) + "_star", "" + x);
                }
            });

        });

        firebaseFirestore.document("USERS/" + currentUserUId + "/My Ratings/" + id).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot documentSnapshot = task.getResult();
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    ratingBar.setRating(Float.parseFloat(documentSnapshot.get("Rating").toString()));
                }
            }
        });

    }

    private void addItemToCart() {
        Map<String, String> map = new HashMap<>();
        map.put("Id", id);

        firebaseFirestore.document("/USERS/" + currentUserUId + "/My Cart/" + id).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        if (documentSnapshot.exists()) {
                            Toast.makeText(getApplicationContext(), "Item is already added in the cart", Toast.LENGTH_SHORT).show();
                        } else {
                            firebaseFirestore.collection("USERS")
                                    .document(currentUserUId)
                                    .collection("My Cart").document(id).set(map);
                            Toast.makeText(getApplicationContext(), "Item was added to the cart", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Dummy text", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void prepareViewPager(ViewPager viewPager, ArrayList<String> arrayList, String details, String spec, String other) {
        TabLayoutAdapter adapter = new TabLayoutAdapter(getSupportFragmentManager());
        productDetailFragment fragment = new productDetailFragment();
        for (int i = 0; i < arrayList.size(); i++) {
            Bundle bundle = new Bundle();
            if (i == 0) bundle.putString("title", details);
            else if (i == 1) bundle.putString("title", spec);
            else bundle.putString("title", other);
            fragment.setArguments(bundle);
            adapter.addFragment(fragment, arrayList.get(i));
            fragment = new productDetailFragment();
        }
        viewPager.setAdapter(adapter);
    }

    private void loadProductDetails() {

        String docPath = "/PRODUCTS/" + id; // Extra space was there //
        firebaseFirestore.document(docPath)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot doc = Objects.requireNonNull(task.getResult());
                        loadDataToProduct(doc);
                    } else {
                        Log.d("Error", task.getException().toString());
                    }
                });

    }

    private void loadDataToProduct(DocumentSnapshot product) {

        if (product == null) {
            return;
        }
        String in_stock = Objects.requireNonNull(product.get("in stock")).toString();
        // In stock
        if(in_stock.equals("false")) {
            TextView inStockText = findViewById(R.id.inStockText);
            inStockText.setBackgroundColor(Color.RED);
            inStockText.setText(R.string.out_of_stock);
            Button buyNow = findViewById(R.id.buyNowButton);
            buyNow.setEnabled(false);
            Button addToCart = findViewById(R.id.addToCartButton);
            addToCart.setEnabled(false);
        }

        // Slider
        ImageSlider imageSlider = findViewById(R.id.imgSlider);

        String[] productPicUrls = product.get("product_pic").toString().split(", ");

        for (String imgUrl : productPicUrls) slideModelList.add(new SlideModel(imgUrl, null));

        imageSlider.setImageList(slideModelList);

        TextView briefText = findViewById(R.id.itemBriefDetail);
        briefText.setText(title);

        // Set Ratings
        TextView avgRatingText = findViewById(R.id.averageRatingText);
        avgRatingText.setText(getAvg(product));


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
        String priceText = "Rs." + price + "/-";
        priceImageView.setText(priceText);

        TextView MRP = findViewById(R.id.cut_price);
        String mrpText = "Rs. " + Objects.requireNonNull(product.get("MRP")).toString() + "/-";
        MRP.setText(mrpText);
        MRP.setPaintFlags(MRP.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        setProgressInRacingBars(findViewById(R.id.stars1), 1, product);
        setProgressInRacingBars(findViewById(R.id.stars2), 2, product);
        setProgressInRacingBars(findViewById(R.id.stars3), 3, product);
        setProgressInRacingBars(findViewById(R.id.stars4), 4, product);
        setProgressInRacingBars(findViewById(R.id.stars5), 5, product);

        TextView s1 = findViewById(R.id.num_of_1_star);
        s1.setText(product.get("1_star").toString());

        TextView s2 = findViewById(R.id.num_of_2_stars);
        s2.setText(product.get("2_star").toString());

        TextView s3 = findViewById(R.id.num_of_3_stars);
        s3.setText(product.get("3_star").toString());

        TextView s4 = findViewById(R.id.num_of_4_stars);
        s4.setText(product.get("4_star").toString());

        TextView s5 = findViewById(R.id.num_of_5_stars);
        s5.setText(product.get("5_star").toString());

        prev_rating = (int) ratingBar.getRating();
    }

    private String getAvg(DocumentSnapshot product) {
        float sum = 0, temp;
        for (int i = 1; i <= 5; i++) {
            temp = Integer.parseInt(Objects.requireNonNull(product.get(i + "_star")).toString());
            sum += i * temp;
            total += temp;
        }
        String average = "" + sum / total;
        if (average.length() > 3) average = average.substring(0, 3);
        String totS = "" + total;
        totalRatings.setText(totS);
        return average;
    }

    void setProgressInRacingBars(ProgressBar progressBar, int starNo, DocumentSnapshot product) {
        int stars = Integer.parseInt(Objects.requireNonNull(product.get(starNo + "_star")).toString());
        progressBar.setMax(total);
        progressBar.setProgress(stars);
    }

}
package com.example.intigritirobotics;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.Source;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.example.intigritirobotics.MainHomeActivity.currentUserUId;
import static com.example.intigritirobotics.MainHomeActivity.firebaseFirestore;
import static com.example.intigritirobotics.MainHomeActivity.loadingDialog;

public class ProductDetailActivity extends AppCompatActivity {

    private final List<SlideModel> slideModelList = new ArrayList<>();
    private String price, title, id, userPath;
    private int total, prev_rating, is_app_starting;
    public FirebaseFirestore firebaseFirestore;
    private TextView totalRatings, tvOutOfStock, imgAverageRating, imgTotalRating;
    private RatingBar ratingBar;
    private LinearLayout getHelpButton, addToCartButton;
    private List<ViewAllModel> horizontalList = new ArrayList<>();
    private LinearLayoutManager horizontalLinearLayoutManager;
    private RecyclerView RelatedProductRecyclerview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        Toolbar toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        
        Intent intent = getIntent();
        price = intent.getStringExtra("Price");
        title = intent.getStringExtra("Title");
        id = intent.getStringExtra("ID");
        firebaseFirestore = FirebaseFirestore.getInstance();
        tvOutOfStock = findViewById(R.id.tv_out_of_stock);
        ratingBar = (RatingBar) findViewById(R.id.rating_stars);
        totalRatings = (TextView) findViewById(R.id.number_of_rating_text);
        RelatedProductRecyclerview = findViewById(R.id.related_product_recyclerview);
        horizontalLinearLayoutManager = new LinearLayoutManager(this);
        imgAverageRating = findViewById(R.id.img_averageRatingText);
        imgTotalRating = findViewById(R.id.img_total_rating);
        getHelpButton = findViewById(R.id.pd_project_help_btn);
        addToCartButton = findViewById(R.id.addToCartButton);


        is_app_starting = 0;

        loadProductDetails();
        addToCartButton.setOnClickListener(view -> addItemToCart());

        ratingBar.setOnRatingBarChangeListener((ratingBar, v, b) -> updateRating(v));

        firebaseFirestore.document("USERS/" + currentUserUId + "/My Ratings/" + id).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot documentSnapshot = task.getResult();
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    ratingBar.setRating(Float.parseFloat(documentSnapshot.get("Rating").toString()));
                }
            }
        });

        getHelpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProductDetailActivity.this, ProjectPdfActivity.class);
                startActivity(intent);
            }
        });
    }

    public void updateRating(float v) {
        is_app_starting++;

        Map<String, String> userRatingMap = new HashMap<>();
        userRatingMap.put("Rating", "" + v);

        Map<String, Object> productUpdateMap = new HashMap<>();

        DocumentReference productRef = firebaseFirestore.document("PRODUCTS/" + id);

        productRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && is_app_starting > 1) {
                DocumentSnapshot prodSnap = task.getResult();
                assert prodSnap != null;
                String s = prodSnap.get((v + "").substring(0, 1) + "_star").toString();

                int x = s==null ? 0 : Integer.parseInt(s);

                firebaseFirestore
                        .document("USERS/"+currentUserUId+"/My Ratings/"+id)
                        .get()
                        .addOnCompleteListener(task1 -> {
                            if(task1.isSuccessful()) {

                                if(Objects.requireNonNull(task1.getResult()).exists()) {
                                    String prevRating = task1.getResult().get("Rating").toString();
                                    int pre_num = Integer.parseInt(prodSnap.get(prevRating.substring(0,1)+"_star").toString());


                                    productUpdateMap.put(prevRating.substring(0,1)+"_star", (pre_num-1)+"");
                                }

                                firebaseFirestore.document("USERS/" + currentUserUId + "/My Ratings/" + id).set(userRatingMap);
                                productUpdateMap.put((int)v+"_star", x+1+"");


                                productRef.update(productUpdateMap);
                            }
                        });

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
                            Toast.makeText(getApplicationContext(), "Already added to cart ;)", Toast.LENGTH_SHORT).show();
                        } else {
                            firebaseFirestore.collection("USERS")
                                    .document(currentUserUId)
                                    .collection("My Cart").document(id).set(map);
                            Toast.makeText(getApplicationContext(), "Added to cart :)", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getApplicationContext(), task.getException().toString(), Toast.LENGTH_SHORT).show();

                    }
                });

    }

    private void loadDataToProduct(DocumentSnapshot product) {

        if (product == null) {
            return;
        }
        String in_stock = Objects.requireNonNull(product.get("in stock")).toString();

        // In stock
        if (in_stock.equals("false")) {
            Button buyNow = findViewById(R.id.buyNowButton);
            buyNow.setEnabled(false);
            LinearLayout addToCart = findViewById(R.id.addToCartButton);
            addToCart.setEnabled(false);
            tvOutOfStock.setVisibility(View.VISIBLE);
        }

        ///////////////////////////////////////////////////////// Slider/////////////////////////////////////////////////////////////////
        ImageSlider imageSlider = findViewById(R.id.imgSlider);

        String[] productPicUrls = product.get("product_pic").toString().split(", ");

        for (String imgUrl : productPicUrls) slideModelList.add(new SlideModel(imgUrl, null));

        imageSlider.setImageList(slideModelList);

        TextView briefText = findViewById(R.id.itemBriefDetail);
        briefText.setText(title);
        ///////////////////////////////////////////////////////// Slider/////////////////////////////////////////////////////////////////


        ////////////////////////////////////////////////////////// Set Ratings//////////////////////////////////////////////////////////////
        TextView avgRatingText = findViewById(R.id.averageRatingText);
        avgRatingText.setText(getAvg(product));
        ////////////////////////////////////////////////////////// Set Ratings//////////////////////////////////////////////////////////////


        ///////////////////////////////////////////////////////////// Tabs//////////////////////////////////////////////////////////////////
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
        ///////////////////////////////////////////////////////////// Tabs//////////////////////////////////////////////////////////////////


        ///////////////////////////////////////////////////////////// Set Price/////////////////////////////////////////////////////////////////////////
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

        ///////////////////////////////////////////////////////////// Set Price/////////////////////////////////////////////////////////////////////////

    }

    public String getAvg(DocumentSnapshot product) {
        float sum = 0, temp;
        for (int i = 1; i <= 5; i++) {
            temp = Integer.parseInt(Objects.requireNonNull(product.get(i + "_star")).toString());
            sum += i * temp;
            total += temp;
        }
        String average = "" + sum / total;
        if (average.length() > 3) average = average.substring(0, 3);
        String totS = "" + total;
        totalRatings.setText("(" + totS + ") Rating");
        return average;
    }

    void setProgressInRacingBars(ProgressBar progressBar, int starNo, DocumentSnapshot product) {
        int stars = Integer.parseInt(Objects.requireNonNull(product.get(starNo + "_star")).toString());
        progressBar.setMax(total);
        progressBar.setProgress(stars);
    }

    private void loadRelatedProduct() {
        firebaseFirestore.collection("PRODUCTS").get().addOnCompleteListener(task2 -> {
            int i = 1;
            if (task2.isSuccessful()) {
                horizontalList.clear();

                for (QueryDocumentSnapshot documentSnapshot : task2.getResult()) {

                    String id = documentSnapshot.getId();
                    String picUrl = documentSnapshot.get("product_pic").toString().split(", ")[0];
                    String title = documentSnapshot.get("product title").toString();
                    float rating = Float.parseFloat(String.valueOf(documentSnapshot.get("product rating")));
                    int price = Integer.parseInt(String.valueOf(documentSnapshot.get("product price")));
                    horizontalList.add(new ViewAllModel(id, picUrl, title, rating, price));
                    i++;
                }
            } else {
                loadingDialog.dismiss();
                String error = task2.getException().getMessage();
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            loadingDialog.dismiss();

        });

        horizontalLinearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        RelatedProductRecyclerview.setLayoutManager(horizontalLinearLayoutManager);
        RelatedProductAdapter adapter1 = new RelatedProductAdapter(horizontalList);
        RelatedProductRecyclerview.setAdapter(adapter1);
        adapter1.notifyDataSetChanged();
    }

    @Override
    protected void onStart() {
        loadRelatedProduct();
        super.onStart();
    }
}
package com.example.intigritirobotics;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static com.example.intigritirobotics.MainHomeActivity.loadingDialog;

public class ViewAllActivity extends AppCompatActivity {

    private RecyclerView productRecycler;
    private LinearLayoutManager projectLinearLayoutManager;
    private List<ViewAllModel> productList = new ArrayList<>();
    private FirebaseFirestore firebaseFirestore;
    private List<ViewAllModel> recList = new ArrayList<>();
    private  String ToolbarTitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all);
        Toolbar toolbar = findViewById(R.id.vie_all_toolbar);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        ToolbarTitle= intent.getStringExtra("Title");
        getSupportActionBar().setTitle(ToolbarTitle);
        firebaseFirestore  = FirebaseFirestore.getInstance();
        productRecycler = findViewById(R.id.product_preview_recyclerview);
        projectLinearLayoutManager = new LinearLayoutManager(ViewAllActivity.this);
        loadProducts();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_home, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {

            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadProducts() {

        firebaseFirestore.collection("/CATEGORY/1AcKQNSDSQqnpvA5e4vN/products").get().addOnCompleteListener(task -> {

            if (task.isSuccessful()) {

                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {

                    int productId = Integer.parseInt(documentSnapshot.get("product_index").toString());

                    firebaseFirestore.collection("PRODUCTS").get().addOnCompleteListener(task1 -> {

                        if(task1.isSuccessful()) {

                            DocumentSnapshot products = task1.getResult().getDocuments().get(productId);

                            String id = products.get("id").toString();
                            String picUrl = products.get("product_pic").toString().split(", ")[0];
                            String title = products.get("product title").toString();
                            float rating = Float.parseFloat(String.valueOf(products.get("product rating")));
                            int price = Integer.parseInt(String.valueOf(products.get("product price")));

                            ViewAllModel prodModel = new ViewAllModel(id, picUrl, title, rating, price);

                            formList(prodModel);

//                            Log.d("Fet1", id);
//                            Log.d("Fet11", picUrl);
//                            Log.d("Fet11", title);
//                            Log.d("Fet11", ""+rating);
//                            Log.d("Fet11", ""+price);

                        } else {
                            Log.w("Fetch Product", "Product fetching failed");
                        }

                        for(ViewAllModel x: productList) {
                            Log.d("Fet", x.getImage());
                            Log.d("Fet", x.getId());
                            Log.d("Fet", x.getTitle());
                            Log.d("Fet", ""+x.getFinalPrice());
                            Log.d("Fet", ""+x.getTotalRating());
                        }

                    });

                }

                Log.d("Fet", "Product list size: "+productList.size());

            } else {
                loadingDialog.dismiss();
                String error = task.getException().getMessage();
                Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
            }

        });

        projectLinearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        productRecycler.setLayoutManager(projectLinearLayoutManager);
        ViewAllAdapter adapter = new ViewAllAdapter(productList);
        productRecycler.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        loadingDialog.dismiss();

//        Log.d("Fet", ""+recList.size());
//                productList.add(new ViewAllModel(
//                "product.get().toString()",
//                "https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcSvj_XtCdGmER6V7rg0CFvop3j1uTZ4yQ2vI3DyvVa7_nT862WFegNxBejZQMXS65ISN6ACb5U&usqp=CAc",
//                "Title ",
//                4.2F, 129));
    }
    private void formList(ViewAllModel prodModel) {
        recList.add(prodModel);
    }

}
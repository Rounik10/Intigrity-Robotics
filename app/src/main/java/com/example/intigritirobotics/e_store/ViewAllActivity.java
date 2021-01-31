package com.example.intigritirobotics.e_store;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.intigritirobotics.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ViewAllActivity extends AppCompatActivity {

    private RecyclerView productRecycler;
    private LinearLayoutManager projectLinearLayoutManager;
    private final List<ViewAllModel> productList = new ArrayList<>();
    private FirebaseFirestore firebaseFirestore;
    private String categoryId;
    private Dialog loadingDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all);
        loadingDialog = new Dialog(ViewAllActivity.this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.border_background));

        loadingDialog.show();

        Toolbar toolbar = findViewById(R.id.vie_all_toolbar);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        categoryId = intent.getStringExtra("Index");
        String toolbarTitle = intent.getStringExtra("Title");
        Log.d("Cat Id", categoryId);
        Objects.requireNonNull(getSupportActionBar()).setTitle(toolbarTitle);
        firebaseFirestore  = FirebaseFirestore.getInstance();
        productRecycler = findViewById(R.id.product_preview_recyclerview);
        projectLinearLayoutManager = new LinearLayoutManager(ViewAllActivity.this);
        loadProject();
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

    private void loadProject(){

        if ("1".equals(categoryId)) {//code to be executed;
            loadingDialog.dismiss();

            //optional
        } else {
            String docPath = "/CATEGORY/" + categoryId;

            firebaseFirestore.document(docPath).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot products = task.getResult();
                    List<String> productId = (List<String>) products.get("Product Id Array");

                    if (productId == null) {
                        loadingDialog.dismiss();
                        Toast.makeText(this, "No Products Available int this category", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    for (String prod : productId) {
                        String productPath = "/PRODUCTS/" + prod;
                        firebaseFirestore.document(productPath).get().addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                DocumentSnapshot documentSnapshot = task1.getResult();
                                assert documentSnapshot != null;
                                String id = documentSnapshot.getId();
                                String picUrl = Objects.requireNonNull(documentSnapshot.get("product_pic")).toString().split(", ")[0];
                                String title = Objects.requireNonNull(documentSnapshot.get("product title")).toString();
                                float rating = Float.parseFloat(String.valueOf(documentSnapshot.get("product rating")));
                                int price = Integer.parseInt(String.valueOf(documentSnapshot.get("product price")));
                                productList.add(new ViewAllModel(id, picUrl, title, rating, price));
                                projectLinearLayoutManager.setOrientation(RecyclerView.VERTICAL);
                                productRecycler.setLayoutManager(projectLinearLayoutManager);
                                ViewAllAdapter adapter1 = new ViewAllAdapter(productList);
                                productRecycler.setAdapter(adapter1);
                                adapter1.notifyDataSetChanged();
                                loadingDialog.dismiss();
                            }
                        }).addOnFailureListener(Throwable::printStackTrace);
                    }

                }
            });
        }


    }
}

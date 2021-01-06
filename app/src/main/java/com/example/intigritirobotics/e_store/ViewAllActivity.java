package com.example.intigritirobotics.e_store;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Switch;

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

public class ViewAllActivity extends AppCompatActivity {

    private RecyclerView productRecycler;
    private LinearLayoutManager projectLinearLayoutManager;
    private final List<ViewAllModel> productList = new ArrayList<>();
    private FirebaseFirestore firebaseFirestore;
    private List<ViewAllModel> recList;
    private String categoryId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all);
        Toolbar toolbar = findViewById(R.id.vie_all_toolbar);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        categoryId = intent.getStringExtra("Index");
        String toolbarTitle = intent.getStringExtra("Title");
        Log.d("Cat Id", categoryId);
        getSupportActionBar().setTitle(toolbarTitle);
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

        switch(categoryId){
            case "1":
                //code to be executed;
                break;  //optional

            default:
        String docPath = "/CATEGORY/"+categoryId;

        firebaseFirestore.document(docPath).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                DocumentSnapshot products = task.getResult();
                List<String> productId = (List<String>) products.get("Product Id Array");

                for(String prod : productId){
                    String productPath = "/PRODUCTS/"+ prod;
                    firebaseFirestore.document(productPath).get().addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task1.getResult();
                            String id = documentSnapshot.getId();
                            String picUrl = documentSnapshot.get("product_pic").toString().split(", ")[0];
                            String title = documentSnapshot.get("product title").toString();
                            float rating = Float.parseFloat(String.valueOf(documentSnapshot.get("product rating")));
                            int price = Integer.parseInt(String.valueOf(documentSnapshot.get("product price")));
                            productList.add(new ViewAllModel(id, picUrl, title, rating, price));
                            projectLinearLayoutManager.setOrientation(RecyclerView.VERTICAL);
                            productRecycler.setLayoutManager(projectLinearLayoutManager);
                            ViewAllAdapter adapter1 = new ViewAllAdapter(productList);
                            productRecycler.setAdapter(adapter1);
                            adapter1.notifyDataSetChanged();
                            MainHomeActivity.loadingDialog.dismiss();
                        }
                    }).addOnFailureListener(e -> Log.e("Fail",e.getMessage()));
                }

            }
        });
        }


    }
}

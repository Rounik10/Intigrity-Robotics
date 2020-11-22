package com.example.intigritirobotics;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import static com.example.intigritirobotics.MainHomeActivity.firebaseFirestore;
import static com.example.intigritirobotics.MainHomeActivity.loadingDialog;

public class ViewAllActivity extends AppCompatActivity {

    private RecyclerView productRecycler;
    private LinearLayoutManager projectLinearLayoutManager;
    private List<ViewAllModel> productList = new ArrayList<>();
    private FirebaseFirestore firebaseFirestore;
    private List<ViewAllModel> recList;
    private  String ToolbarTitle, categoryId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all);

        Toolbar toolbar = findViewById(R.id.vie_all_toolbar);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        categoryId = intent.getStringExtra("Index");
        ToolbarTitle= intent.getStringExtra("Title");
        Log.d("Cat Id", categoryId);
        getSupportActionBar().setTitle(ToolbarTitle);
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

        firebaseFirestore.collection("PRODUCTS").get().addOnCompleteListener(task1 -> {
            int i =1;
            if (task1.isSuccessful()) {
                for (QueryDocumentSnapshot documentSnapshot : task1.getResult()) {

                    String id = documentSnapshot.get("id").toString();
                    String picUrl = documentSnapshot.get("product_pic").toString().split(", ")[0];
                    String title = documentSnapshot.get("product title").toString();
                    float rating = Float.parseFloat(String.valueOf(documentSnapshot.get("product rating")));
                    int price = Integer.parseInt(String.valueOf(documentSnapshot.get("product price")));
                    productList.add(new ViewAllModel(id, picUrl, title, rating, price));
                    Toast.makeText(this, ""+i, Toast.LENGTH_SHORT).show();
                    i++;
                }
                projectLinearLayoutManager.setOrientation(RecyclerView.VERTICAL);
                productRecycler.setLayoutManager(projectLinearLayoutManager);
                ViewAllAdapter adapter1 = new ViewAllAdapter(productList);
                productRecycler.setAdapter(adapter1);
                adapter1.notifyDataSetChanged();
                loadingDialog.dismiss();
            }
        }).addOnFailureListener(e -> {

        });

    }
}

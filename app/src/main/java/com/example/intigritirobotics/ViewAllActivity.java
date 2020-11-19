package com.example.intigritirobotics;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import static com.example.intigritirobotics.MainHomeActivity.firebaseFirestore;
import static com.example.intigritirobotics.MainHomeActivity.loadingDialog;

public class ViewAllActivity extends AppCompatActivity {
    private List<ViewAllModel> productList = new ArrayList<>();
    private RecyclerView productRecycler;
    private LinearLayoutManager projectLinearLayoutManager;

    private FirebaseFirestore firebaseFirestore;
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
    private void loadProducts()
    {
        firebaseFirestore.collection("/CATEGORY/1AcKQNSDSQqnpvA5e4vN/products").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                productList.clear();
                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                    productList.add(new ViewAllModel(
                            documentSnapshot.get("id").toString(),
                            documentSnapshot.get("product_pic").toString().split(", ")[0],
                            documentSnapshot.get("product_title").toString(),
                            Float.parseFloat(String.valueOf(documentSnapshot.get("product_rating"))),
                            Integer.parseInt(String.valueOf(documentSnapshot.get("product_price")))
                    ));
                }
                
                projectLinearLayoutManager.setOrientation(RecyclerView.VERTICAL);
                productRecycler.setLayoutManager(projectLinearLayoutManager);
                ViewAllAdapter adapter = new ViewAllAdapter(productList);
                productRecycler.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                loadingDialog.dismiss();

            } else {
                loadingDialog.dismiss();
                String error = task.getException().getMessage();
                Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
            }

        });

    }
    

}
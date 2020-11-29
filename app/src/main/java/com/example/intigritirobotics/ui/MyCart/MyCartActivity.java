package com.example.intigritirobotics.ui.MyCart;

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

import com.example.intigritirobotics.MainHomeActivity;
import com.example.intigritirobotics.R;
import com.example.intigritirobotics.ViewAllAdapter;
import com.example.intigritirobotics.ViewAllModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import static com.example.intigritirobotics.MainHomeActivity.currentUserUId;
import static com.example.intigritirobotics.MainHomeActivity.firebaseFirestore;
import static com.example.intigritirobotics.MainHomeActivity.loadingDialog;

public class MyCartActivity extends AppCompatActivity {

    private RecyclerView cartItemRecycler;
    private LinearLayoutManager projectLinearLayoutManager;
    private List<ViewAllModel> productList = new ArrayList<>();
    private FirebaseFirestore firebaseFirestore;
    private List<ViewAllModel> recList;
    private String ToolbarTitle, categoryId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all);

        Toolbar toolbar = findViewById(R.id.vie_all_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(ToolbarTitle);
        firebaseFirestore  = FirebaseFirestore.getInstance();
        cartItemRecycler = findViewById(R.id.cart_recycler_view);
        projectLinearLayoutManager = new LinearLayoutManager(MyCartActivity.this);

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

        String colPath = "/USERS/"+currentUserUId+"/My Cart";




        firebaseFirestore.collection(colPath).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                QuerySnapshot q = task.getResult();

                for(DocumentSnapshot prodSnap : q.getDocuments()){
                    String productPath = "/PRODUCTS/"+ prodSnap.getId();
                    Log.d("Itt",productPath);

                    firebaseFirestore.document(productPath).get().addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            Log.d("Itt",task1.getResult().getId());
                            DocumentSnapshot documentSnapshot = task1.getResult();
                            String id = documentSnapshot.getId();
                            String picUrl = documentSnapshot.get("product_pic").toString().split(", ")[0];
                            String title = documentSnapshot.get("product title").toString();
                            float rating = Float.parseFloat(String.valueOf(documentSnapshot.get("product rating")));
                            int price = Integer.parseInt(String.valueOf(documentSnapshot.get("product price")));
                            productList.add(new ViewAllModel(id, picUrl, title, rating, price));

                            projectLinearLayoutManager.setOrientation(RecyclerView.VERTICAL);
                            cartItemRecycler.setLayoutManager(projectLinearLayoutManager);
                            MyCartAdapter adapter1 = new MyCartAdapter(productList);
                            cartItemRecycler.setAdapter(adapter1);
                            adapter1.notifyDataSetChanged();
                            MainHomeActivity.loadingDialog.dismiss();
                        }
                    }).addOnFailureListener(e -> {

                    });
                }

            }
        });

    }
}

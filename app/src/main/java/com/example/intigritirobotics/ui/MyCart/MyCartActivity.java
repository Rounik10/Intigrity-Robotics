package com.example.intigritirobotics.ui.MyCart;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.intigritirobotics.MainHomeActivity;
import com.example.intigritirobotics.R;
import com.example.intigritirobotics.ViewAllModel;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import static com.example.intigritirobotics.MainHomeActivity.currentUserUId;

public class MyCartActivity extends AppCompatActivity {

    private RecyclerView cartItemRecycler;
    private LinearLayoutManager projectLinearLayoutManager;
    private final List<ViewAllModel> productList = new ArrayList<>();
    private TextView totalPriceTextView;
    private FirebaseFirestore firebaseFirestore;
    private int totalPrice;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_cart);
        totalPriceTextView = findViewById(R.id.total_amount_number);
        Toolbar toolbar = findViewById(R.id.cart_toolbar);
        setSupportActionBar(toolbar);
        firebaseFirestore  = FirebaseFirestore.getInstance();
        cartItemRecycler = findViewById(R.id.cart_recycler_view);
        projectLinearLayoutManager = new LinearLayoutManager(MyCartActivity.this);
        Log.d("Total Price", "1"+totalPrice);
        loadProject();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_process_order, menu);
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

    private void loadProject() {

        String colPath = "/USERS/"+currentUserUId+"/My Cart";

        firebaseFirestore.collection(colPath).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                QuerySnapshot q = task.getResult();

                for(DocumentSnapshot prodSnap : Objects.requireNonNull(q).getDocuments()){
                    if(!prodSnap.exists()) break;
                    String productPath = "/PRODUCTS/"+ prodSnap.getId();

                    firebaseFirestore.document(productPath).get().addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            Log.d("Total Price", ""+totalPrice);
                            DocumentSnapshot documentSnapshot = task1.getResult();
                            String id = documentSnapshot.getId();
                            String picUrl = Objects.requireNonNull(documentSnapshot.get("product_pic")).toString().split(", ")[0];
                            String title = Objects.requireNonNull(documentSnapshot.get("product title")).toString();
                            float rating = Float.parseFloat(String.valueOf(documentSnapshot.get("product rating")));
                            int price = Integer.parseInt(String.valueOf(documentSnapshot.get("product price")));
                            totalPrice+= price;
                            productList.add(new ViewAllModel(id, picUrl, title, rating, price));
                            projectLinearLayoutManager.setOrientation(RecyclerView.VERTICAL);
                            cartItemRecycler.setLayoutManager(projectLinearLayoutManager);
                            MyCartAdapter adapter1 = new MyCartAdapter(productList);
                            cartItemRecycler.setAdapter(adapter1);
                            adapter1.notifyDataSetChanged();
                            MainHomeActivity.loadingDialog.dismiss();
                        }
                        totalPriceTextView.setText(""+totalPrice);
                    }
                    ).addOnFailureListener(e -> {

                    });
                }

            }
        });

    }

}
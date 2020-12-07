package com.example.intigritirobotics.ui.MyCart;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.intigritirobotics.CheckOutActivity;
import com.example.intigritirobotics.MainHomeActivity;
import com.example.intigritirobotics.R;
import com.example.intigritirobotics.ViewAllModel;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import static com.example.intigritirobotics.MainHomeActivity.currentUserUId;
import static com.example.intigritirobotics.MainHomeActivity.firebaseFirestore;

public class MyCartActivity extends AppCompatActivity {

    public static RecyclerView cartItemRecycler;
    public static LinearLayoutManager projectLinearLayoutManager;
    public static final List<ViewAllModel> productList = new ArrayList<>();
    public static TextView totalPriceTextView, deliveryPriceTextView, cartBottomTotal, cartTotal;
    public static FirebaseFirestore firebaseFirestore;
    public static int totalPrice;
    public static MyCartAdapter adapter1;
    public static  View v, text;
    private Button checkoutBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_cart);

        totalPriceTextView = findViewById(R.id.total_amount_number);
        deliveryPriceTextView = findViewById(R.id.delivery_cost_text);

        Toolbar toolbar = findViewById(R.id.cart_toolbar);
        setSupportActionBar(toolbar);

        firebaseFirestore  = FirebaseFirestore.getInstance();
        cartItemRecycler = findViewById(R.id.cart_recycler_view);
        cartBottomTotal = findViewById(R.id.cart_bottom_total_price);
        cartTotal = findViewById(R.id._total_price);
        projectLinearLayoutManager = new LinearLayoutManager(MyCartActivity.this);
        Log.d("Total Price", "1"+totalPrice);
         v = findViewById(R.id.include_product_price);
         text = findViewById(R.id.nothing_to_show);

        checkoutBtn = findViewById(R.id.cart_checkout_btn);

        loadProject();

        checkoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MyCartActivity.this, CheckOutActivity.class);
                myIntent.putExtra("Products cost",cartTotal.getText().toString());
                myIntent.putExtra("Delivery",deliveryPriceTextView.getText().toString());
                myIntent.putExtra("Total cost",totalPriceTextView.getText().toString());
                startActivity(myIntent);
            }

        });
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

    public static void loadProject() {

        String colPath = "/USERS/"+currentUserUId+"/My Cart";

        firebaseFirestore.collection(colPath).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                productList.clear();

                totalPrice = 0;
                QuerySnapshot q = task.getResult();
                assert q != null;
                if(q.size() == 0) {
                    v.setVisibility(View.GONE);
                    text.setVisibility(View.VISIBLE);
                }
                for(DocumentSnapshot prodSnap : Objects.requireNonNull(q).getDocuments()){
                    if(!prodSnap.exists()) break;
                    String productPath = "/PRODUCTS/"+ prodSnap.getId();

                    firebaseFirestore.document(productPath).get().addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task1.getResult();
                            assert documentSnapshot != null;
                            String id = documentSnapshot.getId();
                            String picUrl = Objects.requireNonNull(documentSnapshot.get("product_pic")).toString().split(", ")[0];
                            String title = Objects.requireNonNull(documentSnapshot.get("product title")).toString();
                            float rating = Float.parseFloat(String.valueOf(documentSnapshot.get("product rating")));
                            int price = Integer.parseInt(String.valueOf(documentSnapshot.get("product price")));

                            productList.add(new ViewAllModel(id, picUrl, title, rating, price, 1));
                            projectLinearLayoutManager.setOrientation(RecyclerView.VERTICAL);
                            cartItemRecycler.setLayoutManager(projectLinearLayoutManager);

                            adapter1 = new MyCartAdapter(productList);
                            adapter1.notifyDataSetChanged();
                            cartItemRecycler.setAdapter(adapter1);
                            adapter1.notifyDataSetChanged();
                            MainHomeActivity.loadingDialog.dismiss();

                            calculatePrice();
                        }
                    }
                    ).addOnFailureListener(e -> {

                    });
                }
            }
        });
    }

    static void  calculatePrice() {

        int total = 0;

        for(ViewAllModel product : productList) {
            int price = product.getFinalPrice();
            int quantity = product.getQuantity();
            total += price*quantity;
        }

        cartTotal.setText(total+"");

        String delPrice = (total>500)? "Free" : "Rs.60/-";
        if(!delPrice.equals("Free")) total += 60;

        deliveryPriceTextView.setText(delPrice);
        cartBottomTotal.setText(total+"");
        totalPriceTextView.setText(total+"");

    }

    public static void deleteItem(View itemView, String index, int price) {

        firebaseFirestore
                .document("USERS/"+currentUserUId+"/My Cart/"+index).delete()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        itemView.setVisibility(View.GONE);
                        Toast.makeText(itemView.getContext(),"Item Deleted", Toast.LENGTH_SHORT).show();
                        loadProject();
                    } else {
                        Toast.makeText(itemView.getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                });

        totalPriceTextView.setText("Rs."+price+"/-");
    }

}
package com.example.intigritirobotics;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentSnapshot;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import static com.example.intigritirobotics.MainHomeActivity.firebaseFirestore;

public class MyOrderDetailActivity extends AppCompatActivity {

    private static final String TAG = "Debug: MyOrderDetail";
    private String orderId;
    private String[] productQty, productId, productPrices;
    private TextView totalPriceText, deliveryCostText, total_amount_number;
    RecyclerView orderRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_order_detail);
        Intent myIntent = getIntent();

        orderId = myIntent.getStringExtra("order id");

        productId =  Objects.requireNonNull(myIntent.getStringExtra("productId")).split(", ");
        productQty = Objects.requireNonNull(myIntent.getStringExtra("product qty")).split(", ");
        productPrices = Objects.requireNonNull(myIntent.getStringExtra("product price")).split(", ");

        totalPriceText = findViewById(R.id._total_price);
        deliveryCostText = findViewById(R.id.delivery_cost_text);
        total_amount_number = findViewById(R.id.total_amount_number);
        Button toPdfAct = findViewById(R.id.pdf_act_button);

        setRecycler();
        setPrices();

        toPdfAct.setOnClickListener(l->{
            Intent intent = new Intent(this, ProjectPdfActivity.class);
            intent.putExtra("orderId", orderId);
            startActivity(intent);
        });

    }

    private void setPrices() {

        int totalPrice = 0;

        for(int i=0; i<productQty.length; i++) {
            totalPrice += Integer.parseInt(productQty[i]) * Integer.parseInt(productPrices[i]);
        }

        String delivery = totalPrice < 500 ? "Rs.60/-": "Free";
        String totalAmount = totalPrice < 500 ? "Rs." + (totalPrice + 60) +"/-" : "Rs." + totalPrice + "/-";
        String totalPriceString = "Rs."+totalPrice+"/-";
        
        totalPriceText.setText(totalPriceString);
        deliveryCostText.setText(delivery);
        total_amount_number.setText(totalAmount);

    }

    private void setRecycler() {

        List<OrderDetailItemsModel> orderDetailItemsModelList = new ArrayList<>();

        for(int i=0; i<productId.length; i++) {

            String prodId = productId[i];
            String prodPrice = productPrices[i];
            String prodQty = productQty[i];

            firebaseFirestore
                    .document("PRODUCTS/"+productId[i])
                    .get()
                    .addOnCompleteListener(task -> {

                if(task.isSuccessful()) {

                    DocumentSnapshot product =  task.getResult();
                    int total = 0;
                    float sum = 0, temp;
                    for (int j = 1; j <= 5; j++) {
                        assert product != null;
                        temp = Integer.parseInt(Objects.requireNonNull(product.get(j + "_star")).toString());
                        sum += j * temp;
                        total += temp;
                    }
                    String average = "" + sum / total;
                    if (average.length() > 3) average = average.substring(0, 3);

                    orderDetailItemsModelList.add(new OrderDetailItemsModel(prodId, prodPrice, prodQty, average));

                    Log.d(TAG, average);

                    orderRecycler = findViewById(R.id.order_detail_recyclerView);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
                    orderRecycler.setLayoutManager(linearLayoutManager);

                    OrderDetailItemAdapter orderDetailItemAdapter = new OrderDetailItemAdapter(orderDetailItemsModelList);
                    orderRecycler.setAdapter(orderDetailItemAdapter);

                }

            });

        }

    }

}
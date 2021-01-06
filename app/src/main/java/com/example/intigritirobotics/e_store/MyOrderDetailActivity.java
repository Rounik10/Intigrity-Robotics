package com.example.intigritirobotics.e_store;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.example.intigritirobotics.R;
import com.google.firebase.firestore.DocumentSnapshot;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.example.intigritirobotics.e_store.MainHomeActivity.TheUser;
import static com.example.intigritirobotics.e_store.MainHomeActivity.firebaseFirestore;

public class MyOrderDetailActivity extends AppCompatActivity {

    private static final String TAG = "Debug: MyOrderDetail";
    private String orderId;
    private String[] productQty, productId, productPrices;
    private TextView totalPriceText, deliveryCostText, total_amount_number;
    private TextView name, address, phone, pin, paymentMethod, paymentId;
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

        name = findViewById(R.id.address_view_name);
        phone = findViewById(R.id.address_view_mobile);
        address = findViewById(R.id.address_view_street_area);
        pin = findViewById(R.id.address_view_state_pin_code);
        paymentMethod = findViewById(R.id.payment_method);
        paymentId = findViewById(R.id.payment_id);

        setRecycler();
        setPrices();
        setDetails();

        toPdfAct.setOnClickListener(l->{
            Intent intent = new Intent(this, ProjectPdfActivity.class);
            intent.putExtra("orderId", orderId);
            startActivity(intent);
        });

    }

    private void setDetails() {

        firebaseFirestore.document("ORDERS/"+orderId).get().addOnSuccessListener(order ->{

            address.setText(Objects.requireNonNull(order.get("shipping address")).toString());
            name.setText(Objects.requireNonNull(TheUser.name));
            phone.setText(Objects.requireNonNull(order.get("phone no")).toString());
            pin.setText(Objects.requireNonNull(order.get("PIN")).toString());
            paymentMethod.setText(Objects.requireNonNull(order.get("payment method")).toString());
            paymentId.setText(Objects.requireNonNull(order.get("payment id")).toString());

        }).addOnFailureListener(Throwable::printStackTrace);

    }

    private void setPrices() {

        int totalPrice = 0;

        for(int i=0; i<productQty.length; i++) {
            totalPrice += Integer.parseInt(productQty[i]) * Integer.parseInt(productPrices[i]);
        }

        String delivery = totalPrice < 500 ? "Rs.60/-": "Free";
        String totalAmount = totalPrice < 500 ? ""+(totalPrice + 60)  : ""+totalPrice;
        String totalPriceString = ""+totalPrice;
        
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
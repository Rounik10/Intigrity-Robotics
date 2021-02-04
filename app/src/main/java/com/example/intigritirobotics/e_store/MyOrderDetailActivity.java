package com.example.intigritirobotics.e_store;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.example.intigritirobotics.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.example.intigritirobotics.e_store.MainHomeActivity.TheUser;

public class MyOrderDetailActivity extends AppCompatActivity {

    private static final String TAG = "Debug: MyOrderDetail";
    private String orderId;
    private String[] productQty, productId, productPrices;
    private TextView totalPriceText, deliveryCostText, total_amount_number;
    private TextView name, address, phone, pin, paymentMethod, paymentId;
    private int discount;
    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    RecyclerView orderRecycler;
    private Dialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_my_order_detail);
        loadingDialog = new Dialog(MyOrderDetailActivity.this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.border_background));

        loadingDialog.show();

        totalPriceText = findViewById(R.id._total_price);
        deliveryCostText = findViewById(R.id.delivery_cost_text);
        total_amount_number = findViewById(R.id.total_amount_number);
        Button toPdfAct = findViewById(R.id.pdf_act_button);

        gtExtra();

        name = findViewById(R.id.address_view_name);
        phone = findViewById(R.id.address_view_mobile);
        address = findViewById(R.id.address_view_street_area);
        pin = findViewById(R.id.address_view_state_pin_code);
        paymentMethod = findViewById(R.id.payment_method);
        paymentId = findViewById(R.id.payment_id);



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
            loadingDialog.dismiss();
            discount = Integer.parseInt(Objects.requireNonNull(order.get("offer discount")).toString());

        }).addOnFailureListener(Throwable::printStackTrace);

    }

    private void setPrices() {

        int totalPrice = 0;

        for(int i=0; i<productQty.length; i++) {
            totalPrice += Integer.parseInt(productQty[i]) * Integer.parseInt(productPrices[i]);
        }

        totalPrice -= discount;

        String delivery = totalPrice < 500 ? "Rs.60/-": "Free";
        String totalAmount = totalPrice < 500 ? ""+(totalPrice + 60)  : ""+totalPrice;
        String totalPriceString = ""+totalPrice;

        Log.d(TAG,">>>1"+(totalPriceText == null) );
        Log.d(TAG,">>>2"+(deliveryCostText == null) );
        Log.d(TAG,">>>3"+(total_amount_number == null) );

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
    private void  gtExtra()
    {
        Intent myIntent = getIntent();
        orderId = myIntent.getStringExtra("order id");


        if (myIntent.getStringExtra("from notification").equals("true"))
        {
            firebaseFirestore.document("ORDERS/"+orderId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful())
                    {
                        DocumentSnapshot order =  task.getResult();
                        productId = Objects.requireNonNull(order.get("productQsIds").toString()).split(", ");
                        productQty = Objects.requireNonNull(order.get("productQty").toString()).split(", ");
                        productPrices = Objects.requireNonNull(order.get("productPrice").toString()).split(", ");
                        setRecycler();
                        setPrices();
                        setDetails();
                    }
                }
            }).addOnFailureListener(Throwable::printStackTrace);

        }
        else {

            productId = Objects.requireNonNull(myIntent.getStringExtra("productId")).split(", ");
            productQty = Objects.requireNonNull(myIntent.getStringExtra("product qty")).split(", ");
            productPrices = Objects.requireNonNull(myIntent.getStringExtra("product price")).split(", ");
            setRecycler();
            setPrices();
            setDetails();
        }

    }

    @Override
    public void onBackPressed() {
        Intent myIntent = getIntent();
        if (myIntent.getStringExtra("from notification").equals("true"))
        {
            Intent intent = new Intent(MyOrderDetailActivity.this, MainHomeActivity.class);
            startActivity(intent);
        }
        finish();

        super.onBackPressed();
    }
}
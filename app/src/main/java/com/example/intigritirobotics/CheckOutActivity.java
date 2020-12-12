package com.example.intigritirobotics;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.intigritirobotics.MainHomeActivity.currentUserUId;
import static com.example.intigritirobotics.MainHomeActivity.firebaseFirestore;
import static com.example.intigritirobotics.ui.MyCart.MyCartActivity.productList;

public class CheckOutActivity extends AppCompatActivity implements PaymentResultListener {
    private TextView totalPriceTextView, deliveryPriceTextView, cartBottomTotal, cartTotal;
    final int UPI_PAYMENT = 0;
    private Button checkoutPayBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Checkout.preload(this);

        setContentView(R.layout.activity_check_out);
        Intent intent = getIntent();
        totalPriceTextView = findViewById(R.id.total_amount_number);
        deliveryPriceTextView = findViewById(R.id.delivery_cost_text);
        cartTotal = findViewById(R.id._total_price);

        cartTotal.setText(intent.getStringExtra("Products cost"));
        deliveryPriceTextView.setText(intent.getStringExtra("Delivery"));
        totalPriceTextView.setText(intent.getStringExtra("Total cost"));
        checkoutPayBtn = findViewById(R.id.checkout_pay_button);


        checkoutPayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPayment();
            }
        });

    }

    private void loadProductsToMyOrders() {

        Map<String , Object> map = new HashMap<>();

        StringBuilder idStr = new StringBuilder();
        StringBuilder qtyStr = new StringBuilder();

        for(ViewAllModel product: productList) {
            idStr.append(product.getId()).append(", ");
            qtyStr.append(product.getQuantity()).append(", ");
        }

        map.put("productQsIds", idStr.toString());
        map.put("productQty", qtyStr.toString());

        CollectionReference collRef = firebaseFirestore.collection("/USERS/"+ currentUserUId + "/My Orders");
        collRef.add(map).addOnCompleteListener(task -> {
            if(task.isSuccessful()) {

                firebaseFirestore.collection("/USERS/"+ currentUserUId + "/My Cart")
                        .get()
                        .addOnCompleteListener(task1 -> {
                            List<DocumentSnapshot> l = task1.getResult().getDocuments();
                            for(int i=0; i<l.size(); i++) {
                                firebaseFirestore
                                        .document("USERS/"+currentUserUId+"/My Cart/"+l.get(i).getId()).delete();
                                Log.d("dikkat", l.get(i).getId());
                            }
                });
                productList.clear();

                Intent intent = new Intent(this, MainHomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("EXIT", true);
                startActivity(intent);

            } else {
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public static boolean isConnectionAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()
                    && netInfo.isConnectedOrConnecting()
                    && netInfo.isAvailable()) {
                return true;
            }
        }
        return false;
    }

    public void startPayment() {
        /**
         * Instantiate Checkout
         */
        Checkout checkout = new Checkout();

        /**
         * Reference to current activity
         */
        final Activity activity = this;

        /**
         * Pass your payment options to the Razorpay Checkout as a JSONObject
         */
        try {
            JSONObject options = new JSONObject();

            options.put("name", "Merchant Name");
            options.put("description", "Test Order");
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");

            options.put("theme.color", "#3399cc");
            options.put("currency", "INR");
            options.put("amount", "100");//pass amount in currency subunits
            options.put("prefill.email", "rpgamerindia@gmail.com");
            options.put("prefill.contact","6299022603");

            checkout.open(activity, options);
        } catch(Exception e) {
            Log.e("Payment Failed", "Error in starting Razorpay Checkout", e);
        }
    }

    @Override
    public void onPaymentSuccess(String s) {

        Toast.makeText(this, "Order Successful", Toast.LENGTH_SHORT).show();
        loadProductsToMyOrders();


    }

    @Override
    public void onPaymentError(int i, String s) {
        Log.d("dikkat", s);
        Toast.makeText(this, "Payment Error"+s, Toast.LENGTH_SHORT).show();
    }
}

class product {
    String name;
    int qty;
    product(String name, int qty) {
        this.name = name;
        this.qty = qty;
    }
}


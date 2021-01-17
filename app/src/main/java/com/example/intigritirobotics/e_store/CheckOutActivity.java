package com.example.intigritirobotics.e_store;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.text.Editable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.intigritirobotics.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import static com.example.intigritirobotics.e_store.MainHomeActivity.TheUser;
import static com.example.intigritirobotics.e_store.MainHomeActivity.currentUserUId;
import static com.example.intigritirobotics.e_store.MainHomeActivity.firebaseFirestore;
import static com.example.intigritirobotics.e_store.ui.MyCart.MyCartActivity.productList;

public class CheckOutActivity extends AppCompatActivity implements PaymentResultListener {
    private static final String TAG = "CheckOutActivity: ";
    private TextView totalPriceTextView, deliveryPriceTextView, cartTotal;
    private Button checkoutPayBtn, couponApplyBtn;
    private TextView consumerName, shippingAddress, mobileNo, pinCode;
    private String orderId, date, address;
    private String paymentMethod;
    private Intent intent;
    private List<ViewAllModel> ProdList;
    private String intentFrom;
    private RadioButton onlinePayment;
    private String paymentId;
    private EditText codeInp;
    private int discount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Checkout.preload(this);
        setContentView(R.layout.activity_check_out);
        intent = getIntent();
        
        intentFrom = intent.getStringExtra("from");
        address = TheUser.getAddress();
        onlinePayment = findViewById(R.id.online_method);

        totalPriceTextView = findViewById(R.id.total_amount_number);
        deliveryPriceTextView = findViewById(R.id.delivery_cost_text);
        cartTotal = findViewById(R.id._total_price);

        codeInp = findViewById(R.id.code_input);

        consumerName = findViewById(R.id.address_view_name);
        shippingAddress = findViewById(R.id.address_view_street_area);
        mobileNo = findViewById(R.id.address_view_mobile);
        pinCode = findViewById(R.id.address_view_state_pin_code);
        Button updateAddressBtn = findViewById(R.id.address_view_update_address);
        couponApplyBtn = findViewById(R.id.code_apply_btn);

        setData();
        checkoutPayBtn.setOnClickListener(v -> startPayment());
        updateAddressBtn.setOnClickListener(v -> startActivity(new Intent(this, UpdateUserDetails.class)));
        couponApplyBtn.setOnClickListener(v-> new AlertDialog.Builder(this)
                .setTitle("Confirm")
                .setMessage("Do you really want to use your coupon, you can't use it again !")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> applyCoupon())
                .setNegativeButton(android.R.string.no, null).show());

        codeInp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                couponApplyBtn.setEnabled(editable.length() > 5);
            }
        });
    }

    private void applyCoupon() {

        firebaseFirestore.collection("OFFERS").get().addOnSuccessListener(task->{
            boolean codeExists = false;
            boolean expired = false;
            String[] val;
            for(DocumentSnapshot offer : task.getDocuments()) {
                String code = offer.get("Id").toString();
                if(code.equals(codeInp.getText().toString())) {
                    codeExists = true;
                    if(offer.get("Expired").toString().equals("true")) {
                        expired = true;
                        break;
                    }

                    int newPrice = Integer.parseInt(totalPriceTextView.getText().toString());

                    val = offer.get("value").toString().split(" ");
                    if(val[0].equals("1")) {
                        String delCharge = deliveryPriceTextView.getText().toString();
                        if(delCharge.equals("Free")) {
                            Toast.makeText(this, "Delivery is Already free for this Order!", Toast.LENGTH_SHORT).show();
                        } else {
                            newPrice-=60;
                            discount = 60;
                            deliveryPriceTextView.setText(R.string.free);
                        }
                    } else if(val[0].equals("2")) {
                        if(newPrice < Integer.parseInt(val[2])) {
                            Toast.makeText(this, "Total is too low", Toast.LENGTH_SHORT).show();
                            break;
                        }
                        discount = Integer.parseInt(val[2]);
                        newPrice-= discount;
                    } else {
                        discount = Math.min(newPrice / 2, 200);
                        newPrice-=discount;
                    }

                    String price = ""+newPrice;
                    totalPriceTextView.setText(price);

                    Map<String, Object> map = new HashMap<>();
                    map.put("Expired", true);

                    firebaseFirestore.document("OFFERS/"+offer.getId()).update(map);
                }
            }
            if(expired) Toast.makeText(this, "Code is Expired", Toast.LENGTH_SHORT).show();
            else if(!codeExists) Toast.makeText(this, "Code is Incorrect!", Toast.LENGTH_SHORT).show();
        });

    }

    private void setData() {

        if(!intentFrom.equals("ProductDetailActivity")) {
            ProdList = productList;
        } else {
            ProdList = new ArrayList<>();
            String id = intent.getStringExtra("id");
            String image = intent.getStringExtra("image url");
            String title = intent.getStringExtra("title");
            float totalRating = intent.getFloatExtra("total rating", 0);
            int finalPrice = intent.getIntExtra("final price", 0);
            ViewAllModel product = new ViewAllModel(id, image, title, totalRating, finalPrice,1);
            ProdList.add(product);
        }

        cartTotal.setText(intent.getStringExtra("Products cost"));
        deliveryPriceTextView.setText(intent.getStringExtra("Delivery"));

        totalPriceTextView.setText(intent.getStringExtra("Total cost"));
        checkoutPayBtn = findViewById(R.id.checkout_pay_button);
        pinCode.setText(TheUser.pin);

        consumerName.setText(TheUser.name);
        shippingAddress.setText(address);
        mobileNo.setText(TheUser.phoneNumber);

    }

    private void loadProductsToMyOrders() {

        Map<String , Object> map = new HashMap<>();

        StringBuilder idStr = new StringBuilder();
        StringBuilder qtyStr = new StringBuilder();
        StringBuilder priceStr = new StringBuilder();

        for(ViewAllModel product: ProdList) {
            idStr.append(product.getId()).append(", ");
            qtyStr.append(product.getQuantity()).append(", ");
            priceStr.append(product.getFinalPrice()).append(", ");
        }

        date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        CollectionReference collRef = firebaseFirestore.collection("ORDERS");

        collRef.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                int totalOrders = Objects.requireNonNull(task.getResult()).size() + 1;

                orderId = "IR-00000" + totalOrders;

                map.put("productQsIds", idStr.toString());
                map.put("productQty", qtyStr.toString());
                map.put("productPrice", priceStr.toString());
                map.put("order by", currentUserUId);
                map.put("order date", date);
                map.put("offer discount", discount);
                map.put("order status", "Order Placed");
                map.put("invoice token", "x");
                map.put("shipping address", address);
                map.put("phone no", TheUser.phoneNumber);
                map.put("PIN", TheUser.pin);
                map.put("payment method", paymentMethod);
                map.put("payment id", paymentId);

                Map<String, Object> m2 = new HashMap<>();
                m2.put("order Id", orderId);

                firebaseFirestore.collection("/USERS/"+ currentUserUId +"/My Orders").add(m2);

                collRef.document(orderId).set(map).addOnCompleteListener(task1 -> {

                    if(task.isSuccessful()) {
                        uploadPdf(ProdList);
                        firebaseFirestore.collection("/USERS/"+ currentUserUId + "/My Cart")
                                .get()
                                .addOnCompleteListener(task2 -> {
                                    List<DocumentSnapshot> l = Objects.requireNonNull(task2.getResult()).getDocuments();
                                    for(int i=0; i<l.size(); i++) {
                                        firebaseFirestore
                                                .document("USERS/"+currentUserUId+"/My Cart/"+l.get(i).getId()).delete();
                                        Log.d(TAG, l.get(i).getId());
                                    }
                                });

                    } else {
                        Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    void exit() {
        ProdList.clear();

        Intent intent = new Intent(this, MainHomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("EXIT", true);
        startActivity(intent);
    }

    private void uploadPdf(List<ViewAllModel> ProdList) {

        PdfDocument pdfDocument = new PdfDocument();
        Paint paint = new Paint();

        Bitmap bitmap = Bitmap.createBitmap(1000, 1300, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);

        paint.setColor(Color.WHITE);
        canvas.drawRect(0,0, canvas.getWidth(), canvas.getHeight(), paint);
        paint.setColor(Color.BLACK);

        paint.setTextSize(80);
        canvas.drawText("Intigriti Robotics", 30, 80, paint);

        paint.setTextAlign(Paint.Align.RIGHT);

        paint.setTextSize(30);
        canvas.drawText("Invoice Id", canvas.getWidth()-40, 40, paint);
        canvas.drawText(orderId, canvas.getWidth()-40, 80, paint);

        paint.setTextAlign(Paint.Align.LEFT);

        paint.setColor(Color.rgb(150, 150, 150));
        canvas.drawRect(30, 130, canvas.getWidth()-40, 135, paint);

        paint.setColor(Color.BLACK);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

        canvas.drawText("Order date: "+ date, 50, 200, paint);
        canvas.drawText("Shipping Address: ", 50, 250, paint);

        paint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText("Sold By: ", 665, 250,paint);

        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));

        TextPaint mTextPaint=new TextPaint();

        mTextPaint.setTextSize(28);

        StaticLayout mTextLayout = new StaticLayout(address, mTextPaint, canvas.getWidth()/2-50, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);

        canvas.save();

        canvas.translate(50, 270);
        mTextLayout.draw(canvas);

        int t1 = mTextLayout.getHeight();
        canvas.restore();

        canvas.save();
        mTextLayout = new StaticLayout("soldBy", mTextPaint, canvas.getWidth()/2-50, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        canvas.translate(550,270);
        mTextLayout.draw(canvas);

        int t2 = mTextLayout.getHeight();
        canvas.restore();

        int row = 270 + Math.max(t1,t2);

        paint.setColor(Color.rgb(150, 150, 150));
        canvas.drawRect(30, row+30, 280, row+80, paint);

        paint.setColor(Color.WHITE);
        canvas.drawText("BILL TO", 50, row + 65, paint);

        paint.setColor(Color.BLACK);
        canvas.drawText("Consumer Name: ", 30 , row + 120, paint);
        canvas.drawText(TheUser.name, 280 , row + 120, paint);

        canvas.drawText("Contact No: ", 550 , row + 120, paint);
        canvas.drawText(TheUser.phoneNumber, 720 , row + 120, paint);

        paint.setColor(Color.rgb(150, 150, 150));
        canvas.drawRect(30, row + 150, canvas.getWidth()-30, row + 200, paint);

        paint.setColor(Color.WHITE);

        int low = row + 250;

        canvas.drawText("S/No.", 50, row + 185, paint);
        canvas.drawText("Items", 300, row + 185, paint);
        canvas.drawText("Qty", 550, row + 185, paint);

        paint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText("Amount", canvas.getWidth()-40, row+185, paint);
        paint.setTextAlign(Paint.Align.LEFT);

        paint.setColor(Color.BLACK);

        int s_no = 0;
        int sum = 0;

        for(ViewAllModel product: ProdList) {

            sum += product.getFinalPrice() * product.getQuantity();

            paint.setTextAlign(Paint.Align.LEFT);

            String title = product.getTitle();
            if(title.length()>20) title = title.substring(0,20)+"...";

            canvas.drawText(""+ ++s_no, 60, low, paint);
            canvas.drawText(title, 160, low, paint);
            canvas.drawText(""+product.getQuantity(), 550 ,low, paint);

            paint.setTextAlign(Paint.Align.RIGHT);
            canvas.drawText(product.getQuantity() + " x " + (int)(0.82 * product.getFinalPrice()), canvas.getWidth() - 40, low, paint);
            paint.setTextAlign(Paint.Align.LEFT);

            low += 50;
        }

        paint.setColor(Color.rgb(150,150, 150));
        canvas.drawRect(30, low+50, canvas.getWidth()-40, low+55, paint);

        paint.setColor(Color.BLACK);
        low += 100;

        canvas.drawText("SUBTOTAL", 550, low, paint);
        canvas.drawText("GST 18%", 550, low+50, paint);
        canvas.drawText(paymentMethod, 280, low, paint);

        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText("TOTAL", 550, low+100, paint);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));

        paint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText("Rs." + 0.82 * sum + "/-", canvas.getWidth()-40, low, paint);
        canvas.drawText( "Rs."+0.18 * sum+"/-", canvas.getWidth()-40, low+50, paint);

        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText("Payment Method: ",270, low, paint);
        canvas.drawText( "Rs."+(0.18 * sum + 0.82*sum )+"/-", canvas.getWidth()-40, low+100, paint);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));

        uploadBitmap(bitmap);

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(canvas.getWidth(),low+250,1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);

        page.getCanvas().drawBitmap(bitmap, 0, 0, paint);
        pdfDocument.finishPage(page);

    }

    private void uploadBitmap(Bitmap bitmap) {
        FirebaseStorage firebaseStorage= FirebaseStorage.getInstance();

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] data = byteArrayOutputStream.toByteArray();

        UploadTask uploadTask = firebaseStorage.getReference("invoices").child(orderId).putBytes(data);

        uploadTask.addOnSuccessListener(taskSnapshot -> {
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            StorageReference dateRef = storageRef.child("invoices").child(orderId);
            Toast.makeText(CheckOutActivity.this,dateRef.toString(),Toast.LENGTH_LONG).show();

            dateRef.getDownloadUrl().addOnSuccessListener(downloadUrl -> firebaseFirestore
                    .collection("ORDERS")
                    .document(orderId)
                    .update("invoice token",downloadUrl.toString()));
            exit();
        }).addOnFailureListener(e -> {
            Log.d("File Upload", "Invoice upload failed");
            e.printStackTrace();
        }).addOnProgressListener(snapshot -> {
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.show();
        });

    }

    public void startPayment() {
        if (onlinePayment.isChecked()) {
            Checkout checkout = new Checkout();
            final Activity activity = this;

            try {
                JSONObject options = new JSONObject();
                options.put("name", "Merchant Name");
                options.put("description", "Test Order");
                options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
                options.put("theme.color", "#3399cc");
                options.put("currency", "INR");
                options.put("amount", "100");//pass amount in currency subunits
                options.put("prefill.email", "rpgamerindia@gmail.com");
                options.put("prefill.contact", "6299022603");

                checkout.open(activity, options);
            } catch (Exception e) {
                Log.e("Payment Failed", "Error in starting Razorpay Checkout", e);
            }
        }
    }

    @Override
    public void onPaymentSuccess(String s) {

        Toast.makeText(this, "Order Successful", Toast.LENGTH_SHORT).show();

        paymentMethod = "UPI";

        Log.d("payment id",s);

        paymentId = s;
        loadProductsToMyOrders();

    }

    @Override
    public void onPaymentError(int i, String s) {
        Log.d(TAG, s);
        Toast.makeText(this, "Payment Error"+s, Toast.LENGTH_SHORT).show();
    }
}

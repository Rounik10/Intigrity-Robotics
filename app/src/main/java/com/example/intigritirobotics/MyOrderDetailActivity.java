package com.example.intigritirobotics;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import static com.example.intigritirobotics.MainHomeActivity.TheUser;
import static com.example.intigritirobotics.MainHomeActivity.currentUserUId;
import static com.example.intigritirobotics.MainHomeActivity.firebaseFirestore;

public class MyOrderDetailActivity extends AppCompatActivity {

    private static final String TAG = "Dikkat: MyOrderDetail";
    private SQLiteDatabase sqLiteDatabase;
    private String orderId;
    private String date;
    private String address, soldBy, payment, status;
    TextView randomTest;
    private String productQty[], productId[], productPrices[];
    RecyclerView orderRecycler;
    List<Product> prodList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_order_detail);
        Intent myIntent = getIntent();

        date = myIntent.getStringExtra("date");
        orderId = myIntent.getStringExtra("order id");
        date = myIntent.getStringExtra("date");
        status = myIntent.getStringExtra("status");
        String productIdStr = myIntent.getStringExtra("productId");
        String productQtyStr = myIntent.getStringExtra("product qty");
        String productPricesStr = myIntent.getStringExtra("product price");

        productId = productIdStr.split(", ");
        productQty = productQtyStr.split(", ");
        productPrices = productPricesStr.split(", ");
        setRecycler();

        date = Calendar.getInstance().getTime().toString();

        //randomTest = findViewById(R.id.textView16);

        PdfHelper pdfHelper = new PdfHelper(this);
        sqLiteDatabase = pdfHelper.getWritableDatabase();
        payment = "UPI";

        Button toPdfAct = findViewById(R.id.pdf_act_button);
        toPdfAct.setOnClickListener(l->{
            Intent intent = new Intent(this, ProjectPdfActivity.class);
            intent.putExtra("orderId", orderId);
            intent.putExtra("payment", payment);
            intent.putExtra("address",address);
            intent.putExtra("soldBy",soldBy);
            intent.putExtra("date",date);
            intent.putExtra("productId", productIdStr);
            intent.putExtra("productQty", productQtyStr);
            intent.putExtra("productPrices", productPricesStr);
            startActivity(intent);
        });

        prodList = new ArrayList<>();
/*
        for(int i=0; i< productId.length; i++) {
            String prodId = productId[i];
            int prodQty = Integer.parseInt(productQty[i]);
            int prodPrice = Integer.parseInt(productPrices[i]);

            FirebaseFirestore.getInstance().document("PRODUCTS/" + prodId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()) {
                            DocumentSnapshot productSnap = task.getResult();
                            String title = productSnap.get("product title").toString();
                            prodList.add(new MyOrderDetailActivity.Product(title, prodQty, prodPrice));

                            address = MainHomeActivity.TheUser.address;
                            orderId = "#1001";
                            soldBy = "Gamotech * SURVEY NO. 38/2, 39 AND 40, JADIGENAHALLI HOBLI,KACHARAKANAHALLI VILLAGE, HOSAKOTE TALUK, Bengaluru (Bangalore) Urban Bangalore, Karnataka, 562114 IN";

                            saveInvoiceAsPDF();
                        }
                    });
        }
*/
    }
/*
    public void saveInvoiceAsPDF() {

        PdfDocument pdfDocument = new PdfDocument();
        Paint paint = new Paint();

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(1000,1300,1).create();

        PdfDocument.Page page = pdfDocument.startPage(pageInfo);

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

        canvas.drawText("Order date: "+ date.substring(3,10)+ date.substring(29), 50, 200, paint);
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
        mTextLayout = new StaticLayout(soldBy, mTextPaint, canvas.getWidth()/2-50, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
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

        for(MyOrderDetailActivity.Product product: prodList) {

            sum += product.price * product.qty;

            paint.setTextAlign(Paint.Align.LEFT);

            String title = product.title;
            if(title.length()>20) title = title.substring(0,20)+"...";

            canvas.drawText(""+ ++s_no, 60, low, paint);
            canvas.drawText(title, 160, low, paint);
            canvas.drawText(""+product.qty, 550 ,low, paint);

            paint.setTextAlign(Paint.Align.RIGHT);
            canvas.drawText(product.qty + " x " + (int)(0.82 * product.price), canvas.getWidth() - 40, low, paint);
            paint.setTextAlign(Paint.Align.LEFT);

            low += 50;
        }

        paint.setColor(Color.rgb(150,150, 150));
        canvas.drawRect(30, low+50, canvas.getWidth()-40, low+55, paint);

        paint.setColor(Color.BLACK);
        low += 100;

        canvas.drawText("SUBTOTAL", 550, low, paint);
        canvas.drawText("GST 18%", 550, low+50, paint);
        canvas.drawText(payment, 280, low, paint);

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

        page.getCanvas().drawBitmap(bitmap, 0, 0, paint);

        ImageView pdfImg = findViewById(R.id.invoice_img);
        pdfImg.setImageBitmap(bitmap);

        pdfDocument.finishPage(page);

        File file = new File(this.getExternalFilesDir("/PDF/"), date.substring(10,20)+"Testing Invoice.pdf");

        try {
            pdfDocument.writeTo(new FileOutputStream(file));
            Toast.makeText(this, "File Saved", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed To Save", Toast.LENGTH_SHORT).show();
        }

        pdfDocument.close();

    }
    */

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

    static class Product {
        String title;
        int qty;
        int price;
         Product(String title, int qty, int price) {
            this.title = title;
            this.price = price;
            this.qty = qty;
        }

    }

}
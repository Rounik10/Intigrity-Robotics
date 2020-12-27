package com.example.intigritirobotics;

import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MyOrderDetailActivity extends AppCompatActivity {

    private SQLiteDatabase sqLiteDatabase;
    private String orderId;
    private String date;
    private String address, soldBy, payment;
    TextView randomTest;
    private List<Product> prodList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_order_detail);

   //     Intent myIntent = getIntent();

        /*
            intent.putExtra("date", clickedOrder.getOrderDate());
            intent.putExtra("order id", clickedOrder.getOrderID());
            intent.putExtra("productId", clickedOrder.getProductID());
            intent.putExtra("status", clickedOrder.getProductStatus());
        */

//        orderId = myIntent.getStringExtra("order id");
//        productId = myIntent.getStringExtra("productId").split(", ");
//        date = myIntent.getStringExtra("date");
//        status = myIntent.getStringExtra("status");

        date = Calendar.getInstance().getTime().toString();

        randomTest = findViewById(R.id.textView16);

        PdfHelper pdfHelper = new PdfHelper(this);
        sqLiteDatabase = pdfHelper.getWritableDatabase();
        payment = "UPI";

        // Product list banana hai jsime ki products honge saare

        prodList = new ArrayList<>();

        prodList.add(new Product("title 1", 1, 50));
        prodList.add(new Product("title 2", 2, 100));
        prodList.add(new Product("title 2", 2, 100));

        address = "Sumit sharma Iar institute of advance robotics, dehradun, Itbp road DEHRADUN, UTTARAKHAND, 248001 IN State/UT Code: 05";
        orderId = "#1001";
        soldBy = "Gamotech * SURVEY NO. 38/2, 39 AND 40, JADIGENAHALLI HOBLI,KACHARAKANAHALLI VILLAGE, HOSAKOTE TALUK, Bengaluru (Bangalore) Urban Bangalore, Karnataka, 562114 IN";

        pdfHelper.insert("Name", "9999999", 5L,"55", 11,111);

        saveInvoiceAsPDF();
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

    public void saveInvoiceAsPDF() {

        PdfDocument pdfDocument = new PdfDocument();
        Paint paint = new Paint();

        Cursor cursor = sqLiteDatabase.query("myTable", null, null, null, null, null, null);

        cursor.move(cursor.getCount());

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
        canvas.drawText("Dummy Name", 280 , row + 120, paint);

        canvas.drawText("Contact No: ", 550 , row + 120, paint);
        canvas.drawText("+91 629902260X", 720 , row + 120, paint);

        paint.setColor(Color.rgb(150, 150, 150));
        canvas.drawRect(30, row + 150, canvas.getWidth()-30, row + 200, paint);

        paint.setColor(Color.WHITE);

        int low = row + 250;

        canvas.drawText("S/No.", 50, row + 185, paint);
        canvas.drawText("Items", 300, row + 185, paint);
        canvas.drawText("Qty", 550, row + 185, paint);

        paint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText("Amount", canvas.getWidth()-40, 435, paint);
        paint.setTextAlign(Paint.Align.RIGHT);

        paint.setColor(Color.BLACK);

        int s_no = 0;
        int sum = 0;

        for(Product product: prodList) {

            sum += product.price * product.qty;

            paint.setTextAlign(Paint.Align.LEFT);

            canvas.drawText(""+ ++s_no, 60, low, paint);
            canvas.drawText(product.title, 160, low, paint);
            canvas.drawText(""+product.qty, 550 ,low, paint);

            paint.setTextAlign(Paint.Align.RIGHT);
            canvas.drawText(product.qty + " x " + product.price, canvas.getWidth() - 40, low, paint);
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
        canvas.drawText("Rs." + sum + "/-", canvas.getWidth()-40, low, paint);
        canvas.drawText( "Rs."+0.18 * sum+"/-", canvas.getWidth()-40, low+50, paint);

        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText("Payment Method: ",270, low, paint);
        canvas.drawText( "Rs."+(0.18 * sum + sum )+"/-", canvas.getWidth()-40, low+100, paint);
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
        sqLiteDatabase.close();
        cursor.close();

    }
}
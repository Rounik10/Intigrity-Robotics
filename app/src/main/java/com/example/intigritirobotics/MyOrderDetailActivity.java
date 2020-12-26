package com.example.intigritirobotics;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class MyOrderDetailActivity extends AppCompatActivity {

    private PdfHelper pdfHelper;
    private SQLiteDatabase sqLiteDatabase;
    private Button saveInvoiceButton;
    private String orderId;
    private String productId[];
    private String date;
    private String status;
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

        pdfHelper = new PdfHelper(this);
        sqLiteDatabase = pdfHelper.getWritableDatabase();

        saveInvoiceButton = findViewById(R.id.save_invoice_button);

        // Product list banana hai jsime ki products honge saare

        prodList = new ArrayList<>();

        prodList.add(new Product("title 1", 1, 50));
        prodList.add(new Product("title 2", 2, 100));

        orderId = "Iddd";

        pdfHelper.insert("Name", "9999999", 5L,"55", 11,111);

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

    public void saveInvoiceAsPDF(View view) {

        PdfDocument pdfDocument = new PdfDocument();
        Paint paint = new Paint();

        String[] columns = {"invoiceNo", "customerName", "contactNo", "date", "item", "qty", "amount"};
        Cursor cursor = sqLiteDatabase.query("myTable", null, null, null, null, null, null);

        cursor.move(cursor.getCount());

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(1000,900,1).create();

        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        paint.setTextSize(80);
        canvas.drawText("Intigriti Robotics", 30, 30, paint);

        paint.setTextAlign(Paint.Align.RIGHT);

        paint.setTextSize(30);
        canvas.drawText("Invoice Id", canvas.getWidth()-40, 40, paint);
        canvas.drawText(orderId, canvas.getWidth()-40, 40, paint);

        paint.setTextAlign(Paint.Align.LEFT);

        paint.setColor(Color.rgb(150, 150, 150));
        canvas.drawRect(30, 150, canvas.getWidth()-40, 160, paint);

        paint.setColor(Color.BLACK);
        canvas.drawText("date", 50, 200, paint);


        paint.setColor(Color.rgb(150, 150, 150));
        canvas.drawRect(30, 250, 280, 300, paint);

        paint.setColor(Color.WHITE);
        canvas.drawText("BILL TO", 50, 285, paint);

        paint.setColor(Color.BLACK);
        canvas.drawText("Consumer Name: ", 30 , 350, paint);
        canvas.drawText("Dummy Name", 150 , 350, paint);

        canvas.drawText("Contact No: ", 650 , 350, paint);
        canvas.drawText("Dummy Name", 850 , 350, paint);

        paint.setColor(Color.rgb(150, 150, 150));
        canvas.drawRect(30, 400, canvas.getWidth()-30, 450, paint);

        paint.setColor(Color.WHITE);
        canvas.drawText("S/No.", 50, 435, paint);
        canvas.drawText("Item", 90, 435, paint);
        canvas.drawText("Qty", 550, 435, paint);

        paint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText("Amount", canvas.getWidth()-40, 435, paint);
        paint.setTextAlign(Paint.Align.RIGHT);

        paint.setColor(Color.BLACK);

        int low = 500;
        int s_no = 0;

        int sum = 0;

        for(Product product: prodList) {

            sum += product.price * product.qty;

            canvas.drawText(""+ ++s_no, 50, low, paint);
            canvas.drawText(product.title, 90, low, paint);
            canvas.drawText(""+product.qty, 550 ,low, paint);
            paint.setTextAlign(Paint.Align.RIGHT);
            canvas.drawText(product.qty + " x " + product.price, canvas.getWidth() - 40, low, paint);
            paint.setTextAlign(Paint.Align.LEFT);
            low += 50;
        }

        paint.setColor(Color.rgb(150,150, 150));
        canvas.drawRect(30, low+50, canvas.getWidth(), low+100, paint);

        low += 100;

        canvas.drawText("SUBTOTAL", 550, low, paint);
        canvas.drawText("GST 18%", 550, low+50, paint);

        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText("TOTAL", 550, low+100, paint);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));

        paint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText("Rs." + sum + "/-", canvas.getWidth()-40, low, paint);
        canvas.drawText( "Rs."+0.18 * sum+"/-", canvas.getWidth()-40, low+50, paint);

        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText( "Rs."+(0.18 * sum + sum )+"/-", canvas.getWidth()-40, low+100, paint);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));

        pdfDocument.finishPage(page);

        File file = new File(this.getExternalFilesDir("/"), Math.random()*1000+"Testing Invoice.pdf");

        try {
            pdfDocument.writeTo(new FileOutputStream(file));
            Toast.makeText(this, "File Saved", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed To Save", Toast.LENGTH_SHORT).show();
        }

        pdfDocument.close();

    }
}
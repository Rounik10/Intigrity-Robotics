package com.example.intigritirobotics;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.graphics.pdf.PdfRenderer;
import android.os.Bundle;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.example.intigritirobotics.MainHomeActivity.TheUser;
import static com.example.intigritirobotics.MainHomeActivity.firebaseFirestore;

public class ProjectPdfActivity extends AppCompatActivity {

    private String orderId, soldBy, address, payment, date;
    private String[] productId, productQty, productPrices;
    private List<MyOrderDetailActivity.Product> prodList;
    private ImageView pdfView;
    private PdfDocument pdfDocument;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_pdf);

        Intent intent = getIntent();

        date = intent.getStringExtra("date");
        orderId = intent.getStringExtra("orderId");
        payment = intent.getStringExtra("payment");
        soldBy = intent.getStringExtra("soldBy");
        address = intent.getStringExtra("address");
        productId = intent.getStringExtra("productId").split(", ");
        productQty = intent.getStringExtra("productQty").split(", ");
        productPrices = intent.getStringExtra("productPrices").split(", ");

        pdfView = findViewById(R.id.pdf_viewer);

        prodList = new ArrayList<>();

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

        Button button = findViewById(R.id.save_pdf_button);

        button.setOnClickListener(l->{
            File file = new File(this.getExternalFilesDir("/PDF/"), date.substring(10,20)+"Testing Invoice.pdf");

            try {
                pdfDocument.writeTo(new FileOutputStream(file));
                Toast.makeText(this, "File Saved ", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed To Save", Toast.LENGTH_SHORT).show();
            }

        });

    }

    public void saveInvoiceAsPDF() {

        pdfDocument = new PdfDocument();
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

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(canvas.getWidth(),low+250,1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);

        page.getCanvas().drawBitmap(bitmap, 0, 0, paint);

        pdfView.setImageBitmap(bitmap);

        pdfDocument.finishPage(page);

    }

}
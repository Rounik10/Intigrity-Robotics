package com.example.intigritirobotics;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.pdf.PdfDocument;
import android.graphics.pdf.PdfRenderer;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.barteksc.pdfviewer.PDFView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.FileNotFoundException;
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

        orderId = intent.getStringExtra("orderId");
        pdfView = findViewById(R.id.pdf_viewer);


        firebaseFirestore.collection("ORDERS").document(orderId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot  documentSnapshot = task.getResult();
                Glide.with(getApplicationContext()).load(documentSnapshot.get("invoice token")).apply(new RequestOptions().placeholder(R.drawable.category_icon)).into(pdfView);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
        Button button = findViewById(R.id.save_pdf_button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            saveimage();
            }
        });

    }
    private void saveimage ()
    {
        BitmapDrawable drawable = (BitmapDrawable) pdfView.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        File filename;
        try {
            String path = Environment.getExternalStorageDirectory().toString();

            new File(path + "/folder/subfolder").mkdirs();
            filename = new File(path + "/folder/subfolder/image.jpg");

            FileOutputStream out = new FileOutputStream(filename);


            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

            MediaStore.Images.Media.insertImage(getContentResolver(), filename.getAbsolutePath(), filename.getName(), filename.getName());

            Toast.makeText(getApplicationContext(), "File is Saved in  " + filename, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
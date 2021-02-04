package com.example.intigritirobotics.e_store;

import android.app.Dialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.intigritirobotics.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import static android.os.Environment.DIRECTORY_DOWNLOADS;
import static com.example.intigritirobotics.e_store.MainHomeActivity.TheUser;

public class ProjectPdfActivity extends AppCompatActivity {

    private String orderId;
    private ImageView pdfView;
    private Dialog loadingDialog;
    private ProgressDialog pd;
    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_pdf);
        loadingDialog = new Dialog(ProjectPdfActivity.this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.border_background));

        loadingDialog.show();

        pd = new ProgressDialog(this);
        pd.setTitle("Loading");
        pd.show();
        Intent intent = getIntent();

        orderId = intent.getStringExtra("orderId");
        pdfView = findViewById(R.id.pdf_viewer);

        loadInvoice();

    }

    private void loadInvoice() {
        firebaseFirestore.collection("ORDERS")
                .document(orderId)
                .get()
                .addOnCompleteListener(task -> {
                    DocumentSnapshot documentSnapshot = task.getResult();

                    if(documentSnapshot.get("invoice token").toString() == null) {
                        uploadPdf();
                        return;
                    }

                    Glide.with(getApplicationContext())
                            .load(documentSnapshot.get("invoice token"))
                            .apply(new RequestOptions().placeholder(R.drawable.category_icon))
                            .into(pdfView);

                    pd.dismiss();
                }).addOnFailureListener(Throwable::printStackTrace);
        Button button = findViewById(R.id.save_pdf_button);

        button.setOnClickListener(view -> saveImage());
        loadingDialog.dismiss();
    }

    private void saveImage() {
        loadingDialog.show();
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference filRef = firebaseStorage.getReference().child("/invoices/" + orderId);

        filRef.getDownloadUrl().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String url = task.getResult().toString();
                downloadFile(this, "Invoice " + orderId, DIRECTORY_DOWNLOADS, url);
            } else {
                task.getException().printStackTrace();
                Toast.makeText(this, "Something went wrong, Pleas try Again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void downloadFile(Context context, String fileName, String destinationDir, String url) {
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context, destinationDir, fileName + ".png");

        downloadManager.enqueue(request);
        loadingDialog.dismiss();
        Toast.makeText(ProjectPdfActivity.this, "File Saved! ", Toast.LENGTH_SHORT).show();
    }

    private ArrayList<String> getProdTitles(String[] products) {
        ArrayList<String> titles = new ArrayList<>();
        for(String prod:products) {
            firebaseFirestore.document("PRODUCTS/"+prod)
                    .get()
                    .addOnSuccessListener(task-> titles.add(task.get("product title").toString()));
        }
        return titles;
    }

    private void uploadPdf() {

        firebaseFirestore.document("ORDERS/" + orderId).get().addOnSuccessListener(task -> {

            String date = task.get("order date").toString();
            String address = task.get("shipping address").toString();
            String paymentId = task.get("payment id").toString();


            String[] products = task.get("productQsIds").toString().split(", ");
            String[] qty = task.get("productQty").toString().split(", ");
            String[] prices = task.get("productPrice").toString().split(", ");
            ArrayList<String> titles = getProdTitles(products);


            PdfDocument pdfDocument = new PdfDocument();
            Paint paint = new Paint();

            Bitmap bitmap = Bitmap.createBitmap(1000, 1300, Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(bitmap);

            paint.setColor(Color.WHITE);
            canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), paint);
            paint.setColor(Color.BLACK);

            paint.setTextSize(80);
            canvas.drawText("Intigriti Robotics", 30, 80, paint);

            paint.setTextAlign(Paint.Align.RIGHT);

            paint.setTextSize(30);
            canvas.drawText("Invoice Id", canvas.getWidth() - 40, 40, paint);
            canvas.drawText(orderId, canvas.getWidth() - 40, 80, paint);

            paint.setTextAlign(Paint.Align.LEFT);

            paint.setColor(Color.rgb(150, 150, 150));
            canvas.drawRect(30, 130, canvas.getWidth() - 40, 135, paint);

            paint.setColor(Color.BLACK);
            paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

            canvas.drawText("Order date: " + date, 50, 200, paint);
            canvas.drawText("Shipping Address: ", 50, 250, paint);

            paint.setTextAlign(Paint.Align.RIGHT);
            canvas.drawText("Sold By: ", 665, 250, paint);

            paint.setTextAlign(Paint.Align.LEFT);
            paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));

            TextPaint mTextPaint = new TextPaint();

            mTextPaint.setTextSize(28);

            StaticLayout mTextLayout = new StaticLayout(address, mTextPaint, canvas.getWidth() / 2 - 50, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);

            canvas.save();

            canvas.translate(50, 270);
            mTextLayout.draw(canvas);

            int t1 = mTextLayout.getHeight();
            canvas.restore();

            canvas.save();
            mTextLayout = new StaticLayout("soldBy", mTextPaint, canvas.getWidth() / 2 - 50, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            canvas.translate(550, 270);
            mTextLayout.draw(canvas);

            int t2 = mTextLayout.getHeight();
            canvas.restore();

            int row = 270 + Math.max(t1, t2);

            paint.setColor(Color.rgb(150, 150, 150));
            canvas.drawRect(30, row + 30, 280, row + 80, paint);

            paint.setColor(Color.WHITE);
            canvas.drawText("BILL TO", 50, row + 65, paint);

            paint.setColor(Color.BLACK);
            canvas.drawText("Consumer Name: ", 30, row + 120, paint);
            canvas.drawText(TheUser.name, 280, row + 120, paint);

            canvas.drawText("Contact No: ", 550, row + 120, paint);
            canvas.drawText(TheUser.phoneNumber, 720, row + 120, paint);

            paint.setColor(Color.rgb(150, 150, 150));
            canvas.drawRect(30, row + 150, canvas.getWidth() - 30, row + 200, paint);

            paint.setColor(Color.WHITE);

            int low = row + 250;

            canvas.drawText("S/No.", 50, row + 185, paint);
            canvas.drawText("Items", 300, row + 185, paint);
            canvas.drawText("Qty", 550, row + 185, paint);

            paint.setTextAlign(Paint.Align.RIGHT);
            canvas.drawText("Amount", canvas.getWidth() - 40, row + 185, paint);
            paint.setTextAlign(Paint.Align.LEFT);

            paint.setColor(Color.BLACK);

            int s_no = 0;
            int sum = 0;

            for (int i=0; i<products.length; i++) {

                sum += Integer.parseInt(prices[i]) * Integer.parseInt(qty[i]);

                paint.setTextAlign(Paint.Align.LEFT);

                String title = titles.get(i);
                if (title.length() > 20) title = title.substring(0, 20) + "...";

                canvas.drawText("" + ++s_no, 60, low, paint);
                canvas.drawText(title, 160, low, paint);
                canvas.drawText("" + qty[i], 550, low, paint);

                paint.setTextAlign(Paint.Align.RIGHT);
                canvas.drawText(qty[i] + " x " + (int) (0.82 * Integer.parseInt(prices[i])), canvas.getWidth() - 40, low, paint);
                paint.setTextAlign(Paint.Align.LEFT);

                low += 50;
            }

            paint.setColor(Color.rgb(150, 150, 150));
            canvas.drawRect(30, low + 50, canvas.getWidth() - 40, low + 55, paint);

            paint.setColor(Color.BLACK);
            low += 100;

            canvas.drawText("SUBTOTAL", 550, low, paint);
            canvas.drawText("GST 18%", 550, low + 50, paint);
            canvas.drawText(paymentId, 280, low, paint);

            paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            canvas.drawText("TOTAL", 550, low + 100, paint);
            paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));

            paint.setTextAlign(Paint.Align.RIGHT);
            canvas.drawText("Rs." + 0.82 * sum + "/-", canvas.getWidth() - 40, low, paint);
            canvas.drawText("Rs." + 0.18 * sum + "/-", canvas.getWidth() - 40, low + 50, paint);

            paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            canvas.drawText("Payment ID: ", 270, low, paint);
            canvas.drawText("Rs." + (0.18 * sum + 0.82 * sum) + "/-", canvas.getWidth() - 40, low + 100, paint);
            paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));

            uploadBitmap(bitmap);

            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(canvas.getWidth(), low + 250, 1).create();
            PdfDocument.Page page = pdfDocument.startPage(pageInfo);

            page.getCanvas().drawBitmap(bitmap, 0, 0, paint);
            pdfDocument.finishPage(page);

        }).addOnFailureListener(Throwable::printStackTrace);

    }

    private void uploadBitmap(Bitmap bitmap) {
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] data = byteArrayOutputStream.toByteArray();

        UploadTask uploadTask = firebaseStorage.getReference("invoices").child(orderId).putBytes(data);

        uploadTask.addOnSuccessListener(taskSnapshot -> {
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            StorageReference dateRef = storageRef.child("invoices").child(orderId);
            dateRef.getDownloadUrl().addOnSuccessListener(downloadUrl -> firebaseFirestore
                    .collection("ORDERS")
                    .document(orderId)
                    .update("invoice token", downloadUrl.toString()));

        }).addOnFailureListener(e -> {
            Log.d("File Upload", "Invoice upload failed");
            e.printStackTrace();
        }).addOnProgressListener(snapshot -> {
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Please Wait");
            progressDialog.show();
        });

    }

}
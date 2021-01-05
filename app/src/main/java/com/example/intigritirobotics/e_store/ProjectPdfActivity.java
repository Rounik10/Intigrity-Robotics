package com.example.intigritirobotics.e_store;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.intigritirobotics.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import static android.os.Environment.DIRECTORY_DOWNLOADS;
import static com.example.intigritirobotics.e_store.MainHomeActivity.firebaseFirestore;

public class ProjectPdfActivity extends AppCompatActivity {

    private String orderId;
    private ImageView pdfView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_pdf);

        Intent intent = getIntent();

        orderId = intent.getStringExtra("orderId");
        pdfView = findViewById(R.id.pdf_viewer);

        firebaseFirestore.collection("ORDERS").document(orderId).get().addOnCompleteListener(task -> {
            DocumentSnapshot  documentSnapshot = task.getResult();
            assert documentSnapshot != null;
            Glide.with(getApplicationContext())
                    .load(documentSnapshot.get("invoice token"))
                    .apply(new RequestOptions().placeholder(R.drawable.category_icon))
                    .into(pdfView);

        }).addOnFailureListener(e -> {

        });
        Button button = findViewById(R.id.save_pdf_button);

        button.setOnClickListener(view -> saveImage());

    }
    private void saveImage ()
    {
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference filRef = firebaseStorage.getReference().child("/invoices/" + orderId);

        filRef.getDownloadUrl().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                String url = task.getResult().toString();
                downloadFile(this, "Invoice "+ orderId, DIRECTORY_DOWNLOADS, url);
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
        request.setDestinationInExternalFilesDir(context, destinationDir, fileName+ ".png");

        downloadManager.enqueue(request);
    }

}
package com.example.intigritirobotics.e_store;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.ViewGroup;
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
    private  Dialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_pdf);
        loadingDialog = new Dialog(ProjectPdfActivity.this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.border_background));

        loadingDialog.show();

        ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Loading");
        pd.show();
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

            pd.dismiss();
        }).addOnFailureListener(e -> {

        });
        Button button = findViewById(R.id.save_pdf_button);

        button.setOnClickListener(view -> saveImage());
        loadingDialog.dismiss();

    }
    private void saveImage ()
    {
        loadingDialog.show();
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
        loadingDialog.dismiss();
        Toast.makeText(ProjectPdfActivity.this, "File Saved! ", Toast.LENGTH_SHORT).show();
    }

}
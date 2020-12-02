package com.example.intigritirobotics;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class UpdateUserDetails extends AppCompatActivity {

    public UserModel currentUser;
    public FirebaseAuth firebaseAuth;
    public FirebaseFirestore firebaseFirestore;
    private EditText addFstLine;
    private EditText addSecLine;
    private EditText PIN;
    private EditText city;
    private EditText state;
    private EditText phone;
    private ImageView profilePic;
    private Uri imageUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user_details);
        firebaseFirestore = FirebaseFirestore.getInstance();
        addFstLine = findViewById(R.id.address_line1);
        addSecLine = findViewById(R.id.address_line2);
        profilePic = findViewById(R.id.add_user_image);
        city = findViewById(R.id.city);
        PIN = findViewById(R.id.PIN);
        state = findViewById(R.id.state);
        phone = findViewById(R.id.phone);

    }

    public void openDashboard(View view) {
        startActivity(new Intent(this, MainHomeActivity.class));
    }

    public void updateDataOpenDashboard(View view) {
        SharedPreferences pref = getSharedPreferences("user_details",MODE_PRIVATE);
        String name = pref.getString("username", "Not A User");
        String address = ""+ addFstLine.getText() +",\n"
                + addSecLine.getText()
                +",\n" + city.getText()
                +", " + PIN.getText()+ "\n"+ state.getText();

        String phoneNumber = ""+ phone.getText();
        firebaseAuth = FirebaseAuth.getInstance();
        String userId = firebaseAuth.getUid();

        currentUser = new UserModel(name, address, phoneNumber, userId);

        Map<String, Object> map = new HashMap<>();
        map.put("Name", name);
        map.put("Address", address);
        map.put("Phone",phoneNumber);
        map.put("PIN",PIN.getText().toString());
        Log.d("User Id", "Hell: "+userId);

        DocumentReference docRef = firebaseFirestore.document("USERS/" + userId);

        docRef.update(map).addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                startActivity(new Intent(this, MainHomeActivity.class));
            } else {
                Toast.makeText(this, "Update Failed", Toast.LENGTH_SHORT).show();
                Log.d("fail", "Update failed");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK && data!= null && data.getData()!=null){
            imageUri = data.getData();
            profilePic.setImageURI(imageUri);
            uploadPic();
        }
    }

    private void uploadPic(){
        Uri file = imageUri;
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();

        final ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Uploading Image");
        pd.show();
        StorageReference riversRef = storageReference.child("profileImg/"+currentUser);

        riversRef.putFile(file)
                .addOnSuccessListener(taskSnapshot -> {
                    // Get a URL to the uploaded content
                    pd.dismiss();
                    Snackbar.make(findViewById(android.R.id.content), "Image Uploaded", Snackbar.LENGTH_SHORT).show();
                })
                .addOnFailureListener(exception -> {
                    Toast.makeText(getApplicationContext(), "Update Failed", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }).addOnProgressListener(snapshot -> {
                    double progressPresent = (100.00 * snapshot.getBytesTransferred()/snapshot.getTotalByteCount());
                    pd.setMessage("Percentage "+(int)progressPresent +"%");
                });
    }

    public void handleImage(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);

    }
}
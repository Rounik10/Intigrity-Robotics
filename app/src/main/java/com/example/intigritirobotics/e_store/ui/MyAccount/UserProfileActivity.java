package com.example.intigritirobotics.e_store.ui.MyAccount;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.example.intigritirobotics.R;
import com.example.intigritirobotics.e_store.UpdateUserDetails;

public class UserProfileActivity extends AppCompatActivity {

    private CardView updateProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        Toolbar toolbar = findViewById(R.id.profile_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        updateProfile = findViewById(R.id.update_profile);
        updateProfile.setOnClickListener(l -> updateUserDetails());

        TextView userNameText = findViewById(R.id.profile_user_name);

        String userName = getIntent().getStringExtra("user_name");
        if (userName != null) userNameText.setText(userName);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user_profile, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {

            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void updateUserDetails() {
        Intent intent = new Intent(getApplicationContext(), UpdateUserDetails.class);
        startActivity(intent);
    }
}
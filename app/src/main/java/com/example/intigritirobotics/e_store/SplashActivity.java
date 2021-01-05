package com.example.intigritirobotics.e_store;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.example.intigritirobotics.R;

public class SplashActivity extends AppCompatActivity {

    SharedPreferences pref;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash2);

        int SPLASH_TIME_OUT = 3000;
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            pref = getSharedPreferences("user_details",MODE_PRIVATE);
            intent = new Intent(SplashActivity.this,MainHomeActivity.class);
            if(pref.contains("username") && pref.contains("password")){
                startActivity(intent);
            }
            else {
                Intent sign = new Intent(SplashActivity.this, SignUpActivity.class);
                startActivity(sign);
            }
            finish();
        }, SPLASH_TIME_OUT);
    }
    public void HomeShow() {

        Intent intt2 = new Intent(this, SignUpActivity.class);
        startActivity(intt2);
        finish();
    }
}
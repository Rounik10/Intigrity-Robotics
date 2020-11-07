package com.example.intigritirobotics;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

public class SplashActivity extends AppCompatActivity {

    SharedPreferences pref;
    Intent intent;

    private static int SPLASH_TIME_OUT=3000;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash2);





        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {

                pref = getSharedPreferences("user_details",MODE_PRIVATE);
                intent = new Intent(SplashActivity.this,MainHomeActivity.class);
                if(pref.contains("username") && pref.contains("password")){
                    startActivity(intent);
                    finish();
                }
                else {
                    Intent sign = new Intent(SplashActivity.this,sign_up.class);
                    startActivity(sign);
                    finish();

                }

            }
        },SPLASH_TIME_OUT);
    }
    public void HomeShow() {

        Intent intt2 = new Intent(this, sign_up.class);
        startActivity(intt2);
        finish();
    }
}

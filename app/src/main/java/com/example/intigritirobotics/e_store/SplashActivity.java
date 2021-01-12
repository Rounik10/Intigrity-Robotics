package com.example.intigritirobotics.e_store;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import com.example.intigritirobotics.R;

public class SplashActivity extends AppCompatActivity {

    SharedPreferences pref;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash2);

        if(!isConnectionAvailable(this)) {
            try {
                AlertDialog alertDialog = new AlertDialog.Builder(this).create();

                alertDialog.setTitle("Info");
                alertDialog.setMessage("Internet not available, Cross check your internet connectivity and try again");
                alertDialog.show();
                alertDialog.setOnCancelListener(dialogInterface -> finish());

            } catch (Exception e) {
                Log.d("SplashAct", "Show Dialog: " + e.getMessage());
            }
        } else {
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


    }
    public void HomeShow() {

        Intent intt2 = new Intent(this, SignUpActivity.class);
        startActivity(intt2);
        finish();
    }

    public static boolean isConnectionAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
            return netInfo != null && netInfo.isConnected()
                    && netInfo.isConnectedOrConnecting()
                    && netInfo.isAvailable();
        }
        return false;
    }
}
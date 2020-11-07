package com.example.intigritirobotics;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;


public class ResetActivity extends AppCompatActivity {

    private EditText em3;
    private Button rt;
    private TextView gb;
    private FirebaseAuth fba;
    private ProgressBar pb3;
    private String emailpattern = "[a-zA-Z0-9._-]+@[a-z]+.[a-z]+";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        em3 = (EditText) findViewById(R.id.resetemail);
        rt = (Button)findViewById(R.id.reset);
        gb = (TextView)findViewById(R.id.goback);
        pb3 = (ProgressBar)findViewById(R.id.progressBar3) ;
        fba = FirebaseAuth.getInstance();
        em3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                CheckInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        gb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gobak();
            }
        });
        rt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pb3.setVisibility(View.VISIBLE);
                Checkemail();
            }
        });
    }
    public void gobak()
    {
        Intent intt = new Intent(this, SignInActivity.class);
        startActivity(intt);
        finish();
    }
    public void CheckInputs() {
        if (!TextUtils.isEmpty(em3.getText())) {
            rt.setEnabled(true);


        } else {
            rt.setEnabled(false);
        }
    }
    public void Checkemail()
    {
        pb3.setVisibility(View.VISIBLE);
        rt.setEnabled(false);
        String e = String.valueOf(em3.getText());
        if(em3.getText().toString().matches(emailpattern))
        {
            fba.sendPasswordResetEmail(em3.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {

                                log();

                            }
                            else
                            {

                                em3.setError("There is no user record corresponding to this identifier." );
                                pb3.setVisibility(View.INVISIBLE);
                                rt.setEnabled(true);
                            }
                        }
                    });
        }
        else
        {
            em3.setError("Please Check your E-Mail");
            pb3.setVisibility(View.INVISIBLE);
            rt.setEnabled(true);
        }

    }

    public void log()
    {
        pb3.setVisibility(View.INVISIBLE);
        Toast.makeText(this, "E-Mail Sent Please Check Your Inbox.", Toast.LENGTH_LONG).show();
        Intent intt = new Intent(this, SignInActivity.class);
        startActivity(intt);
        finish();
    }
}

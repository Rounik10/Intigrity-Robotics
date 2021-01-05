package com.example.intigritirobotics.e_store;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.intigritirobotics.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class SignInActivity extends AppCompatActivity {

    private EditText em2,ps2;
    private Button si2, su2 ;
    int t=0,i,x,y,z,s,u,v,w;
    private String e,p;
    private char ch;
    private TextView fp;
    private ProgressBar pb2;
    private FirebaseAuth fba;
    private Button psv2;
    SharedPreferences pref;
    Context context;
    Intent intent;
    private String m, emailpattern = "[a-zA-Z0-9._-]+@[a-z]+.[a-z]+";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        em2 = (EditText) findViewById(R.id.email2);
        ps2 = (EditText) findViewById(R.id.pass2);
        si2 = (Button) findViewById(R.id.signin2);
        su2 = (Button) findViewById(R.id.signup2);
        psv2 = (Button) findViewById(R.id.passview2);
        pb2 = (ProgressBar)findViewById(R.id.progressBar2);
        pref = getSharedPreferences("user_details",MODE_PRIVATE);
        intent = new Intent(SignInActivity.this,MainHomeActivity.class);
        fba = FirebaseAuth.getInstance();
        fp = (TextView)findViewById(R.id.FargotPass);
        em2.addTextChangedListener(new TextWatcher() {
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
        ps2.addTextChangedListener(new TextWatcher() {
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
        si2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                e = String.valueOf(em2.getText());
                p = String.valueOf(ps2.getText());

                Checkemailpss();
            }
        });
        su2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();

            }
        });
        psv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                t++;
                PassView();
            }
        });
        fp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Showreset();
            }
        });


    }

    private void Showreset() {
        Intent intt = new Intent(this, ResetActivity.class);
        startActivity(intt);
        finish();
    }

    private void signup() {
        Intent intt = new Intent(this, SignUpActivity.class);
        startActivity(intt);
        finish();
    }
    public void CheckInputs() {
        if (!TextUtils.isEmpty(em2.getText())) {
            if (!TextUtils.isEmpty(ps2.getText())) {
                si2.setEnabled(true);

            } else {
                si2.setEnabled(false);
            }
        } else {
            si2.setEnabled(false);
        }
    }

    private void Checkemailpss() {
        pb2.setVisibility(View.VISIBLE);
        si2.setEnabled(false);
        if (em2.getText().toString().matches(emailpattern)) {
            fba.signInWithEmailAndPassword(em2.getText().toString(), ps2.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                SharedPreferences.Editor editor = pref.edit();
                                editor.putString("username",e);
                                editor.putString("password",p);
                                editor.commit();
                                HomeShow();
                                finish();
                            } else {
                                pb2.setVisibility(View.INVISIBLE);
                                si2.setEnabled(true);
                                em2.setError("Invalid E-Mail or Password" );
                            }
                        }
                    });
        } else {

            em2.setError("Invalid Email");
            pb2.setVisibility(View.INVISIBLE);
            si2.setEnabled(true);

        }
    }




    public void HomeShow() {


        Toast.makeText(this, "Sign In Successful"  ,  Toast.LENGTH_SHORT).show();
        Intent intt2 = new Intent(this, MainHomeActivity.class);
        startActivity(intt2);
    }
    public void PassView() {

        if (t % 2 != 0) {

            ps2.setTransformationMethod(HideReturnsTransformationMethod.getInstance());

        } else {

            ps2.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }

    }

}

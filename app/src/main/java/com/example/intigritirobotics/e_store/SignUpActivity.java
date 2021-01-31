package com.example.intigritirobotics.e_store;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.intigritirobotics.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {

    private EditText nm1;
    private EditText em1;
    private EditText ps1;
    private EditText cps1;
    private Button su1, li1;
    private ImageView psv1;
    private FirebaseAuth fba;
    int t = 0;
    private String e, p, ps, emailpattern = "[a-zA-Z0-9._-]+@[a-z]+.[a-z]+";
    private ProgressBar pb1;
    private FirebaseFirestore firebaseFirestore;
    public static SharedPreferences pref;
    public static String currentUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        nm1 = (EditText) findViewById(R.id.name1);
        em1 = (EditText) findViewById(R.id.email1);
        ps1 = (EditText) findViewById(R.id.pass1);
        cps1 = (EditText) findViewById(R.id.cpass1);
        su1 = (Button) findViewById(R.id.signup1);
        li1 = (Button) findViewById(R.id.login1);
        psv1 = (ImageView) findViewById(R.id.passview1);
        fba = FirebaseAuth.getInstance();
        pref = getSharedPreferences("user_details", MODE_PRIVATE);
        firebaseFirestore = FirebaseFirestore.getInstance();
        pb1 = (ProgressBar) findViewById((R.id.progressBar));
        em1.addTextChangedListener(new TextWatcher() {
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
        nm1.addTextChangedListener(new TextWatcher() {
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
        ps1.addTextChangedListener(new TextWatcher() {
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
        cps1.addTextChangedListener(new TextWatcher() {
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

        su1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                e = String.valueOf(em1.getText());
                p = String.valueOf(ps1.getText());
                ps = String.valueOf(cps1.getText());

                CheckEmailPass();

            }
        });
        psv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                t++;
                PassView();


            }
        });
        li1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signin();
            }
        });


    }


    public void Signin(View v2) {

        Intent intt = new Intent(this, SignInActivity.class);
        startActivity(intt);
    }

    public void CheckInputs() {
        if (!TextUtils.isEmpty(em1.getText())) {
            if (!TextUtils.isEmpty(nm1.getText())) {

                if (!TextUtils.isEmpty(ps1.getText()) && ps1.length() >= 5) {

                    if (!TextUtils.isEmpty((cps1.getText()))) {
                        su1.setEnabled(true);
                    } else {
                        su1.setEnabled(false);
                    }

                } else {
                    su1.setEnabled(false);
                }
            } else {
                su1.setEnabled(false);
            }
        } else {
            su1.setEnabled(false);
        }
    }

    public void CheckEmailPass() {
        if (em1.getText().toString().matches(emailpattern)) {
            if (p.equals(ps)) {
                pb1.setVisibility(View.VISIBLE);
                su1.setEnabled(false);

                fba.createUserWithEmailAndPassword(em1.getText().toString(), ps1.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Map<Object, String> userdata = new HashMap<>();
                                    userdata.put("User Name", nm1.getText().toString());
                                    firebaseFirestore.collection("USERS")
                                            .document(fba.getUid().toString())
                                            .set(userdata)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    addCoupons();
                                                    SharedPreferences.Editor editor = pref.edit();
                                                    editor.putString("username", e);
                                                    editor.putString("password", p);
                                                    editor.apply();
                                                    startUserDetail();
                                                    currentUID = fba.getUid();
                                                    finish();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            String error = task.getException().getMessage();
                                            em1.setError(error);
                                            pb1.setVisibility(View.INVISIBLE);
                                            su1.setEnabled(true);
                                        }
                                    });

                                } else {
                                    String error = task.getException().getMessage();
                                    ps1.setError(error);
                                    pb1.setVisibility(View.INVISIBLE);
                                    su1.setEnabled(true);
                                }
                            }
                        });
            } else {
                cps1.setError("Confirm Password Don't Match");
            }

        } else {
            em1.setError("Invalid Email");
        }
    }

    private void addCoupons() {

        firebaseFirestore.collection("OFFERS").get().addOnSuccessListener(task -> {

            List<DocumentSnapshot> docList = task.getDocuments();
            Map<String, Object> coupons = new HashMap<>();

            for(int i=0; i<Math.min(docList.size(),4); i++) {
                coupons.put("Id",docList.get(i).getId());
                coupons.put("Expired", false);

                firebaseFirestore
                        .collection("USERS/"+ fba.getUid()+"/My Offers")
                        .document(docList.get(i).getId()).set(coupons)
                        .addOnFailureListener(Throwable::printStackTrace);
            }

        }).addOnFailureListener(Throwable::printStackTrace);

    }

    public void HomeShow() {
        Toast.makeText(this, "Sign Up Successful", Toast.LENGTH_SHORT).show();
        Intent intt2 = new Intent(this, MainHomeActivity.class);
        startActivity(intt2);
    }

    public void startUserDetail() {
        startActivity(new Intent(this, UpdateUserDetails.class));
    }

    public void PassView() {
        if (t % 2 != 0) {

            ps1.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            cps1.setTransformationMethod(HideReturnsTransformationMethod.getInstance());

        } else {

            ps1.setTransformationMethod(PasswordTransformationMethod.getInstance());
            cps1.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }

    }

    public void signin() {

        Intent intt2 = new Intent(this, SignInActivity.class);
        startActivity(intt2);
        finish();
    }
}
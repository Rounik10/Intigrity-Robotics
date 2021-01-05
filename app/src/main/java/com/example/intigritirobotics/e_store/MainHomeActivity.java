package com.example.intigritirobotics.e_store;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.intigritirobotics.R;
import com.example.intigritirobotics.e_store.ui.MyAccount.UserProfileActivity;
import com.example.intigritirobotics.e_store.ui.MyCart.MyCartActivity;
import com.example.intigritirobotics.e_store.ui.Setting.SettingActivity;
import com.example.intigritirobotics.e_store.ui.Support.SupportActivity;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import static com.example.intigritirobotics.e_store.SignUpActivity.pref;

public class MainHomeActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    public static FirebaseFirestore firebaseFirestore;
    public static Dialog loadingDialog;
    public static String currentUserUId;
    public FirebaseAuth firebaseAuth;
    public static UserModel TheUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadingDialog = new Dialog(MainHomeActivity.this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.border_background));
        loadingDialog.show();

        setContentView(R.layout.activity_main_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        firebaseFirestore  =FirebaseFirestore.getInstance();

        firebaseAuth = FirebaseAuth.getInstance();
        currentUserUId = firebaseAuth.getUid();



        loadUserDetails();

        NavigationView navigationView = findViewById(R.id.nav_view);

        DrawerLayout drawer = findViewById(R.id.drawer);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_e_store, R.id.nav_category, R.id.nav_offer, R.id.nav_my_orders,
                R.id.nav_notification, R.id.nav_my_cart, R.id.nav_setting, R.id.nav_support)
                .setDrawerLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        loadingDialog.dismiss();
    }

    private void loadUserDetails() {

        firebaseFirestore.document("USERS/" + currentUserUId).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                DocumentSnapshot userSnap = task.getResult();

                try{
                    String name =  Objects.requireNonNull(userSnap.get("User Name")).toString();
                    String address = Objects.requireNonNull(userSnap.get("Address")).toString();
                    String phone = Objects.requireNonNull(userSnap.get("Phone")).toString();

                    if(name!=null && address!=null && phone !=null){
                        TheUser = new UserModel(name, address, phone, currentUserUId);
                        Log.d("loadUserDetails", address);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case R.id.action_cart:
                 startActivity(new Intent(this, MyCartActivity.class));
                return true;
            case R.id.action_search:
                Toast.makeText(this,"Search", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void cart(MenuItem item) {
        Intent myIntent = new Intent(MainHomeActivity.this, MyCartActivity.class);
        startActivity(myIntent);

    }

    public void account(MenuItem item) {
        Intent myIntent = new Intent(MainHomeActivity.this, UserProfileActivity.class);
        startActivity(myIntent);

    }

    public void setting(MenuItem item) {
        Intent myIntent = new Intent(MainHomeActivity.this, SettingActivity.class);
        startActivity(myIntent);

    }

    public void support(MenuItem item) {
        Intent myIntent = new Intent(MainHomeActivity.this, SupportActivity.class);
        startActivity(myIntent);

    }

    public void logout(MenuItem item) {
        pref = getSharedPreferences("user_details", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.apply();
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
        finish();
    }

    private void addProduct()
    {
        for (int x=0; x<=100; x++) {

            Map<String,Object> pro = new HashMap<>();
        pro.put("1_star","0");
        pro.put("2_star","0");
        pro.put("3_star","0");
        pro.put("4_star","0");
        pro.put("5_star","0");
        pro.put("details","fekh");
        pro.put("in stock","true");
        pro.put("other","hdwjd");
        pro.put("product price","525");
        pro.put("product rating","0");
        pro.put("product title","hbsa");
        pro.put("product_pic","c");
        pro.put("specs",0);

            firebaseFirestore.collection("PRODUCTS").add(pro);
        }

    }

}
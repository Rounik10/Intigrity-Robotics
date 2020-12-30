package com.example.intigritirobotics;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import com.example.intigritirobotics.ui.MyAccount.UserProfileActivity;
import com.example.intigritirobotics.ui.MyCart.MyCartActivity;
import com.example.intigritirobotics.ui.Setting.SettingActivity;
import com.example.intigritirobotics.ui.Support.SupportActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;
import com.squareup.okhttp.internal.framed.Header;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import static com.example.intigritirobotics.SignUpActivity.pref;

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
        setContentView(R.layout.activity_main_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        firebaseFirestore  =FirebaseFirestore.getInstance();


        firebaseAuth = FirebaseAuth.getInstance();
        currentUserUId = firebaseAuth.getUid();

        loadUserDetails();

        loadingDialog = new Dialog(MainHomeActivity.this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.border_background));
        loadingDialog.show();

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



    }

    private void loadUserDetails() {

        firebaseFirestore.document("USERS/" + currentUserUId).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                DocumentSnapshot userSnap = task.getResult();
                TheUser = new UserModel(
                    userSnap.get("User Name").toString(),
                    userSnap.get("Address").toString(),
                    userSnap.get("Phone").toString(),
                    currentUserUId
                );
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
}
package com.example.intigritirobotics;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Menu;

import com.example.intigritirobotics.ui.MyAccount.UserProfileActivity;
import com.example.intigritirobotics.ui.MyCart.MyCartActivity;
import com.example.intigritirobotics.ui.Setting.SettingActivity;
import com.example.intigritirobotics.ui.Support.SupportActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class


MainHomeActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent myIntent = new Intent(MainHomeActivity.this, UserProfileActivity.class);
                startActivity(myIntent);
            }
        });
        NavigationView navigationView = findViewById(R.id.nav_view);

        DrawerLayout drawer = findViewById(R.id.drawer);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_e_store, R.id.nav_category, R.id.nav_offer, R.id.nav_my_orders,
                R.id.nav_notification, R.id.nav_my_cart, R.id.nav_setting, R.id.nav_support)
                .setDrawerLayout(drawer)
                .build();

//        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener()
//        {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                int id = item.getItemId();
//
//              if (id==R.id.nav_my_account)
//              {
//
//                  Intent myIntent = new Intent(MainHomeActivity.this, UserProfileActivity.class);
//                  startActivity(myIntent);
//              }
//
//                return true;
//            }
//        });
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_home, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void cart(View view)
    {
        Intent myIntent = new Intent(MainHomeActivity.this, MyCartActivity.class);
                startActivity(myIntent);

    }
    private void account(View view)
    {
        Intent myIntent = new Intent(MainHomeActivity.this, UserProfileActivity.class);
        startActivity(myIntent);

    }
    private void setting(View view)
    {
        Intent myIntent = new Intent(MainHomeActivity.this, SettingActivity.class);
        startActivity(myIntent);

    }
    private void support(View view)
    {
        Intent myIntent = new Intent(MainHomeActivity.this, SupportActivity.class);
        startActivity(myIntent);

    }
}
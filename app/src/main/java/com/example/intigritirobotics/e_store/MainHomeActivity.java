package com.example.intigritirobotics.e_store;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Layout;
import android.view.MenuItem;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.example.intigritirobotics.R;
import com.example.intigritirobotics.e_store.ui.MyAccount.UserProfileActivity;
import com.example.intigritirobotics.e_store.ui.MyCart.MyCartActivity;
import com.example.intigritirobotics.e_store.ui.Setting.SettingActivity;
import com.example.intigritirobotics.e_store.ui.Support.SupportActivity;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.intigritirobotics.e_store.SignUpActivity.pref;

public class MainHomeActivity extends AppCompatActivity {

    private static final String TAG = "MainHomeActivity";
    private AppBarConfiguration mAppBarConfiguration;
    public FirebaseFirestore firebaseFirestore;
    public static Dialog HomeloadingDialog;
    public static String currentUserUId;
    public FirebaseAuth firebaseAuth;
    public static UserModel TheUser;
    private ImageView headerImg;
    private TextView headerText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        HomeloadingDialog = new Dialog(MainHomeActivity.this);
        HomeloadingDialog.setContentView(R.layout.loading_progress_dialog);
        HomeloadingDialog.setCancelable(false);
        HomeloadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        HomeloadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.border_background));

        HomeloadingDialog.show();

        setContentView(R.layout.activity_main_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        firebaseFirestore  =FirebaseFirestore.getInstance();

        firebaseAuth = FirebaseAuth.getInstance();
        currentUserUId = firebaseAuth.getUid();

        loadUserDetails();

        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);

        headerText = headerView.findViewById(R.id.nav_name);
        headerImg = headerView.findViewById(R.id.nav_pic);

        DrawerLayout drawer = findViewById(R.id.drawer);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_e_store, R.id.nav_category, R.id.nav_offer, R.id.nav_my_orders,
                R.id.nav_notification, R.id.nav_my_cart, R.id.nav_setting, R.id.nav_support)
                .setOpenableLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        headerImg.setOnClickListener(v->startActivity(new Intent(this,UserProfileActivity.class)));

    }

    private void loadUserDetails() {

        firebaseFirestore.document("USERS/" + currentUserUId).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                DocumentSnapshot userSnap = task.getResult();

                try{
                    assert userSnap != null;
                    String name =  Objects.requireNonNull(userSnap.get("User Name")).toString();
                    String address = Objects.requireNonNull(userSnap.get("Address")).toString();
                    String phone = Objects.requireNonNull(userSnap.get("Phone")).toString();
                    String pin = Objects.requireNonNull(userSnap.get("PIN")).toString();
                    String imgToken = userSnap.contains("Profile Image") ? Objects.requireNonNull(userSnap.get("Profile Image")).toString(): null;

                    if(name!=null && address!=null && phone !=null){
                        TheUser = new UserModel(name, address, phone, currentUserUId, pin);
                    }
                    headerText.setText(name);
                    if(imgToken != null) setImage(imgToken);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void setImage(String imgUrl) {
        FirebaseStorage storage = FirebaseStorage.getInstance();

        storage.getReference("/profileImg/"+imgUrl)
                .getDownloadUrl()
                .addOnSuccessListener(task -> Glide.with(this).load(task).into(headerImg))
                .addOnFailureListener(Throwable::printStackTrace);

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

    public void backToHome(MenuItem item) {
        startActivity(new Intent(this, MainHomeActivity.class));
        finish();
    }

    /*
    private void updateProds() {
        firebaseFirestore.collection("PRODUCTS").get().addOnSuccessListener(task->{

           for(DocumentSnapshot d : task.getDocuments()) {
               Map<String, Object> map = new HashMap<>();
               map.put("product_pic","https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcSvj_XtCdGmER6V7rg0CFvop3j1uTZ4yQ2vI3DyvVa7_nT862WFegNxBejZQMXS65ISN6ACb5U&usqp=CAc, https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcSvj_XtCdGmER6V7rg0CFvop3j1uTZ4yQ2vI3DyvVa7_nT862WFegNxBejZQMXS65ISN6ACb5U&usqp=CAc, https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcSvj_XtCdGmER6V7rg0CFvop3j1uTZ4yQ2vI3DyvVa7_nT862WFegNxBejZQMXS65ISN6ACb5U&usqp=CAc");
               firebaseFirestore.document("PRODUCTS/"+d.getId()).update(map);
           }

        });
    }

    private void addCoupons() {

        firebaseFirestore.collection("OFFERS").get().addOnSuccessListener(task -> {

            List<DocumentSnapshot> docList = task.getDocuments();
            Map<String, Object> coupons = new HashMap<>();

            for(int i=0; i<Math.min(docList.size(),4); i++) {
                coupons.put("Id",docList.get(i).getId());
                coupons.put("Expired", false);

                firebaseFirestore
                        .collection("USERS/"+ currentUserUId+"/My Offers")
                        .document(docList.get(i).getId()).set(coupons)
                        .addOnFailureListener(Throwable::printStackTrace);
            }

        }).addOnFailureListener(Throwable::printStackTrace);

    }
     */
}
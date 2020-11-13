package com.example.intigritirobotics;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static com.example.intigritirobotics.MainHomeActivity.firebaseFirestore;
import static com.example.intigritirobotics.MainHomeActivity.loadingDialog;

public class ProductDetailActivity extends AppCompatActivity {

    private final List<SlideModel> slideModelList = new ArrayList<>();
    public static FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseFirestore = FirebaseFirestore.getInstance();
        setContentView(R.layout.activity_product_detail);
        loadProductDetails();
    }

    private void prepareViewPager(ViewPager viewPager, ArrayList<String> arrayList, String details, String spec, String other) {
        TabLayoutAdapter adapter = new TabLayoutAdapter(getSupportFragmentManager());
        productDetailFragment fragment = new productDetailFragment();
        for(int i=0; i< arrayList.size(); i++) {
            Bundle bundle = new Bundle();
            if(i==0) bundle.putString("title",details);
            else if(i==1) bundle.putString("title", spec);
            else bundle.putString("title",other);
            fragment.setArguments(bundle);
            adapter.addFragment(fragment,arrayList.get(i));
            fragment = new productDetailFragment();
        }
        viewPager.setAdapter(adapter);
    }

    private void loadProductDetails() {
        String category_id = "1AcKQNSDSQqnpvA5e4vN"; // Replace with intent extras variables
        int product_index = 0;

        firebaseFirestore.collection("/CATEGORY/1AcKQNSDSQqnpvA5e4vN/products")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot doc = task.getResult().getDocuments().get(product_index);
                        loadDataToProduct(doc);
                    } else {
                        Log.w("Product", "Error getting documents.", task.getException());
                    }
                });

    }

    private void loadDataToProduct(DocumentSnapshot product) {
        String in_stock = product.get("in_stock").toString();
        if(in_stock.equals("false")){
            TextView inStockText = findViewById(R.id.inStockText);
            inStockText.setBackgroundColor(Color.RED);
            inStockText.setText("Out of Stock");
            Button buyNow = findViewById(R.id.buyNowButton);
            buyNow.setEnabled(false);
            Button addToCart = findViewById(R.id.addToCartButton);
            addToCart.setEnabled(false);
        }

        String image_url = product.get("product_pic").toString();
        ImageSlider imageSlider = findViewById(R.id.imgSlider);

        slideModelList.add(new SlideModel(image_url, ScaleTypes.FIT));
        imageSlider.setImageList(slideModelList);

        TextView briefText = findViewById(R.id.itemBriefDetail);
        briefText.setText(product.get("product_title").toString());

        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("Some Details");
        arrayList.add("Specs");
        arrayList.add("Other Details");

        String details = product.get("details").toString();
        String spec = product.get("specs").toString();
        String other = product.get("other").toString();

        TabLayout tabLayout = findViewById(R.id.detailTabs);
        ViewPager viewPager = findViewById(R.id.viewPager);

        prepareViewPager(viewPager, arrayList, details, spec, other);
        tabLayout.setupWithViewPager(viewPager);

        TextView price = findViewById(R.id.PriceText);
        String priceText = "Rs."+product.get("product_price").toString()+"/-";
        price.setText(priceText);
        // Set Ratings
        TextView avgRatingText = findViewById(R.id.averageRatingText);
        avgRatingText.setText(product.get("product_rating").toString());

        TextView totalRatings = findViewById(R.id.number_of_rating_text);
        totalRatings.setText(product.get("total_ratings").toString());
    }
}
package com.example.intigritirobotics.e_store.ui.E_Store;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.intigritirobotics.e_store.CategoryAdapter;
import com.example.intigritirobotics.e_store.CategoryModel;
import com.example.intigritirobotics.e_store.GridAdapter;
import com.example.intigritirobotics.e_store.HorizontalAdapter1;
import com.example.intigritirobotics.R;
import com.example.intigritirobotics.e_store.ViewAllActivity;
import com.example.intigritirobotics.e_store.ViewAllModel;
import com.example.intigritirobotics.e_store.ui.MyCart.MyCartActivity;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;

import static com.example.intigritirobotics.e_store.MainHomeActivity.HomeloadingDialog;
import static com.example.intigritirobotics.e_store.MainHomeActivity.currentUserUId;

public class EStoreFragment extends Fragment {

    private final List<CategoryModel> projectList = new ArrayList<>();
    private RecyclerView projectRecyclerView, horizontalItemsRecyclerview, gridRecView, cartRevView;
    private ImageSlider imageSlider;
    private final List<SlideModel> slideModels = new ArrayList<>();
    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private final List<ViewAllModel> horizontalList = new ArrayList<>();
    private LinearLayoutManager projectLinearLayoutManager, horizontalLinearLayoutManager;
    private GridLayoutManager gridLayoutManager, cartGridManager;
    private final List<ViewAllModel> gridList = new ArrayList<>(), cartList = new ArrayList<>();
    private CardView cartCard, bundleCard;

    SwipeRefreshLayout mSwipeRefreshLayout;
    ProgressDialog progressDialog ;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_e_store, container, false);
        HomeloadingDialog.show();

        mSwipeRefreshLayout = view.findViewById(R.id.e_store_swipe);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.gen_black);
        projectRecyclerView = view.findViewById(R.id.category_recyclerview);

        gridRecView = view.findViewById(R.id.product_grid_1);
        gridLayoutManager = new GridLayoutManager(getContext(), 2);

        cartGridManager = new GridLayoutManager(getContext(),2);
        cartRevView = view.findViewById(R.id.cart_grid);

        cartCard = view.findViewById(R.id.cart_card);
        bundleCard = view.findViewById(R.id.bundle_card);

        Button hor1ViewAllBtn = view.findViewById(R.id.Horizontal1_view_all_button);
        horizontalItemsRecyclerview = view.findViewById(R.id.horizontal_items_recyclerview);
        horizontalLinearLayoutManager = new LinearLayoutManager(getContext());
        projectLinearLayoutManager = new LinearLayoutManager(getContext());
        imageSlider = view.findViewById(R.id.image_slider);
        loadProject();

        hor1ViewAllBtn.setOnClickListener(view1 -> {
            Intent myIntent = new Intent(getContext(), ViewAllActivity.class);
            myIntent.putExtra("Index","1");
            myIntent.putExtra("Title","#Trendding");
            startActivity(myIntent);
        });
        mSwipeRefreshLayout.setOnRefreshListener(this::loadProject);

        //TextView viewAll;
        //viewAll = view.findViewById(R.id.view_all_bundles_text);
        // viewAll.setOnClickListener(v->startActivity(new Intent(getContext(), ViewAllActivity.class)));
        
        TextView gotoCart;
        gotoCart = view.findViewById(R.id.cart_grid_text);
        gotoCart.setOnClickListener(v->startActivity(new Intent(getContext(), MyCartActivity.class)));
        return view;

    }

    private void loadProject() {
        firebaseFirestore.collection("CATEGORY").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                projectList.clear();
                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                    projectList.add(new CategoryModel(
                            documentSnapshot.getId(),
                            documentSnapshot.get("category_pic").toString(),
                            documentSnapshot.get("category_title").toString()));
                }
                firebaseFirestore.collection("SLIDER").get().addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        slideModels.clear();
                        for (QueryDocumentSnapshot documentSnapshot : task1.getResult()) {
                            slideModels.add(new SlideModel(
                                    documentSnapshot.get("pic").toString(),
                                    ScaleTypes.FIT));
                        }
                        imageSlider.setImageList(slideModels);

                    }
                    else {
                        progressDialog.dismiss();
                        String error = task1.getException().getMessage();
                        Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(e -> {
                    e.printStackTrace();
                    progressDialog.dismiss();
                });

                projectLinearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
                projectRecyclerView.setLayoutManager(projectLinearLayoutManager);
                CategoryAdapter adapter = new CategoryAdapter(projectList);
                projectRecyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();

            } else {
                progressDialog.dismiss();
                String error = task.getException().getMessage();
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }

        });

        firebaseFirestore.collection("PRODUCTS").get().addOnCompleteListener(task2 -> {
            if (task2.isSuccessful()) {
                horizontalList.clear();

                List<DocumentSnapshot> list = task2.getResult().getDocuments();
                int n = Math.min(list.size(), 8);

                for (int i=0; i<n; i++) {
                    DocumentSnapshot documentSnapshot = list.get(i);
                    String id = documentSnapshot.getId();
                    String picUrl = documentSnapshot.get("product_pic").toString().split(", ")[0];
                    String title = documentSnapshot.get("product title").toString();
                    float rating = Float.parseFloat(String.valueOf(documentSnapshot.get("product rating")));
                    int price = Integer.parseInt(String.valueOf(documentSnapshot.get("product price")));
                    horizontalList.add(new ViewAllModel(id, picUrl, title, rating, price));
                }
            }
            else {
                progressDialog.dismiss();
                String error = task2.getException().getMessage();
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            e.printStackTrace();
            progressDialog.dismiss();

        });

        horizontalLinearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        horizontalItemsRecyclerview.setLayoutManager(horizontalLinearLayoutManager);
        HorizontalAdapter1 adapter1 = new HorizontalAdapter1(horizontalList);
        horizontalItemsRecyclerview.setAdapter(adapter1);
        adapter1.notifyDataSetChanged();
        mSwipeRefreshLayout.setRefreshing(false);
        HomeloadingDialog.dismiss();

        setUpGrid();
        setUpInYourCart();

    }

    private void setUpInYourCart() {
        firebaseFirestore.collection("USERS/"+currentUserUId+"/My Cart")
                .get()
                .addOnSuccessListener(task->{
                    if(task.size()>1) {

                        cartList.clear();

                        for (int i=0; i<2; i++) {
                            String prod_id = task.getDocuments().get(i).getId();

                            firebaseFirestore.document("PRODUCTS/"+prod_id).get().addOnSuccessListener(documentSnapshot -> {
                                String id = documentSnapshot.getId();
                                String picUrl = documentSnapshot.get("product_pic").toString().split(", ")[0];
                                String title = documentSnapshot.get("product title").toString();
                                float rating = Float.parseFloat(String.valueOf(documentSnapshot.get("product rating")));
                                int price = Integer.parseInt(String.valueOf(documentSnapshot.get("product price")));
                                cartList.add(new ViewAllModel(id, picUrl, title, rating, price));

                                cartRevView.setLayoutManager(cartGridManager);
                                GridAdapter gridAdapter = new GridAdapter(cartList);
                                cartRevView.setAdapter(gridAdapter);
                                gridAdapter.notifyDataSetChanged();
                            });

                        }

                        cartCard.setVisibility(View.VISIBLE);
                    }
                }
        ).addOnFailureListener(Throwable::printStackTrace);

    }

    void setUpGrid() {
        firebaseFirestore.collection("PRODUCTS").get().addOnCompleteListener(task -> {
            if (task.isSuccessful())
            {
                gridList.clear();
                QuerySnapshot prodListSnap = task.getResult();

                for (int i=0; i<4; i++) {
                    DocumentSnapshot documentSnapshot = prodListSnap.getDocuments().get(i);

                    String id = documentSnapshot.getId();
                    String picUrl = documentSnapshot.get("product_pic").toString().split(", ")[0];
                    String title = documentSnapshot.get("product title").toString();
                    float rating = Float.parseFloat(String.valueOf(documentSnapshot.get("product rating")));
                    int price = Integer.parseInt(String.valueOf(documentSnapshot.get("product price")));
                    gridList.add(new ViewAllModel(id, picUrl, title, rating, price));

                }

                gridRecView.setLayoutManager(gridLayoutManager);
                GridAdapter gridAdapter = new GridAdapter(gridList);
                gridRecView.setAdapter(gridAdapter);
                gridAdapter.notifyDataSetChanged();
                bundleCard.setVisibility(View.VISIBLE);
            }
            else
            {
                task.getException().printStackTrace();
            }

        });

    }

}
package com.example.intigritirobotics.e_store.ui.E_Store;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.intigritirobotics.e_store.CategoryAdapter;
import com.example.intigritirobotics.e_store.CategoryModel;
import com.example.intigritirobotics.e_store.HorizontalAdapter1;
import com.example.intigritirobotics.R;
import com.example.intigritirobotics.e_store.ViewAllActivity;
import com.example.intigritirobotics.e_store.ViewAllModel;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import static com.example.intigritirobotics.e_store.MainHomeActivity.firebaseFirestore;
import static com.example.intigritirobotics.e_store.MainHomeActivity.loadingDialog;

public class EStoreFragment extends Fragment {

    private List<CategoryModel> projectList = new ArrayList<>();
    private RecyclerView projectRecyclerView, horizontalItemsRecyclerview;
    private ImageSlider imageSlider;
    private List<SlideModel> slideModels = new ArrayList<>();
    private List<ViewAllModel> horizontalList = new ArrayList<>();
    private LinearLayoutManager projectLinearLayoutManager, horizontalLinearLayoutManager;
    private Button Hor1ViewAllBtn;
    SwipeRefreshLayout mSwipeRefreshLayout;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_e_store, container, false);

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.e_store_swipe);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.gen_black);
        projectRecyclerView = view.findViewById(R.id.category_recyclerview);
        Hor1ViewAllBtn = view.findViewById(R.id.Horizontal1_view_all_button);
        horizontalItemsRecyclerview = view.findViewById(R.id.horizontal_items_recyclerview);
        horizontalLinearLayoutManager = new LinearLayoutManager(getContext());
        projectLinearLayoutManager = new LinearLayoutManager(getContext());
        imageSlider = view.findViewById(R.id.image_slider);
        loadProject();

        Hor1ViewAllBtn.setOnClickListener(view1 -> {
            Intent myIntent = new Intent(getContext(), ViewAllActivity.class);
            startActivity(myIntent);
        });
        mSwipeRefreshLayout.setOnRefreshListener(this::loadProject);
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
                        loadingDialog.dismiss();
                        String error = task1.getException().getMessage();
                        Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(e -> {
                    loadingDialog.dismiss();

                });

                projectLinearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
                projectRecyclerView.setLayoutManager(projectLinearLayoutManager);
                CategoryAdapter adapter = new CategoryAdapter(projectList);
                projectRecyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();

            } else {
                loadingDialog.dismiss();
                String error = task.getException().getMessage();
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }

        });

        /////////////////////////////////////////////////////////////////// HORIZONTAL ///////////////////////////////////////////////////////

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
                loadingDialog.dismiss();
                String error = task2.getException().getMessage();
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            loadingDialog.dismiss();

        });

        horizontalLinearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        horizontalItemsRecyclerview.setLayoutManager(horizontalLinearLayoutManager);
        HorizontalAdapter1 adapter1 = new HorizontalAdapter1(horizontalList);
        horizontalItemsRecyclerview.setAdapter(adapter1);
        adapter1.notifyDataSetChanged();
        mSwipeRefreshLayout.setRefreshing(false);

    }

}
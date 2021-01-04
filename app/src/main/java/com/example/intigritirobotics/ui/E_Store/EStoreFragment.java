package com.example.intigritirobotics.ui.E_Store;

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

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.intigritirobotics.CategoryAdapter;
import com.example.intigritirobotics.CategoryModel;
import com.example.intigritirobotics.CheckOutActivity;
import com.example.intigritirobotics.HorizontalAdapter1;
import com.example.intigritirobotics.R;
import com.example.intigritirobotics.ViewAllActivity;
import com.example.intigritirobotics.ViewAllModel;
import com.example.intigritirobotics.ui.MyCart.MyCartActivity;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import static com.example.intigritirobotics.MainHomeActivity.firebaseFirestore;
import static com.example.intigritirobotics.MainHomeActivity.loadingDialog;

public class EStoreFragment extends Fragment {

    private List<CategoryModel> projectList = new ArrayList<>();
    private RecyclerView projectRecyclerView, horizontalItemsRecyclerview;
    private ImageSlider imageSlider;
    private List<SlideModel> slideModels = new ArrayList<>();
    private List<ViewAllModel> horizontalList = new ArrayList<>();
    private LinearLayoutManager projectLinearLayoutManager, horizontalLinearLayoutManager;
    public String categoryId;
    private Button Hor1ViewAllBtn;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_e_store, container, false);

        projectRecyclerView = view.findViewById(R.id.category_recyclerview);
        Hor1ViewAllBtn = view.findViewById(R.id.Horizontal1_view_all_button);
        horizontalItemsRecyclerview = view.findViewById(R.id.horizontal_items_recyclerview);
        horizontalLinearLayoutManager = new LinearLayoutManager(getContext());
        projectLinearLayoutManager = new LinearLayoutManager(getContext());
        imageSlider = view.findViewById(R.id.image_slider);
        loadProject();

        Hor1ViewAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(getContext(), ViewAllActivity.class);

                startActivity(myIntent);
            }
        });
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
            int i =1;
            if (task2.isSuccessful()) {
                horizontalList.clear();

                for (QueryDocumentSnapshot documentSnapshot : task2.getResult()) {

                    String id = documentSnapshot.getId();
                    String picUrl = documentSnapshot.get("product_pic").toString().split(", ")[0];
                    String title = documentSnapshot.get("product title").toString();
                    float rating = Float.parseFloat(String.valueOf(documentSnapshot.get("product rating")));
                    int price = Integer.parseInt(String.valueOf(documentSnapshot.get("product price")));
                    horizontalList.add(new ViewAllModel(id, picUrl, title, rating, price));
                    Toast.makeText(getContext(), ""+i, Toast.LENGTH_LONG).show();
                    i++;
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
        loadingDialog.dismiss();
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    }

}
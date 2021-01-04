package com.example.intigritirobotics.ui.Category;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.intigritirobotics.CategoryAdapter;
import com.example.intigritirobotics.CategoryModel;
import com.example.intigritirobotics.R;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.example.intigritirobotics.MainHomeActivity.firebaseFirestore;
import static com.example.intigritirobotics.MainHomeActivity.loadingDialog;

public class CategoryFragment extends Fragment {
    private final List<CategoryModel> projectList = new ArrayList<>();
    private RecyclerView recyclerView;
    private GridLayoutManager gridLayoutManager;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        
        View view = inflater.inflate(R.layout.fragment_category, container, false);

        recyclerView =view.findViewById(R.id.category_fragment_recyclerview);
        gridLayoutManager = new GridLayoutManager(getContext(), 2);

        loadCategory();
        return view;
    }

    private void loadCategory() {
        loadingDialog.show();
        firebaseFirestore.collection("CATEGORY").get().addOnCompleteListener(task -> {
            if (task.isSuccessful())
            {
                projectList.clear();
                for (QueryDocumentSnapshot documentSnapshot : Objects.requireNonNull(task.getResult())) {
                    projectList.add(new CategoryModel(
                            documentSnapshot.getId(),
                            Objects.requireNonNull(documentSnapshot.get("category_pic")).toString(),
                            Objects.requireNonNull(documentSnapshot.get("category_title")).toString()));
                }

                recyclerView.setLayoutManager(gridLayoutManager);
                CategoryFragmentAdapter adapter = new CategoryFragmentAdapter(projectList);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                loadingDialog.dismiss();
            }
            else
            {
                loadingDialog.dismiss();
                String error = Objects.requireNonNull(task.getException()).getMessage();
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }

        });

    }
}
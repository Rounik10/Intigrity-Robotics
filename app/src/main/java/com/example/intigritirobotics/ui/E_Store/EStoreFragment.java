package com.example.intigritirobotics.ui.E_Store;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.intigritirobotics.CategoryAdapter;
import com.example.intigritirobotics.CategoryModel;
import com.example.intigritirobotics.MainHomeActivity;
import com.example.intigritirobotics.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import static com.example.intigritirobotics.MainHomeActivity.firebaseFirestore;
import static com.example.intigritirobotics.MainHomeActivity.loadingDialog;

public class EStoreFragment extends Fragment {

    private List<CategoryModel> projectList = new ArrayList<>();
    private RecyclerView projectRecyclerView;
    private LinearLayoutManager projectLinearLayoutManager;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_e_store, container, false);

         projectRecyclerView = view.findViewById(R.id.category_recyclerview);
        projectLinearLayoutManager = new LinearLayoutManager(getContext());
        loadProject();
        return view;


       }

    private void loadProject()
    {
        firebaseFirestore.collection("CATEGORY").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    projectList.clear();
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        projectList.add(new CategoryModel(
                                documentSnapshot.get("index").toString(),
                                documentSnapshot.get("category_pic").toString(),
                                documentSnapshot.get("category_title").toString()));
                    }

                    projectLinearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
                    projectRecyclerView.setLayoutManager(projectLinearLayoutManager);
                    CategoryAdapter adapter = new CategoryAdapter(projectList);
                    projectRecyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    loadingDialog.dismiss();
                }
                else
                {
                    loadingDialog.dismiss();
                    String error = task.getException().getMessage();
                    Toast.makeText(getContext(),error,Toast.LENGTH_SHORT).show();
                }

            }
        });


    }



}
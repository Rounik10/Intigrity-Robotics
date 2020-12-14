package com.example.intigritirobotics.ui.MyOrders;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.intigritirobotics.CategoryModel;
import com.example.intigritirobotics.R;
import com.example.intigritirobotics.ui.Category.CategoryFragmentAdapter;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import static com.example.intigritirobotics.MainHomeActivity.firebaseFirestore;
import static com.example.intigritirobotics.MainHomeActivity.loadingDialog;

public class MyOrdersFragment extends Fragment {
    private List<MyOrderModel> myOrderModels = new ArrayList<>();
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_orders, container, false);
        recyclerView =view.findViewById(R.id.category_fragment_recyclerview);
        linearLayoutManager = new LinearLayoutManager(getContext());

        loadOrders();

        return view;
    }
    private void loadOrders() {
        firebaseFirestore.collection("PRODUCTS").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                myOrderModels.clear();
                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                    myOrderModels.add(new MyOrderModel(
                            documentSnapshot.get("order id").toString(),
                            documentSnapshot.get("order date").toString(),
                            documentSnapshot.get("productQsIds").toString(),
                            documentSnapshot.get("order status").toString()));
                }


                linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
                recyclerView.setLayoutManager(linearLayoutManager);
                MyOrderAdapter adapter = new MyOrderAdapter(myOrderModels);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();

            } else {
                loadingDialog.dismiss();
                String error = task.getException().getMessage();
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }

        });

    }

}
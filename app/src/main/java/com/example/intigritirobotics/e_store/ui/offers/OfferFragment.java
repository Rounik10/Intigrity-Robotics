package com.example.intigritirobotics.e_store.ui.offers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.intigritirobotics.R;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import static com.example.intigritirobotics.e_store.MainHomeActivity.HomeloadingDialog;
import static com.example.intigritirobotics.e_store.MainHomeActivity.firebaseFirestore;

public class OfferFragment extends Fragment {

    private List<OfferViewModel> offerslist = new ArrayList<>();
    private RecyclerView offerRecyclerView;
    private LinearLayoutManager offerLayoutManager;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_offers, container, false);
        HomeloadingDialog.show();

        offerRecyclerView = view.findViewById(R.id.offer_recyclerview);
        offerLayoutManager = new LinearLayoutManager(getContext());
        loadOffer();
        return view;

    }

    private void loadOffer() {
        firebaseFirestore.collection("OFFERS").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                offerslist.clear();
                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                    offerslist.add(new OfferViewModel(
                            documentSnapshot.get("Banner").toString(),
                            documentSnapshot.get("Id").toString(),
                            (Boolean) documentSnapshot.get("Expired")));

                }
                offerLayoutManager.setOrientation(RecyclerView.VERTICAL);
                offerRecyclerView.setLayoutManager(offerLayoutManager);
                OfferAdapter adapter = new OfferAdapter(offerslist);
                offerRecyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                HomeloadingDialog.dismiss();


            } else {
                String error = task.getException().getMessage();
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                HomeloadingDialog.dismiss();

            }
        });
    }
}
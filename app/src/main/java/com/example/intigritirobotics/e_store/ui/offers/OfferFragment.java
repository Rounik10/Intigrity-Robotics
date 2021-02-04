package com.example.intigritirobotics.e_store.ui.offers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.intigritirobotics.R;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.example.intigritirobotics.e_store.MainHomeActivity.HomeloadingDialog;
import static com.example.intigritirobotics.e_store.MainHomeActivity.currentUserUId;
import static com.example.intigritirobotics.e_store.MainHomeActivity.firebaseFirestore;

public class OfferFragment extends Fragment {

    private final List<OfferViewModel> offersList = new ArrayList<>();
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
        firebaseFirestore.collection("USERS/"+currentUserUId+"/My Offers").get().addOnSuccessListener(task1 -> {

            offersList.clear();
            List<DocumentSnapshot> offerList = task1.getDocuments();

            for(DocumentSnapshot offer: offerList) {
                boolean expired = Objects.equals(offer.get("Expired"), true);
                String id = Objects.requireNonNull(offer.getId());

                firebaseFirestore.document("OFFERS/"+id).get()
                        .addOnCompleteListener(task->{
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();

                        assert documentSnapshot != null;
                        offersList.add(new OfferViewModel(
                                Objects.requireNonNull(documentSnapshot.get("Banner")).toString(),
                                id,
                                expired));

                        offerLayoutManager.setOrientation(RecyclerView.VERTICAL);
                        offerRecyclerView.setLayoutManager(offerLayoutManager);
                        OfferAdapter adapter = new OfferAdapter(offersList);
                        offerRecyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        HomeloadingDialog.dismiss();

                    }
                });

            }

        });
    }

}
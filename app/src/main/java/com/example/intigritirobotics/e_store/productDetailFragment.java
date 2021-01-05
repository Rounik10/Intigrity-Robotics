package com.example.intigritirobotics.e_store;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.intigritirobotics.R;

public class productDetailFragment extends Fragment {

    public productDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_produt_detail, container,false);

        TextView textView = view.findViewById(R.id.detailText);

        String sTitle = getArguments().getString("title");

        textView.setText(sTitle);
        return view;
    }
}
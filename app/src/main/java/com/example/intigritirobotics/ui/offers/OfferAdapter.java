package com.example.intigritirobotics.ui.offers;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.intigritirobotics.MainHomeActivity;
import com.example.intigritirobotics.R;
import com.example.intigritirobotics.ui.offers.OfferViewModel;

import java.util.List;

import static android.content.Context.CLIPBOARD_SERVICE;

public class OfferAdapter extends RecyclerView.Adapter<OfferAdapter.ViewHolder> {

    private List<OfferViewModel> offerViewModelList;

    public OfferAdapter(List<OfferViewModel> offerViewModelList) {
        this.offerViewModelList = offerViewModelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.coupon_item_layout,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        String ID = offerViewModelList.get(position).getOfferID();
        String BANNER = offerViewModelList.get(position).getBanner();
        boolean EXPIRED = offerViewModelList.get(position).isExpired();
        viewHolder.setData(ID,BANNER,EXPIRED);
    }

    @Override
    public int getItemCount() {
        return offerViewModelList.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView Banner;
        private Button Use;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Banner = itemView.findViewById(R.id.offer_banner);
            Use = itemView.findViewById(R.id.offer_use_button);


        }

        private void  setData(String ID, String BANNER, boolean EXPIRED)
        {
            Glide.with(itemView.getContext()).load(BANNER).apply(new RequestOptions().placeholder(R.drawable.banner1)).into(Banner);

            if(EXPIRED)
            {
               Use.setBackgroundTintList(itemView.getResources().getColorStateList(R.color.grey_light));
               Use.setText("EXPIRED");
               Use.setTextColor(itemView.getResources().getColorStateList(R.color.dot_dark_screen2));
            }
            Use.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ClipboardManager clipboard = (ClipboardManager) itemView.getContext().getSystemService(CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("Coupon", ID);
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(itemView.getContext(), "Coupon copied to clip board", Toast.LENGTH_SHORT).show();

                }
            });
        }
    }

}

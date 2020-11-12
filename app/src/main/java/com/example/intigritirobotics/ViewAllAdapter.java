package com.example.intigritirobotics;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class ViewAllAdapter extends RecyclerView.Adapter<ViewAllAdapter.ViewHolder>{
    private List<ViewAllModel> viewAllModelList;

    public ViewAllAdapter(List<ViewAllModel> viewAllModelList) {
        this.viewAllModelList = viewAllModelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.product_preview_layout,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        String id = viewAllModelList.get(position).getId();
        String resource = viewAllModelList.get(position).getImage();
        String title = viewAllModelList.get(position).getTitle();
        int price = viewAllModelList.get(position).getFinalPrice();
        int rating = viewAllModelList.get(position).getTotalRating();
        viewHolder.setData(id,resource,title,price,rating);
    }



    @Override
    public int getItemCount() {
        return viewAllModelList.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView pic;
        private TextView ProductTitle;
        private TextView Price;
        private TextView Rating;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            pic = itemView.findViewById(R.id.product_preview_pic);
            ProductTitle = itemView.findViewById(R.id.product_preview_title);
            Price = itemView.findViewById(R.id.product_preview_price);
            Rating = itemView.findViewById(R.id.product_preview_rating);


        }

        private void  setData(String id,String resource,String title,int price,int rating)
        {
            Glide.with(itemView.getContext()).load(resource).apply(new RequestOptions().placeholder(R.drawable.category_icon)).into(pic);
            ProductTitle.setText(title);
            Price.setText(price);
            Rating.setText(rating);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent( itemView.getContext(),ViewAllActivity.class);
                    intent.putExtra("ProductId",id);
                    itemView.getContext().startActivity(intent);
                }
            });

        }


    }

}

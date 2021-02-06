package com.example.intigritirobotics.e_store;

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
import com.example.intigritirobotics.R;
import java.util.List;

public class GridAdapter extends RecyclerView.Adapter<GridAdapter.ViewHolder> {

    private final List<ViewAllModel> ViewAllModelList;

    public GridAdapter(List<ViewAllModel> ViewAllModelList) {
        this.ViewAllModelList = ViewAllModelList;
    }

    @NonNull
    @Override
    public GridAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.grid_item_model,viewGroup,false);
        return new GridAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GridAdapter.ViewHolder viewHolder, int position) {
        String index = ViewAllModelList.get(position).getId();
        String resource = ViewAllModelList.get(position).getImage();
        String title = ViewAllModelList.get(position).getTitle();
        int price = ViewAllModelList.get(position).getFinalPrice();
        float rating = ViewAllModelList.get(position).getTotalRating();
        viewHolder.setData(index,resource,title, price, rating);
    }

    @Override
    public int getItemCount() {
        return ViewAllModelList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView pic;
        private final TextView text;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            pic = itemView.findViewById(R.id.prod_img);
            text = itemView.findViewById(R.id.prod_text);
        }
        private void setData( final String index, String resource, String title, int price, float rating)
        {
            Glide.with(itemView.getContext()).load(resource).apply(new RequestOptions()
                    .placeholder(R.drawable.category_icon))
                    .into(pic);
            String display_title = (title.length() < 20) ? title : title.substring(0,20)+"...";
            text.setText(display_title);

            itemView.setOnClickListener(v -> {
                Intent intent = new Intent( itemView.getContext(),ProductDetailActivity.class);
                intent.putExtra("Index",index);
                intent.putExtra("Title",display_title);
                intent.putExtra("ID", index);
                intent.putExtra("Price",""+price);
                intent.putExtra("Rating",""+rating);
                itemView.getContext().startActivity(intent);
            });

        }

    }
}

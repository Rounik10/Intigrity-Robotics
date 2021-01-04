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

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private List<CategoryModel> categoryModelList;

    public CategoryAdapter(List<CategoryModel> categoryModelList) {
            this.categoryModelList = categoryModelList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.category_item_layout,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        String index = categoryModelList.get(position).getIndex();
        String resource = categoryModelList.get(position).getImage();
        String title = categoryModelList.get(position).getTitle();
        viewHolder.setData(index,resource,title);

    }

    @Override
    public int getItemCount() {
        return categoryModelList.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView pic;
        private TextView CategoryTitle;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            pic = itemView.findViewById(R.id.category_pic);
            CategoryTitle = itemView.findViewById(R.id.category_title);

        }

        private void  setData( final String index, String resource, String title)
        {
           Glide.with(itemView.getContext()).load(resource).apply(new RequestOptions().placeholder(R.drawable.category_icon)).into(pic);
            CategoryTitle.setText(title);

             itemView.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     Intent intent = new Intent( itemView.getContext(),ViewAllActivity.class);
                     intent.putExtra("Index",index);
                     intent.putExtra("Title",title);
                     itemView.getContext().startActivity(intent);
                 }
             });

        }

    }

}

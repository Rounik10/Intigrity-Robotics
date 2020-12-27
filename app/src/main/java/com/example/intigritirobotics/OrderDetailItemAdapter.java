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

public class OrderDetailItemAdapter extends RecyclerView.Adapter<OrderDetailItemAdapter.ViewHolder> {

    private List<OrderDetailItemsModel>OrderDetailItemAdapterList;

    public OrderDetailItemAdapter(List<OrderDetailItemsModel> orderDetailItemAdapterList) {
        OrderDetailItemAdapterList = orderDetailItemAdapterList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.ordered_items_layout,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        String ID = OrderDetailItemAdapterList.get(position).getProductID();
        String Price = OrderDetailItemAdapterList.get(position).getProductPrice();
        String Qty = OrderDetailItemAdapterList.get(position).getProductQty();
        String  Rating = OrderDetailItemAdapterList.get(position).getProductRating();
        viewHolder.setData(ID, Price, Qty, Rating);
    }

    @Override
    public int getItemCount() {
        return OrderDetailItemAdapterList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView pic;
        private TextView title;
        private TextView qty;
        private TextView price;
        private TextView ProductRating;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            pic = itemView.findViewById(R.id.horizontal_product_preview_pic);
            title = itemView.findViewById(R.id.horizontal_product_preview_title);
            ProductRating = itemView.findViewById(R.id.horizontal_product_preview_rating);
            price = itemView.findViewById(R.id.horizontal_product_preview_price);
        }
        private void  setData(String  ID,String Price, String Qty, String Rating)
        {
            //Glide.with(itemView.getContext()).load(resource).apply(new RequestOptions().placeholder(R.drawable.category_icon)).into(pic);
            String priceText = "Rs." + price +"/-";

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent( itemView.getContext(),ProductDetailActivity.class);
                    intent.putExtra("ID", ID);
                    intent.putExtra("Price",""+price);
                    itemView.getContext().startActivity(intent);
                }
            });

        }

    }

}
package com.example.intigritirobotics;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.RawRes;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Objects;

public class OrderDetailItemAdapter extends RecyclerView.Adapter<OrderDetailItemAdapter.ODIViewHolder> {

    private List<OrderDetailItemsModel>OrderDetailItemAdapterList;

    public OrderDetailItemAdapter(List<OrderDetailItemsModel> orderDetailItemAdapterList) {
        OrderDetailItemAdapterList = orderDetailItemAdapterList;
    }

    @NonNull
    @Override
    public ODIViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.ordered_items_layout,viewGroup,false);
        return new ODIViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ODIViewHolder viewHolder, int position) {
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

    public class ODIViewHolder extends RecyclerView.ViewHolder {

        private ImageView pic;
        private TextView title;
        private TextView qty;
        private TextView price;
        private RatingBar ProductRating;

        public ODIViewHolder(@NonNull View itemView) {
            super(itemView);
            pic = itemView.findViewById(R.id.order_detail_product_pic);
            title = itemView.findViewById(R.id.order_detail_product_title);
            ProductRating = itemView.findViewById(R.id.order_detail_product_ratingBar);
            price = itemView.findViewById(R.id.order_detail_product_price);
            qty = itemView.findViewById(R.id.order_detail_product_qty);
        }
        private void  setData(String  ID,String Price, String Qty, String Rating)
        {
            Log.d("Dikkat: OrderAdapter", "null hai " + (title == null));

            FirebaseFirestore.getInstance().document("/PRODUCTS/"+ID).get().addOnCompleteListener(task -> {
               if(task.isSuccessful()) {
                   DocumentSnapshot prodSnap = task.getResult();
                   assert prodSnap != null;

                   Glide.with(itemView.getContext())
                           .load(prodSnap.get("product_pic").toString().split(", ")[0])
                           .placeholder(R.drawable.category_icon)
                           .into(pic);

                   String titleText = Objects.requireNonNull(prodSnap.get("product title")).toString();
                   title.setText(titleText);
                   String qtyText = "Qty: "+Qty;
                   qty.setText(qtyText);
                   String priceText = "Rs."+Price+"/-";
                   price.setText(priceText);
                   ProductRating.setRating(Float.parseFloat(Rating));
               }
            });

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
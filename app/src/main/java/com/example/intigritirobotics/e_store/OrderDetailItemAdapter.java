package com.example.intigritirobotics.e_store;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.intigritirobotics.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.example.intigritirobotics.e_store.MainHomeActivity.currentUserUId;
import static com.example.intigritirobotics.e_store.MainHomeActivity.firebaseFirestore;

public class OrderDetailItemAdapter extends RecyclerView.Adapter<OrderDetailItemAdapter.ODIViewHolder> {

    private final List<OrderDetailItemsModel>OrderDetailItemAdapterList;
    public int is_app_starting = 0;

    public OrderDetailItemAdapter(List<OrderDetailItemsModel> orderDetailItemAdapterList) {
        OrderDetailItemAdapterList = orderDetailItemAdapterList;
        is_app_starting++;
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

    public static class ODIViewHolder extends RecyclerView.ViewHolder {

        private final ImageView pic;
        private final TextView title;
        private final TextView qty;
        private final TextView price;
        private final RatingBar ProductRating;
        private int is_app_starting = 0;

        public ODIViewHolder(@NonNull View itemView) {
            super(itemView);
            pic = itemView.findViewById(R.id.order_detail_product_pic);
            title = itemView.findViewById(R.id.order_detail_product_title);
            ProductRating = itemView.findViewById(R.id.order_detail_product_ratingBar);
            ProductRating.setClickable(false);
            price = itemView.findViewById(R.id.order_detail_product_price);
            qty = itemView.findViewById(R.id.order_detail_product_qty);
        }

        private void  setData(String  ID,String Price, String Qty, String Rating)
        {
            FirebaseFirestore.getInstance().document("/PRODUCTS/"+ID).get().addOnCompleteListener(task -> {
               if(task.isSuccessful()) {
                   DocumentSnapshot prodSnap = task.getResult();
                   assert prodSnap != null;

                   Glide.with(itemView.getContext())
                           .load(Objects.requireNonNull(prodSnap.get("product_pic")).toString().split(", ")[0])
                           .placeholder(R.drawable.category_icon)
                           .into(pic);

                   String titleText = Objects.requireNonNull(prodSnap.get("product title")).toString();
                   assert title != null;

                   title.setText(titleText);
                   String qtyText = "Qty: "+Qty;
                   qty.setText(qtyText);
                   String priceText = "Rs."+Price+"/-";
                   price.setText(priceText);
                   ProductRating.setRating(Float.parseFloat(Rating));
                   ProductRating.setEnabled(false);
                   is_app_starting++;
               }
            });
/*
            ProductRating.setOnRatingBarChangeListener((ratingBar, v, b) -> {

                is_app_starting++;

                Map<String, String> userRatingMap = new HashMap<>();
                userRatingMap.put("Rating", "" + v);

                Map<String, Object> productUpdateMap = new HashMap<>();

                DocumentReference productRef = firebaseFirestore.document("PRODUCTS/" + ID);

                productRef.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful() && is_app_starting > 2) {
                        DocumentSnapshot prodSnap = task.getResult();
                        assert prodSnap != null;
                        String s = Objects.requireNonNull(prodSnap.get((v + "").substring(0, 1) + "_star")).toString();

                        int x = s==null ? 0 : Integer.parseInt(s);

                        firebaseFirestore
                                .document("USERS/"+currentUserUId+"/My Ratings/"+ID)
                                .get()
                                .addOnCompleteListener(task1 -> {
                                    if(task1.isSuccessful()) {

                                        if(Objects.requireNonNull(task1.getResult()).exists()) {
                                            String prevRating = Objects.requireNonNull(task1.getResult().get("Rating")).toString();
                                            int pre_num = Integer.parseInt(Objects.requireNonNull(prodSnap.get(prevRating.substring(0, 1) + "_star")).toString());

                                            Log.d("Prev num is: ",""+pre_num);

                                            productUpdateMap.put(prevRating.substring(0,1)+"_star", (pre_num-1)+"");
                                        }

                                        firebaseFirestore.document("USERS/" + currentUserUId + "/My Ratings/" + ID).set(userRatingMap);
                                        productUpdateMap.put((int)v+"_star", x+1+"");

                                        Log.d("Map Me kya hai",productUpdateMap.keySet().toString());
                                        Log.d("Map Values", productUpdateMap.values().toString());

                                        productUpdateMap.put("product rating", getAvg(prodSnap));
                                        productRef.update(productUpdateMap);
                                    }
                                });

                    }
                });

            });
*/
            itemView.setOnClickListener(v -> {
                Intent intent = new Intent( itemView.getContext(),ProductDetailActivity.class);
                intent.putExtra("ID", ID);
                intent.putExtra("Price",""+price.getText().subSequence(3,price.getText().length()-2));
                intent.putExtra("Title", title.getText());
                itemView.getContext().startActivity(intent);
            });

        }

        private String getAvg(DocumentSnapshot prodSnap) {
            float sum = 0, temp;
            int total = 0;

            for (int i = 1; i <= 5; i++) {
                temp = Integer.parseInt(Objects.requireNonNull(prodSnap.get(i + "_star")).toString());
                sum += i * temp;
                total += temp;
            }

            String average = "" + sum / total;
            if (average.length() > 3) average = average.substring(0, 3);

            return average;
        }

    }

}
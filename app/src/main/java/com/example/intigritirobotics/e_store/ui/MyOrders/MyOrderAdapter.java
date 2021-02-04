package com.example.intigritirobotics.e_store.ui.MyOrders;

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
import com.example.intigritirobotics.e_store.MyOrderDetailActivity;
import com.example.intigritirobotics.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.List;

public class MyOrderAdapter extends RecyclerView.Adapter<MyOrderAdapter.ViewHolder> {

    private final List<MyOrderModel> myOrderModelList;
    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    public MyOrderAdapter(List<MyOrderModel> myOrderModelList) {
        this.myOrderModelList = myOrderModelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.my_order_item_layout,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        String orderId = myOrderModelList.get(position).getOrderID();
        String orderDate = myOrderModelList.get(position).getOrderDate();
        String productID = myOrderModelList.get(position).getProductID();
        String productStatus = myOrderModelList.get(position).getProductStatus();

        viewHolder.setData(orderId,orderDate,productID, productStatus);
    }

    @Override
    public int getItemCount() {
        return myOrderModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView pic;
        private final TextView ProductTitle,OrderId, OrderDate,OrderStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            pic = itemView.findViewById(R.id.my_order_item_layout_pic);
            ProductTitle = itemView.findViewById(R.id.my_order_item_layout_title);
            OrderDate = itemView.findViewById(R.id.my_order_item_layout_order_date);
            OrderId = itemView.findViewById(R.id.my_order_item_layout_order_id);
            OrderStatus = itemView.findViewById(R.id.my_order_item_layout_status);

        }

        private void  setData( String orderId, String orderDate, String productId, String productStatus)
        {
            firebaseFirestore.document("PRODUCTS/"+productId.split(", ")[0]).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful())
                        {
                            DocumentSnapshot queryDocumentSnapshots = task.getResult();
                            Glide.with(itemView.getContext())
                                    .load(queryDocumentSnapshots
                                            .get("product_pic")
                                            .toString()
                                            .split(", ")[0])
                                    .apply(new RequestOptions().placeholder(R.drawable.category_icon))
                                    .into(pic);
                            ProductTitle.setText(queryDocumentSnapshots.get("product title").toString());
                        }
                    }).addOnFailureListener(Throwable::printStackTrace);
            OrderDate.setText(orderDate);
            OrderId.setText(orderId);
            OrderStatus.setText(productStatus);


            itemView.setOnClickListener(v -> {
                Intent intent = new Intent( itemView.getContext(), MyOrderDetailActivity.class);
                MyOrderModel clickedOrder = myOrderModelList.get(getAdapterPosition());

                intent.putExtra("date", clickedOrder.getOrderDate());
                intent.putExtra("order id", clickedOrder.getOrderID());
                intent.putExtra("productId", clickedOrder.getProductID());
                intent.putExtra("status", clickedOrder.getProductStatus());
                intent.putExtra("product qty", clickedOrder.getProductQty());
                intent.putExtra("product price", clickedOrder.getProductPrices());

                itemView.getContext().startActivity(intent);
            });

        }

    }

}